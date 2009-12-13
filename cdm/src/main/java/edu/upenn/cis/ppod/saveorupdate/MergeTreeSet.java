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

import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;

/**
 * @author Sam Donnelly
 */
public class MergeTreeSet implements IMergeTreeSet {

	private final Provider<Tree> treeProvider;

	@Inject
	MergeTreeSet(final Provider<Tree> treeProvider) {
		this.treeProvider = treeProvider;
	}

	public TreeSet merge(final TreeSet targetTreeSet,
			final TreeSet sourceTreeSet,
			final Map<OTU, OTU> mergedOTUsBySourceOTU) {

		// For the response to the client
		targetTreeSet.setDocId(sourceTreeSet.getDocId());

		targetTreeSet.setLabel(sourceTreeSet.getLabel());

		final Set<Tree> dbTreesToBeRemoved = newHashSet();

		// Get rid of deleted trees
		for (final Tree dbTree : targetTreeSet.getTrees()) {
			if (null == sourceTreeSet.getTreeByPPodId(dbTree.getPPodId())) {
				dbTreesToBeRemoved.add(dbTree);
			}
		}

		for (final Tree dbTreeToBeRemoved : dbTreesToBeRemoved) {
			targetTreeSet.removeTree(dbTreeToBeRemoved);
		}

		for (final Tree sourceTree : sourceTreeSet.getTrees()) {
			Tree targetTree;
			if (null == (targetTree = targetTreeSet.getTreeByPPodId(sourceTree
					.getPPodId()))) {
				targetTree = treeProvider.get();
				targetTreeSet.addTree(targetTree);
				targetTree.setPPodId();
			}
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
		return targetTreeSet;
	}
}