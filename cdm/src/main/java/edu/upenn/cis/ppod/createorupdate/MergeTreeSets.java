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

import java.util.List;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.domain.IHasPPodId;
import edu.upenn.cis.ppod.domain.PPodOtu;
import edu.upenn.cis.ppod.domain.PPodOtuSet;
import edu.upenn.cis.ppod.domain.PPodTree;
import edu.upenn.cis.ppod.domain.PPodTreeSet;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.model.ModelFactory;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;

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
			final TreeSet targetTreeSet,
			final PPodTreeSet sourceTreeSet,
			final PPodOtuSet sourceOtuSet) {
		checkNotNull(targetTreeSet);
		checkNotNull(sourceTreeSet);

		targetTreeSet.setLabel(sourceTreeSet.getLabel());

		final List<Tree> newTargetTrees = newArrayList();

		for (final PPodTree sourceTree : sourceTreeSet.getTrees()) {
			Tree targetTree;
			if (null == (targetTree =
					find(targetTreeSet.getTrees(), compose(
									equalTo(sourceTree.getPPodId()),
									IHasPPodId.getPPodId),
									null))) {
				targetTree = ModelFactory.newTree(newVersionInfo
						.getNewVersionInfo());
			}
			newTargetTrees.add(targetTree);

			String targetNewick = sourceTree.getNewick();

			if (sourceOtuSet.getOtus().size() != targetTreeSet
					.getParent()
					.getOtus()
					.size()) {
				throw new IllegalArgumentException(
						"sourceTreeSet.getOTUSet().getOTUsSize() should be the same as targetTreeSet.getOTUSet().getOTUsSize()");
			}

			for (int i = 0, sourceOtuSetSize = sourceOtuSet.getOtus().size(); i < sourceOtuSetSize; i++) {
				final PPodOtu sourceOTU = sourceOtuSet.getOtus().get(i);
				final Otu targetOTU = targetTreeSet.getParent().getOtus()
						.get(i);
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