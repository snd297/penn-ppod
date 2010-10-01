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
import com.google.inject.Provider;

import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.IWithPPodId;

/**
 * Merge {@code sourceOTUSet} onto {@code targetOTUSet}.
 * 
 * @author Sam Donnelly
 */
final class MergeOTUSets implements IMergeOTUSets {

	private final Provider<IOTU> otuProvider;

	@Inject
	MergeOTUSets(
			final Provider<IOTU> otuProvider) {
		this.otuProvider = otuProvider;
	}

	public void mergeOTUSets(
			final IOTUSet targetOTUSet,
			final IOTUSet sourceOTUSet) {
		checkNotNull(targetOTUSet);
		checkNotNull(sourceOTUSet);
		targetOTUSet.setLabel(sourceOTUSet.getLabel());
		targetOTUSet.setDescription(sourceOTUSet.getDescription());

		// This is for a response to the service client.
		targetOTUSet.setDocId(sourceOTUSet.getDocId());

		final List<IOTU> newTargetOTUs =
				newArrayListWithCapacity(sourceOTUSet.getOTUs().size());

		for (final IOTU sourceOTU : sourceOTUSet.getOTUs()) {
			IOTU targetOTU;
			if (null == (targetOTU =
					find(targetOTUSet.getOTUs(),
							compose(
									equalTo(sourceOTU
											.getPPodId()),
											IWithPPodId.getPPodId),
											null))) {
				targetOTU = otuProvider.get();
			}
			newTargetOTUs.add(targetOTU);
			targetOTU.setLabel(sourceOTU.getLabel());

			// This is for a response to the service client.
			targetOTU.setDocId(sourceOTU.getDocId());
		}
		targetOTUSet.setOTUs(newTargetOTUs);
	}
}
