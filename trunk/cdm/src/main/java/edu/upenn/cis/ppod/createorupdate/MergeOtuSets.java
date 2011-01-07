/*
 * Copyright (C) 2009 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.createorupdate;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayListWithCapacity;

import java.util.List;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.domain.IHasPPodId;
import edu.upenn.cis.ppod.domain.PPodOtu;
import edu.upenn.cis.ppod.domain.PPodOtuSet;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.model.ModelFactory;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.OtuSet;

/**
 * Merge {@code sourceOtuSet} onto {@code targetOtuSet}.
 * 
 * @author Sam Donnelly
 */
class MergeOtuSets implements IMergeOtuSets {

	private INewVersionInfo newVersionInfo;

	@Inject
	MergeOtuSets(final INewVersionInfo newVersionInfo) {
		this.newVersionInfo = newVersionInfo;
	}

	public void mergeOTUSets(
			final OtuSet targetOtuSet,
			final PPodOtuSet sourceOtuSet) {
		checkNotNull(targetOtuSet);
		checkNotNull(sourceOtuSet);
		targetOtuSet.setLabel(sourceOtuSet.getLabel());
		// targetOtuSet.setDescription(sourceOtuSet.getDescription());

		// This is for a response to the service client.
		targetOtuSet.setDocId(sourceOtuSet.getDocId());

		final List<Otu> newTargetOTUs =
				newArrayListWithCapacity(sourceOtuSet.getOtus().size());

		for (final PPodOtu sourceOTU : sourceOtuSet.getOtus()) {
			Otu targetOTU;
			if (null == (targetOTU =
					find(targetOtuSet.getOtus(),
							compose(
									equalTo(sourceOTU
											.getPPodId()),
											IHasPPodId.getPPodId),
											null))) {
				targetOTU = ModelFactory.newOTU(newVersionInfo
						.getNewVersionInfo());
			}
			newTargetOTUs.add(targetOTU);
			targetOTU.setLabel(sourceOTU.getLabel());

			// This is for a response to the service client.
			targetOTU.setDocId(sourceOTU.getDocId());
		}
		targetOtuSet.setOtus(newTargetOTUs);
	}
}
