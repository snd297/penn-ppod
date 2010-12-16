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
import static com.google.common.collect.Lists.newArrayList;

import java.util.Iterator;
import java.util.List;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.ITree;
import edu.upenn.cis.ppod.imodel.ITreeSet;
import edu.upenn.cis.ppod.imodel.IWithPPodId;
import edu.upenn.cis.ppod.model.ModelFactory;

/**
 * An {@code IMergeTreeSets}.
 * 
 * @author Sam Donnelly
 */
class MergeTreeSets implements IMergeTreeSets {

	private final INewVersionInfo newVersionInfo;

	@Inject
	MergeTreeSets(INewVersionInfo newVersionInfo) {
		this.newVersionInfo = newVersionInfo;
	}

	public void mergeTreeSets(
			final ITreeSet targetTreeSet,
			final ITreeSet sourceTreeSet) {
		checkNotNull(targetTreeSet);
		checkNotNull(sourceTreeSet);

		// For the response to the client
		targetTreeSet.setDocId(sourceTreeSet.getDocId());

		targetTreeSet.setLabel(sourceTreeSet.getLabel());

		final List<ITree> newTargetTrees = newArrayList();

		for (final ITree sourceTree : sourceTreeSet.getTrees()) {
			ITree targetTree;
			if (null == (targetTree =
							find(targetTreeSet.getTrees(), compose(
									equalTo(sourceTree.getPPodId()),
									IWithPPodId.getPPodId),
									null))) {
				targetTree = ModelFactory.newTree(newVersionInfo
						.getNewVersionInfo());
			}
			newTargetTrees.add(targetTree);

			String targetNewick = sourceTree.getNewick();

			if (sourceTreeSet
					.getParent()
					.getOTUs()
					.size() != targetTreeSet
					.getParent()
					.getOTUs()
					.size()) {
				throw new IllegalArgumentException(
						"sourceTreeSet.getOTUSet().getOTUsSize() should be the same as targetTreeSet.getOTUSet().getOTUsSize()");
			}
			for (final Iterator<IOTU> sourceOTUItr = sourceTreeSet.getParent()
					.getOTUs().iterator(), targetOTUItr = targetTreeSet
					.getParent().getOTUs().iterator(); sourceOTUItr.hasNext();) {
				final IOTU sourceOTU = sourceOTUItr.next();
				final IOTU targetOTU = targetOTUItr.next();
				targetNewick = targetNewick.replace(
						sourceOTU.getDocId(),
						targetOTU.getPPodId());
			}
			targetTree.setNewick(targetNewick);
			targetTree.setLabel(sourceTree.getLabel());
		}
		targetTreeSet.setTrees(newTargetTrees);
	}
}