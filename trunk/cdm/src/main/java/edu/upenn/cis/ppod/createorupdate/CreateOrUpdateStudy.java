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

import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.dao.IAttachmentNamespaceDAO;
import edu.upenn.cis.ppod.dao.IAttachmentTypeDAO;
import edu.upenn.cis.ppod.dao.IObjectWithLongIdDAO;
import edu.upenn.cis.ppod.dao.IStudyDAO;
import edu.upenn.cis.ppod.domain.IHasPPodId;
import edu.upenn.cis.ppod.domain.PPodDnaMatrix;
import edu.upenn.cis.ppod.domain.PPodDnaSequenceSet;
import edu.upenn.cis.ppod.domain.PPodOtuSet;
import edu.upenn.cis.ppod.domain.PPodStandardMatrix;
import edu.upenn.cis.ppod.domain.PPodStudy;
import edu.upenn.cis.ppod.domain.PPodTreeSet;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.model.DnaMatrix;
import edu.upenn.cis.ppod.model.DnaSequenceSet;
import edu.upenn.cis.ppod.model.ModelFactory;
import edu.upenn.cis.ppod.model.OtuSet;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.TreeSet;

/**
 * Create a new study or update an existing one.
 * 
 * @author Sam Donnelly
 */
class CreateOrUpdateStudy implements ICreateOrUpdateStudy {

	private final IStudyDAO studyDAO;
	private final IObjectWithLongIdDAO dao;
	private final INewVersionInfo newVersionInfo;
	private final IMergeOtuSets mergeOTUSets;
	private final ICreateOrUpdateDNAMatrix createOrUpdateDNAMatrix;
	private final IMergeDNASequenceSets mergeDNASequenceSets;
	private final ICreateOrUpdateStandardMatrix createOrUpdateStandardMatrix;
	private final IMergeTreeSets mergeTreeSets;

	@Inject
	CreateOrUpdateStudy(
			final IObjectWithLongIdDAO dao,
			final IAttachmentNamespaceDAO attachmentNamespaceDAO,
			final IAttachmentTypeDAO attachmentTypeDAO,
			final IStudyDAO studyDAO,
			final INewVersionInfo newVersionInfo,
			final IMergeOtuSets mergeOTUSets,
			final ICreateOrUpdateDNAMatrix createOrUpdateDNAMatrix,
			final IMergeDNASequenceSets mergeDNASequenceSets,
			final ICreateOrUpdateStandardMatrix createOrUpdateStandardMatrix,
			final IMergeTreeSets mergeTreeSets) {
		this.studyDAO = studyDAO;
		this.dao = dao;
		this.newVersionInfo = newVersionInfo;
		this.mergeOTUSets = mergeOTUSets;
		this.createOrUpdateDNAMatrix = createOrUpdateDNAMatrix;
		this.mergeDNASequenceSets = mergeDNASequenceSets;
		this.createOrUpdateStandardMatrix = createOrUpdateStandardMatrix;
		this.mergeTreeSets = mergeTreeSets;
	}

	public Study createOrUpdateStudy(
			final PPodStudy incomingStudy) {
		Study dbStudy = null;
		boolean makeStudyPersistent = false;
		if (null == (dbStudy =
				studyDAO.getStudyByPPodId(
						incomingStudy.getPPodId()))) {
			dbStudy = ModelFactory.newStudy(newVersionInfo.getNewVersionInfo());
			makeStudyPersistent = true;
		}

		dbStudy.setLabel(incomingStudy.getLabel());

		if (makeStudyPersistent) {
			studyDAO.makePersistent(dbStudy);
		}

		// Delete otu sets in persisted study that are not in the incoming
		// study.
		final Set<OtuSet> toBeRemoveds = newHashSet();
		for (final OtuSet dbOTUSet : dbStudy.getOtuSets()) {
			if (null == find(
					incomingStudy.getOtuSets(),
					compose(
							equalTo(
							dbOTUSet.getPPodId()),
							IHasPPodId.getPPodId),
					null)) {
				toBeRemoveds.add(dbOTUSet);
			}
		}
		for (final OtuSet toBeRemoved : toBeRemoveds) {
			dbStudy.removeOTUSet(toBeRemoved);
		}

		// Save or update incoming otu sets
		int incomingOTUSetPos = -1;
		for (final PPodOtuSet incomingOTUSet : incomingStudy.getOtuSets()) {
			incomingOTUSetPos++;
			OtuSet dbOTUSet;
			if (null == (dbOTUSet =
					find(dbStudy.getOtuSets(),
							compose(
									equalTo(
									incomingOTUSet.getPPodId()),
									IHasPPodId.getPPodId),
							null))) {
				dbOTUSet = ModelFactory.newOTUSet(newVersionInfo
						.getNewVersionInfo());
				dbOTUSet.setLabel(incomingOTUSet.getLabel()); // non-null, do it
				// now
				dbStudy.addOtuSet(incomingOTUSetPos, dbOTUSet);
				dao.makePersistent(dbOTUSet);
			}

			mergeOTUSets.mergeOTUSets(dbOTUSet, incomingOTUSet);

			handleDNAMatrices(dbOTUSet, incomingOTUSet);
			handleStandardMatrices(dbOTUSet, incomingOTUSet);
			handleDNASequenceSets(dbOTUSet, incomingOTUSet);
			handleTreeSets(dbOTUSet, incomingOTUSet);
		}
		return dbStudy;
	}

	private void handleDNAMatrices(
			final OtuSet dbOTUSet,
			final PPodOtuSet incomingOTUSet) {

		// Let's delete matrices missing from the incoming OTU set
		final Set<DnaMatrix> toBeRemoveds = newHashSet();
		for (final DnaMatrix dbMatrix : dbOTUSet.getDnaMatrices()) {
			if (null == find(
							incomingOTUSet.getDnaMatrices(),
							compose(
									equalTo(
										dbMatrix.getPPodId()),
										IHasPPodId.getPPodId),
										null)) {
				toBeRemoveds.add(dbMatrix);
			}
		}
		for (final DnaMatrix toBeRemoved : toBeRemoveds) {
			dbOTUSet.removeDnaMatrix(toBeRemoved);
		}
		int incomingMatrixPos = -1;
		for (final PPodDnaMatrix incomingMatrix : incomingOTUSet
				.getDnaMatrices()) {
			incomingMatrixPos++;
			DnaMatrix dbMatrix;
			if (null == (dbMatrix =
					find(
							dbOTUSet.getDnaMatrices(),
							compose(
									equalTo(
											incomingMatrix.getPPodId()),
									IHasPPodId.getPPodId),
									null
									))) {
				dbMatrix = ModelFactory.newDNAMatrix(newVersionInfo
						.getNewVersionInfo());

				// Do this here because it's non-nullable
				dbMatrix.setLabel(incomingMatrix.getLabel());
				dbOTUSet.addDnaMatrix(incomingMatrixPos, dbMatrix);
				dao.makePersistent(dbMatrix);
			}
			createOrUpdateDNAMatrix
							.createOrUpdateMatrix(
									dbMatrix, incomingMatrix);
		}
	}

	private void handleDNASequenceSets(
			final OtuSet dbOTUSet,
			final PPodOtuSet incomingOTUSet) {

		// Let's delete sequences missing from the incoming otu set
		final Set<DnaSequenceSet> toBeRemoveds = newHashSet();
		for (final DnaSequenceSet dbSequenceSet : dbOTUSet
				.getDnaSequenceSets()) {
			if (null == find(
					incomingOTUSet.getDnaSequenceSets(),
					compose(
							equalTo(
								dbSequenceSet.getPPodId()),
							IHasPPodId.getPPodId),
							null)) {
				toBeRemoveds.add(dbSequenceSet);
			}
		}

		for (final DnaSequenceSet toBeRemoved : toBeRemoveds) {
			dbOTUSet.removeDnaSequenceSet(toBeRemoved);
		}
		int incomingSequenceSetPos = -1;
		for (final PPodDnaSequenceSet incomingSequenceSet : incomingOTUSet
				.getDnaSequenceSets()) {
			incomingSequenceSetPos++;
			DnaSequenceSet dbDNASequenceSet;
			if (null == (dbDNASequenceSet =
					find(dbOTUSet.getDnaSequenceSets(),
							compose(
									equalTo(incomingSequenceSet
											.getPPodId()),
									IHasPPodId.getPPodId),
									null))) {
				dbDNASequenceSet = ModelFactory
						.newDNASequenceSet(newVersionInfo.getNewVersionInfo());
				dbDNASequenceSet.setLabel(incomingSequenceSet.getLabel());
				dbOTUSet.addDnaSequenceSet(
						incomingSequenceSetPos, dbDNASequenceSet);
				dao.makePersistent(dbDNASequenceSet);
			}
			mergeDNASequenceSets
					.mergeSequenceSets(dbDNASequenceSet, incomingSequenceSet);
		}
	}

	private void handleStandardMatrices(
			final OtuSet dbOtuSet,
			final PPodOtuSet incomingOTUSet) {

		// Let's delete matrices missing from the incoming OTU set
		final Set<StandardMatrix> toBeRemoveds = newHashSet();
		for (final StandardMatrix dbMatrix : dbOtuSet.getStandardMatrices()) {
			if (null == find(
							incomingOTUSet.getStandardMatrices(),
							compose(
									equalTo(
										dbMatrix.getPPodId()),
										IHasPPodId.getPPodId),
										null)) {
				toBeRemoveds.add(dbMatrix);
			}
		}

		for (final StandardMatrix toBeRemoved : toBeRemoveds) {
			dbOtuSet.removeStandardMatrix(toBeRemoved);
		}
		int incomingMatrixPos = -1;
		for (final PPodStandardMatrix incomingMatrix : incomingOTUSet
				.getStandardMatrices()) {
			incomingMatrixPos++;
			StandardMatrix dbMatrix;
			if (null == (dbMatrix =
					find(
							dbOtuSet.getStandardMatrices(),
							compose(
									equalTo(
											incomingMatrix.getPPodId()),
									IHasPPodId.getPPodId),
									null))) {
				dbMatrix = ModelFactory.newStandardMatrix(newVersionInfo
						.getNewVersionInfo());
				dbMatrix.setLabel(incomingMatrix.getLabel());
				dbOtuSet.addStandardMatrix(
						incomingMatrixPos,
						dbMatrix);
				dao.makePersistent(dbMatrix);
			}
			createOrUpdateStandardMatrix
					.createOrUpdateMatrix(dbMatrix, incomingMatrix);
		}
	}

	private void handleTreeSets(
			final OtuSet dbOtuSet,
			final PPodOtuSet incomingOtuSet) {

		// Let's delete tree sets missing from incoming OTU set
		final Set<TreeSet> toBeDeleteds = newHashSet();
		for (final TreeSet dbTreeSet : dbOtuSet.getTreeSets()) {
			if (null == find(incomingOtuSet.getTreeSets(),
						compose(
								equalTo(
										dbTreeSet.getPPodId()),
										IHasPPodId.getPPodId),
										null)) {
				toBeDeleteds.add(dbTreeSet);
			}
		}

		for (final TreeSet toBeDeleted : toBeDeleteds) {
			dbOtuSet.removeTreeSet(toBeDeleted);
		}

		int incomingTreeSetPos = -1;

		// Now let's add in new ones
		for (final PPodTreeSet incomingTreeSet : incomingOtuSet.getTreeSets()) {
			incomingTreeSetPos++;
			TreeSet dbTreeSet;
			if (null == (dbTreeSet =
					find(dbOtuSet.getTreeSets(),
							compose(equalTo(incomingTreeSet.getPPodId()),
									IHasPPodId.getPPodId),
									null))) {
				dbTreeSet = ModelFactory.newTreeSet(newVersionInfo
						.getNewVersionInfo());
				dbTreeSet.setLabel(incomingTreeSet.getLabel());
				dbOtuSet.addTreeSet(incomingTreeSetPos, dbTreeSet);
				dao.makePersistent(dbTreeSet);
			}
			mergeTreeSets.mergeTreeSets(dbTreeSet, incomingTreeSet,
					incomingOtuSet);
		}
	}
}
