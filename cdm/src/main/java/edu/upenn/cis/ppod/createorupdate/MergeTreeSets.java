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
import static com.google.common.collect.Lists.newArrayList;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.Iterator;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.modelinterfaces.INewVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.IOTU;
import edu.upenn.cis.ppod.modelinterfaces.IWithPPodId;

/**
 * An {@code IMergeTreeSets}.
 * 
 * @author Sam Donnelly
 */
final class MergeTreeSets implements IMergeTreeSets {

	private final Provider<Tree> treeProvider;
	private final INewVersionInfo newVersionInfo;

	@Inject
	MergeTreeSets(final Provider<Tree> treeProvider,
			@Assisted final INewVersionInfo newVersionInfo) {

		this.treeProvider = treeProvider;
		this.newVersionInfo = newVersionInfo;
	}

	public void mergeTreeSets(
			final TreeSet targetTreeSet,
			final TreeSet sourceTreeSet) {
		checkNotNull(targetTreeSet);
		checkNotNull(sourceTreeSet);

		// For the response to the client
		targetTreeSet.setXmlId(sourceTreeSet.getXmlId());

		targetTreeSet.setLabel(sourceTreeSet.getLabel());

		final List<Tree> newTargetTrees = newArrayList();

		for (final Tree sourceTree : sourceTreeSet.getTrees()) {
			Tree targetTree;
			if (null == (targetTree =
					findIf(targetTreeSet.getTrees(), compose(
							equalTo(sourceTree.getPPodId()),
							IWithPPodId.getPPodId)))) {
				targetTree = treeProvider.get();
				targetTree.setVersionInfo(
						newVersionInfo.getNewVersionInfo());
				targetTree.setPPodId();
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
						sourceOTU.getXmlId(),
						targetOTU.getPPodId());
			}
			targetTree.setNewick(targetNewick);
			targetTree.setLabel(sourceTree.getLabel());
		}
		targetTreeSet.setTrees(newTargetTrees);
	}
}