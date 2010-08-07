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

import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.Sequence;
import edu.upenn.cis.ppod.model.SequenceSet;

final class MergeSequenceSets<SS extends SequenceSet<S>, S extends Sequence<?>>
		implements IMergeSequenceSets<SS, S> {

	private final Provider<S> sequenceProvider;
	private final INewVersionInfo newVersionInfo;

	@Inject
	MergeSequenceSets(
			final Provider<S> sequenceProvider,
			@Assisted INewVersionInfo newVersionInfo) {
		this.sequenceProvider = sequenceProvider;
		this.newVersionInfo = newVersionInfo;
	}

	public void mergeSequenceSets(
			final SS targSeqSet,
			final SS srcSeqSet) {
		checkNotNull(targSeqSet);
		checkArgument(
				targSeqSet.getParent() != null,
				"targSeqSet does not belong to an OTUSet");

		checkNotNull(srcSeqSet);
		checkArgument(
				srcSeqSet.getParent() != null,
				"srcSeqSet does not belong to an OTUSet");

		targSeqSet.setLabel(srcSeqSet.getLabel());

		final Integer targSeqSetLengths =
				targSeqSet.getSequenceLengths();

		final Integer srcSeqSetLengths =
				srcSeqSet.getSequenceLengths();

		Map<IOTU, S> targOTUsToSeqs;

		if (targSeqSetLengths == null ||
				targSeqSetLengths.equals(srcSeqSetLengths)) {
			// We don't need to clear it since it's either empty or the
			// sequences are already of the correct size, so we grab the
			// OTU->Sequence
			// map
			targOTUsToSeqs =
					targSeqSet.getSequences();
		} else {
			// We need to clear it because it's of the wrong size, so we make a
			// copy of the OTU->Sequence map
			targOTUsToSeqs =
					ImmutableMap.copyOf(
							targSeqSet.getSequences());
			targSeqSet.clearSequences();
		}

		for (int i = 0; i < srcSeqSet.getParent().getOTUs().size(); i++) {
			final IOTU sourceOTU =
					srcSeqSet.getParent()
							.getOTUs()
							.get(i);

			final S srcSeq = srcSeqSet.getSequence(sourceOTU);
			final IOTU targOTU = targSeqSet.getParent()
							.getOTUs()
							.get(i);

			S targSeq;

			if (null == (targSeq =
					targOTUsToSeqs.get(targOTU))) {
				targSeq = sequenceProvider.get();
				targSeq.setVersionInfo(newVersionInfo
						.getNewVersionInfo());
			}
			targSeq.setSequence(srcSeq.getSequence());
			targSeqSet.putSequence(targOTU, targSeq);

			targSeq.setName(srcSeq.getName());
			targSeq.setDescription(srcSeq.getDescription());
			targSeq.setAccession(srcSeq.getAccession());
		}
	}
}
