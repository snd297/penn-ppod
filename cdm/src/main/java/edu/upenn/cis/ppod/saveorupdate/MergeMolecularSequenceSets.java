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
package edu.upenn.cis.ppod.saveorupdate;

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
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;

/**
 * @author Sam Donnelly
 */
final class MergeMolecularSequenceSets<SS extends SequenceSet<S>, S extends Sequence>
		implements IMergeMolecularSequenceSets<SS, S> {

	private final IDAO<Object, Long> dao;
	private final Provider<S> sequenceProvider;
	private final INewPPodVersionInfo newPPodVersionInfo;

	@Inject
	MergeMolecularSequenceSets(final Provider<S> sequenceProvider,
			@Assisted IDAO<Object, Long> dao,
			@Assisted INewPPodVersionInfo newPPodVersionInfo) {
		this.sequenceProvider = sequenceProvider;
		this.dao = dao;
		this.newPPodVersionInfo = newPPodVersionInfo;
	}

	public void merge(final SS targetSequenceSet, final SS sourceSequenceSet) {
		checkNotNull(targetSequenceSet);
		checkNotNull(sourceSequenceSet);
		targetSequenceSet.setLabel(sourceSequenceSet.getLabel());
		dao.saveOrUpdate(targetSequenceSet);

		final Integer targetSequenceSetLengths = targetSequenceSet
				.getSequenceLengths();

		final Integer sourceSequenceSetLengths = sourceSequenceSet
				.getSequenceLengths();

		Map<OTU, S> targetOTUsToSequences;

		if (targetSequenceSetLengths == null ||
				targetSequenceSetLengths.equals(sourceSequenceSetLengths)) {
			// We don't need to clear it since it's either empty or the
			// sequences are already of the correct size, so we grab the
			// OTU->Sequence
			// map
			targetOTUsToSequences = targetSequenceSet
					.getOTUsToSequencesMap();
		} else {
			// We need to clear it because it's of the wrong size, so we make a
			// copy of the OTU->Sequence map
			targetOTUsToSequences = ImmutableMap.copyOf(targetSequenceSet
					.getOTUsToSequencesMap());
			targetSequenceSet.clearSequences();
		}

		for (int i = 0; i < sourceSequenceSet.getOTUSet().getOTUs().size(); i++) {
			final OTU sourceOTU = sourceSequenceSet.getOTUSet().getOTU(i);

			final S sourceSequence = sourceSequenceSet.getSequence(sourceOTU);
			final OTU targetOTU = targetSequenceSet.getOTUSet().getOTU(i);

			S targetSequence;

			if (null == (targetSequence = targetOTUsToSequences.get(targetOTU))) {
				targetSequence = sequenceProvider.get();
				targetSequence.setPPodVersionInfo(newPPodVersionInfo
						.getNewPPodVersionInfo());
			}
			targetSequence.setSequence(sourceSequence.getSequence());
			targetSequenceSet.putSequence(targetOTU, targetSequence);

			targetSequence.setName(sourceSequence.getName());
			targetSequence.setDescription(sourceSequence.getDescription());
			targetSequence.setAccession(sourceSequence.getAccession());

			dao.saveOrUpdate(targetSequence);

			if (targetSequence.isInNeedOfNewPPodVersionInfo()) {
				targetSequence.setPPodVersionInfo(newPPodVersionInfo
						.getNewPPodVersionInfo());
			}

			dao.flush();
			dao.evict(targetSequence);
		}
	}
}
