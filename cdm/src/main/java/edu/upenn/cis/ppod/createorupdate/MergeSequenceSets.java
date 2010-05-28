/*
 * Copyright (C) 2010 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.createorupdate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.Sequence;
import edu.upenn.cis.ppod.model.SequenceSet;
import edu.upenn.cis.ppod.modelinterfaces.INewVersionInfo;

final class MergeSequenceSets<SS extends SequenceSet<S>, S extends Sequence>
		implements IMergeSequenceSets<SS, S> {

	private final IDAO<Object, Long> dao;
	private final Provider<S> sequenceProvider;
	private final INewVersionInfo newVersionInfo;

	@Inject
	MergeSequenceSets(
			final Provider<S> sequenceProvider,
			@Assisted IDAO<Object, Long> dao,
			@Assisted INewVersionInfo newVersionInfo) {
		this.sequenceProvider = sequenceProvider;
		this.dao = dao;
		this.newVersionInfo = newVersionInfo;
	}

	public void mergeSequenceSets(
			final SS trgSeqSet,
			final SS srcSeqSet) {
		checkNotNull(trgSeqSet);
		checkArgument(
				trgSeqSet.getOTUSet() != null,
				"trgSeqSet does not belong to an OTUSet");

		checkNotNull(srcSeqSet);
		checkArgument(
				srcSeqSet.getOTUSet() != null,
				"srcSeqSet does not belong to an OTUSet");

		trgSeqSet.setLabel(srcSeqSet.getLabel());

		dao.makePersistent(trgSeqSet);

		final Integer trgSeqSetLengths =
				trgSeqSet.getSequenceLengths();

		final Integer srcSeqSetLengths =
				srcSeqSet.getSequenceLengths();

		Map<OTU, S> trgOTUsToSeqs;

		if (trgSeqSetLengths == null ||
				trgSeqSetLengths.equals(srcSeqSetLengths)) {
			// We don't need to clear it since it's either empty or the
			// sequences are already of the correct size, so we grab the
			// OTU->Sequence
			// map
			trgOTUsToSeqs =
					trgSeqSet.getSequences();
		} else {
			// We need to clear it because it's of the wrong size, so we make a
			// copy of the OTU->Sequence map
			trgOTUsToSeqs =
					ImmutableMap.copyOf(
							trgSeqSet.getSequences());
			trgSeqSet.clearSequences();
		}

		for (int i = 0; i < srcSeqSet.getOTUSet().getOTUs().size(); i++) {
			final OTU sourceOTU =
					srcSeqSet.getOTUSet()
							.getOTUs()
							.get(i);

			final S srcSeq = srcSeqSet.getSequence(sourceOTU);
			final OTU trgOTU =
					trgSeqSet
							.getOTUSet()
							.getOTUs()
							.get(i);

			S trgSeq;

			if (null == (trgSeq =
					trgOTUsToSeqs.get(trgOTU))) {
				trgSeq = sequenceProvider.get();
				trgSeq.setVersionInfo(newVersionInfo
						.getNewVersionInfo());
			}
			trgSeq.setSequence(srcSeq.getSequence());
			trgSeqSet.putSequence(trgOTU, trgSeq);

			trgSeq.setName(srcSeq.getName());
			trgSeq.setDescription(srcSeq.getDescription());
			trgSeq.setAccession(srcSeq.getAccession());

			dao.makePersistent(trgSeq);

			if (trgSeq.isInNeedOfNewVersion()) {
				trgSeq
						.setVersionInfo(
								newVersionInfo.getNewVersionInfo());
			}

			dao.flush();
			dao.evict(trgSeq);
		}
	}
}
