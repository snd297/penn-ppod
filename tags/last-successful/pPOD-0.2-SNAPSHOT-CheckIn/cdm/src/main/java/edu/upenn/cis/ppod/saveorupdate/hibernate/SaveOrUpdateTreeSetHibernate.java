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
package edu.upenn.cis.ppod.saveorupdate.hibernate;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateTreeSet;

/**
 * @author Sam Donnelly
 */
public class SaveOrUpdateTreeSetHibernate implements ISaveOrUpdateTreeSet {

	private final Provider<Tree> treeProvider;

	@Inject
	SaveOrUpdateTreeSetHibernate(final Provider<Tree> treeProvider) {
		this.treeProvider = treeProvider;
	}

	public TreeSet saveOrUpdate(final TreeSet incomingTreeSet,
			final TreeSet dbTreeSet, final OTUSet dbOTUSet,
			final Map<OTU, OTU> dbOTUsByIncomingOTU) {

		// For the response to the client
		dbTreeSet.setDocId(incomingTreeSet.getDocId());

		dbTreeSet.setLabel(incomingTreeSet.getLabel());
		dbOTUSet.addTreeSet(dbTreeSet);

		final Set<Tree> dbTreesToBeRemoved = newHashSet();

		// Get rid of deleted trees
		for (final Tree dbTree : dbTreeSet.getTrees()) {
			if (null == incomingTreeSet.getTreeByPPodId(dbTree.getPPodId())) {
				dbTreesToBeRemoved.add(dbTree);
			}
		}

		for (final Tree dbTreeToBeRemoved : dbTreesToBeRemoved) {
			dbTreeSet.removeTree(dbTreeToBeRemoved);
		}

		for (final Tree incomingTree : incomingTreeSet.getTrees()) {
			Tree dbTree;
			if (null == (dbTree = dbTreeSet.getTreeByPPodId(incomingTree
					.getPPodId()))) {
				dbTree = treeProvider.get();
				dbTreeSet.addTree(dbTree);
				dbTree.setPPodId();
			}
			String dbNewick = incomingTree.getNewick();
			for (final Entry<OTU, OTU> dbOTUByIncomingOTU : dbOTUsByIncomingOTU
					.entrySet()) {
				dbNewick = dbNewick.replace(dbOTUByIncomingOTU.getKey()
						.getDocId(), dbOTUByIncomingOTU.getValue().getPPodId());

			}
			dbTree.setNewick(dbNewick);
			dbTree.setLabel(incomingTree.getLabel());
		}
		return dbTreeSet;
	}
}