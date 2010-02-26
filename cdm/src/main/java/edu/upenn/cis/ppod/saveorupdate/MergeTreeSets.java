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
package edu.upenn.cis.ppod.saveorupdate;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Lists.newArrayList;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.IUUPPodEntity;

/**
 * @author Sam Donnelly
 */
public class MergeTreeSets implements IMergeTreeSets {

	private final Provider<Tree> treeProvider;
	private final INewPPodVersionInfo newPPodVersionInfo;

	@Inject
	MergeTreeSets(final Provider<Tree> treeProvider,
			@Assisted final INewPPodVersionInfo newPPodVersionInfo) {

		this.treeProvider = treeProvider;
		this.newPPodVersionInfo = newPPodVersionInfo;
	}

	public TreeSet merge(final TreeSet targetTreeSet,
			final TreeSet sourceTreeSet, 
			final Map<OTU, OTU> mergedOTUsBySourceOTU) {
		checkNotNull(targetTreeSet);
		checkNotNull(sourceTreeSet);

		// For the response to the client
		targetTreeSet.setDocId(sourceTreeSet.getDocId());

		targetTreeSet.setLabel(sourceTreeSet.getLabel());

		final List<Tree> newTargetTrees = newArrayList();

		for (final Tree sourceTree : sourceTreeSet.getTrees()) {
			Tree targetTree;
			if (null == (targetTree = findIf(targetTreeSet.getTrees(), compose(
					equalTo(sourceTree.getPPodId()), IUUPPodEntity.getPPodId)))) {
				targetTree = treeProvider.get();
				targetTree.setPPodVersionInfo(newPPodVersionInfo
						.getNewPPodVersionInfo());
				targetTree.setPPodId();
			}
			newTargetTrees.add(targetTree);
			String targetNewick = sourceTree.getNewick();
			for (final Entry<OTU, OTU> mergedOTUBySourceOTU : mergedOTUsBySourceOTU
					.entrySet()) {
				targetNewick = targetNewick.replace(mergedOTUBySourceOTU
						.getKey().getDocId(), mergedOTUBySourceOTU.getValue()
						.getPPodId());

			}
			targetTree.setNewick(targetNewick);
			targetTree.setLabel(sourceTree.getLabel());
		}
		targetTreeSet.setTrees(newTargetTrees);
		return targetTreeSet;
	}
}