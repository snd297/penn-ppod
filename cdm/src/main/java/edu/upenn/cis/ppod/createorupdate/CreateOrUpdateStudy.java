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

import edu.upenn.cis.ppod.IHasPPodId;
import edu.upenn.cis.ppod.PPodDnaMatrix;
import edu.upenn.cis.ppod.PPodOtuSet;
import edu.upenn.cis.ppod.PPodProteinMatrix;
import edu.upenn.cis.ppod.PPodStandardMatrix;
import edu.upenn.cis.ppod.PPodStudy;
import edu.upenn.cis.ppod.PPodTreeSet;
import edu.upenn.cis.ppod.dao.IDnaMatrixDAO;
import edu.upenn.cis.ppod.dao.IOtuSetDAO;
import edu.upenn.cis.ppod.dao.IProteinMatrixDAO;
import edu.upenn.cis.ppod.dao.IStandardMatrixDAO;
import edu.upenn.cis.ppod.dao.IStudyDAO;
import edu.upenn.cis.ppod.dao.ITreeSetDAO;
import edu.upenn.cis.ppod.model.DnaMatrix;
import edu.upenn.cis.ppod.model.OtuSet;
import edu.upenn.cis.ppod.model.ProteinMatrix;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.TreeSet;

/**
 * Create a new study or update an existing one.
 * 
 * @author Sam Donnelly
 */
public final class CreateOrUpdateStudy {

	private final IStudyDAO studyDAO;
	private final MergeOtuSets mergeOtuSets;
	// private final MergeDnaSequenceSets mergeDNASequenceSets;
	private final CreateOrUpdateStandardMatrix createOrUpdateStandardMatrix;
	private final MergeTreeSets mergeTreeSets;
	private final IOtuSetDAO otuSetDAO;
	// private final IDnaSequenceSetDAO dnaSequenceSetDAO;
	private final IDnaMatrixDAO dnaMatrixDAO;
	private final IStandardMatrixDAO standardMatrixDAO;
	private final ITreeSetDAO treeSetDAO;
	private final IProteinMatrixDAO proteinMatrixDAO;

	@Inject
	CreateOrUpdateStudy(
			final IStudyDAO studyDAO,
			final MergeOtuSets mergeOTUSets,
			final CreateOrUpdateStandardMatrix createOrUpdateStandardMatrix,
			final MergeTreeSets mergeTreeSets,
			final IOtuSetDAO otuSetDAO,
			final IDnaMatrixDAO dnaMatrixDAO,
			final IStandardMatrixDAO standardMatrixDAO,
			final ITreeSetDAO treeSetDAO,
			final IProteinMatrixDAO proteinMatrixDAO) {
		this.studyDAO = studyDAO;
		this.mergeOtuSets = mergeOTUSets;
		this.createOrUpdateStandardMatrix = createOrUpdateStandardMatrix;
		this.mergeTreeSets = mergeTreeSets;
		this.otuSetDAO = otuSetDAO;
		this.dnaMatrixDAO = dnaMatrixDAO;
		this.standardMatrixDAO = standardMatrixDAO;
		this.treeSetDAO = treeSetDAO;
		this.proteinMatrixDAO = proteinMatrixDAO;
	}

	public Study createOrUpdateStudy(final PPodStudy incomingStudy) {
		Study dbStudy = null;
		boolean makeStudyPersistent = false;
		if (null == (dbStudy =
				studyDAO.getStudyByPPodId(
						incomingStudy.getPPodId()))) {
			dbStudy = new Study();
			makeStudyPersistent = true;
		}

		dbStudy.setLabel(incomingStudy.getLabel());

		if (makeStudyPersistent) {
			studyDAO.makePersistent(dbStudy);
		}

		// Delete otu sets in persisted study that are not in the incoming
		// study.
		final Set<OtuSet> toBeRemoveds = newHashSet();
		for (final OtuSet dbOtuSet : dbStudy.getOtuSets()) {
			if (null == find(
					incomingStudy.getOtuSets(),
					compose(
							equalTo(
							dbOtuSet.getPPodId()),
							IHasPPodId.getPPodId),
					null)) {
				toBeRemoveds.add(dbOtuSet);
			}
		}
		for (final OtuSet toBeRemoved : toBeRemoveds) {
			dbStudy.removeOtuSet(toBeRemoved);
		}

		// Save or update incoming otu sets
		int incomingOtuSetPos = -1;
		for (final PPodOtuSet incomingOtuSet : incomingStudy.getOtuSets()) {
			incomingOtuSetPos++;
			OtuSet dbOtuSet;
			if (null == (dbOtuSet =
					find(dbStudy.getOtuSets(),
							compose(
									equalTo(
									incomingOtuSet.getPPodId()),
									IHasPPodId.getPPodId),
							null))) {
				dbOtuSet = new OtuSet();
				dbOtuSet.setLabel(incomingOtuSet.getLabel()); // non-null, do it
				// now
				dbStudy.addOtuSet(incomingOtuSetPos, dbOtuSet);
				otuSetDAO.makePersistent(dbOtuSet);
			}

			mergeOtuSets.mergeOtuSets(dbOtuSet, incomingOtuSet);

			handleProteinMatrices(dbOtuSet, incomingOtuSet);
			handleDnaMatrices(dbOtuSet, incomingOtuSet);
			handleStandardMatrices(dbOtuSet, incomingOtuSet);
			// handleDnaSequenceSets(dbOtuSet, incomingOtuSet);
			handleTreeSets(dbOtuSet, incomingOtuSet);
		}

		return dbStudy;
	}

	private void handleDnaMatrices(
			final OtuSet dbOtuSet,
			final PPodOtuSet incomingOtuSet) {

		// Let's delete matrices missing from the incoming OTU set
		final Set<DnaMatrix> toBeRemoveds = newHashSet();
		for (final DnaMatrix dbMatrix : dbOtuSet.getDnaMatrices()) {
			if (null == find(
							incomingOtuSet.getDnaMatrices(),
							compose(
									equalTo(
										dbMatrix.getPPodId()),
										IHasPPodId.getPPodId),
										null)) {
				toBeRemoveds.add(dbMatrix);
			}
		}
		for (final DnaMatrix toBeRemoved : toBeRemoveds) {
			dbOtuSet.removeDnaMatrix(toBeRemoved);
		}
		int incomingMatrixPos = -1;
		for (final PPodDnaMatrix incomingMatrix : incomingOtuSet
				.getDnaMatrices()) {
			incomingMatrixPos++;
			DnaMatrix dbMatrix;
			if (null == (dbMatrix =
					find(
							dbOtuSet.getDnaMatrices(),
							compose(
									equalTo(
											incomingMatrix.getPPodId()),
									IHasPPodId.getPPodId),
									null
									))) {
				dbMatrix = new DnaMatrix();

				// Do this here because it's non-nullable
				dbMatrix.setLabel(incomingMatrix.getLabel());
				dbOtuSet.addDnaMatrix(incomingMatrixPos, dbMatrix);
				dnaMatrixDAO.makePersistent(dbMatrix);
			}
			new CreateOrUpdateDnaMatrix()
							.createOrUpdateMatrix(
									dbMatrix, incomingMatrix);
		}
	}

	// private void handleDnaSequenceSets(
	// final OtuSet dbOtuSet,
	// final PPodOtuSet incomingOtuSet) {
	//
	// // Let's delete sequences missing from the incoming otu set
	// final Set<DnaSequenceSet> toBeRemoveds = newHashSet();
	// for (final DnaSequenceSet dbSequenceSet : dbOtuSet
	// .getDnaSequenceSets()) {
	// if (null == find(
	// incomingOtuSet.getDnaSequenceSets(),
	// compose(
	// equalTo(
	// dbSequenceSet.getPPodId()),
	// IHasPPodId.getPPodId),
	// null)) {
	// toBeRemoveds.add(dbSequenceSet);
	// }
	// }
	//
	// for (final DnaSequenceSet toBeRemoved : toBeRemoveds) {
	// dbOtuSet.removeDnaSequenceSet(toBeRemoved);
	// }
	// int incomingSequenceSetPos = -1;
	// for (final PPodDnaSequenceSet incomingSequenceSet : incomingOtuSet
	// .getDnaSequenceSets()) {
	// incomingSequenceSetPos++;
	// DnaSequenceSet dbDnaSequenceSet;
	// if (null == (dbDnaSequenceSet =
	// find(dbOtuSet.getDnaSequenceSets(),
	// compose(
	// equalTo(incomingSequenceSet
	// .getPPodId()),
	// IHasPPodId.getPPodId),
	// null))) {
	// dbDnaSequenceSet = new DnaSequenceSet();
	// dbDnaSequenceSet.setLabel(incomingSequenceSet.getLabel());
	// dbOtuSet.addDnaSequenceSet(
	// incomingSequenceSetPos, dbDnaSequenceSet);
	// dnaSequenceSetDAO.makePersistent(dbDnaSequenceSet);
	// }
	// mergeDNASequenceSets
	// .mergeSequenceSets(dbDnaSequenceSet, incomingSequenceSet);
	// }
	// }

	private void handleProteinMatrices(
			final OtuSet dbOTUSet,
			final PPodOtuSet incomingOTUSet) {

		// Let's delete matrices missing from the incoming OTU set
		final Set<ProteinMatrix> toBeRemoveds = newHashSet();
		for (final ProteinMatrix dbMatrix : dbOTUSet.getProteinMatrices()) {
			if (null == find(
							incomingOTUSet.getProteinMatrices(),
							compose(
									equalTo(
										dbMatrix.getPPodId()),
										IHasPPodId.getPPodId),
										null)) {
				toBeRemoveds.add(dbMatrix);
			}
		}
		for (final ProteinMatrix toBeRemoved : toBeRemoveds) {
			dbOTUSet.removeProteinMatrix(toBeRemoved);
		}
		int incomingMatrixPos = -1;
		for (final PPodProteinMatrix incomingMatrix : incomingOTUSet
				.getProteinMatrices()) {
			incomingMatrixPos++;
			ProteinMatrix dbMatrix;
			if (null == (dbMatrix =
					find(
							dbOTUSet.getProteinMatrices(),
							compose(
									equalTo(
											incomingMatrix.getPPodId()),
									IHasPPodId.getPPodId),
									null
									))) {
				dbMatrix = new ProteinMatrix();

				// Do this here because it's non-nullable
				dbMatrix.setLabel(incomingMatrix.getLabel());
				dbOTUSet.addProteinMatrix(incomingMatrixPos, dbMatrix);
				proteinMatrixDAO.makePersistent(dbMatrix);
			}
			new CreateOrUpdateProteinMatrix()
							.createOrUpdateMatrix(
									dbMatrix, incomingMatrix);
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
				dbMatrix = new StandardMatrix();
				dbMatrix.setLabel(incomingMatrix.getLabel());
				dbOtuSet.addStandardMatrix(
						incomingMatrixPos,
						dbMatrix);
				standardMatrixDAO.makePersistent(dbMatrix);
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
				dbTreeSet = new TreeSet();
				dbTreeSet.setLabel(incomingTreeSet.getLabel());
				dbOtuSet.addTreeSet(incomingTreeSetPos, dbTreeSet);
				treeSetDAO.makePersistent(dbTreeSet);
			}
			mergeTreeSets.mergeTreeSets(dbTreeSet, incomingTreeSet,
					incomingOtuSet);
		}
	}
}
