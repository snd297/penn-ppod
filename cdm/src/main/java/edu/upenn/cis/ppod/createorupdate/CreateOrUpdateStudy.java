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
import edu.upenn.cis.ppod.imodel.IDNAMatrix;
import edu.upenn.cis.ppod.imodel.IDNASequenceSet;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStudy;
import edu.upenn.cis.ppod.imodel.ITreeSet;
import edu.upenn.cis.ppod.imodel.IWithPPodId;
import edu.upenn.cis.ppod.model.ModelFactory;

/**
 * Create a new study or update an existing one.
 * 
 * @author Sam Donnelly
 */
final class CreateOrUpdateStudy implements ICreateOrUpdateStudy {

	private final IStudyDAO studyDAO;
	private final IObjectWithLongIdDAO dao;
	private final INewVersionInfo newVersionInfo;
	private final MergeOTUSets mergeOTUSets;
	private CreateOrUpdateDNAMatrix createOrUpdateDNAMatrix;
	private MergeDNASequenceSets mergeDNASequenceSets;
	private CreateOrUpdateStandardMatrix createOrUpdateStandardMatrix;
	private MergeTreeSets mergeTreeSets;

	@Inject
	CreateOrUpdateStudy(
				final IObjectWithLongIdDAO dao,
			final IAttachmentNamespaceDAO attachmentNamespaceDAO,
			final IAttachmentTypeDAO attachmentTypeDAO,
			final IStudyDAO studyDAO,
			final INewVersionInfo newVersionInfo,
			final MergeOTUSets mergeOTUSets,
			final CreateOrUpdateDNAMatrix createOrUpdateDNAMatrix,
			final MergeDNASequenceSets mergeDNASequenceSets,
			final CreateOrUpdateStandardMatrix createOrUpdateStandardMatrix,
			final MergeTreeSets mergeTreeSets) {
		this.studyDAO = studyDAO;
		this.dao = dao;
		this.newVersionInfo = newVersionInfo;
		this.mergeOTUSets = mergeOTUSets;
		this.createOrUpdateDNAMatrix = createOrUpdateDNAMatrix;
		this.mergeDNASequenceSets = mergeDNASequenceSets;
		this.createOrUpdateStandardMatrix = createOrUpdateStandardMatrix;
		this.mergeTreeSets = mergeTreeSets;
	}

	public IStudy createOrUpdateStudy(final IStudy incomingStudy) {
		IStudy dbStudy = null;
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
		final Set<IOTUSet> toBeRemoveds = newHashSet();
		for (final IOTUSet dbOTUSet : dbStudy.getOTUSets()) {
			if (null == find(
					incomingStudy.getOTUSets(),
					compose(
							equalTo(
									dbOTUSet.getPPodId()),
							IWithPPodId.getPPodId),
							null)) {
				toBeRemoveds.add(dbOTUSet);
			}
		}
		for (final IOTUSet toBeRemoved : toBeRemoveds) {
			dbStudy.removeOTUSet(toBeRemoved);
		}

		// Save or update incoming otu sets
		int incomingOTUSetPos = -1;
		for (final IOTUSet incomingOTUSet : incomingStudy.getOTUSets()) {
			incomingOTUSetPos++;
			IOTUSet dbOTUSet;
			if (null == (dbOTUSet =
					find(dbStudy.getOTUSets(),
							compose(
									equalTo(
											incomingOTUSet.getPPodId()),
									IWithPPodId.getPPodId),
									null))) {
				dbOTUSet = ModelFactory.newOTUSet(newVersionInfo
						.getNewVersionInfo());
				dbOTUSet.setLabel(incomingOTUSet.getLabel()); // non-null, do it
																// now
				dbStudy.addOTUSet(incomingOTUSetPos, dbOTUSet);
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
			final IOTUSet dbOTUSet,
			final IOTUSet incomingOTUSet) {

		// Let's delete matrices missing from the incoming OTU set
		final Set<IDNAMatrix> toBeRemoveds = newHashSet();
		for (final IDNAMatrix dbMatrix : dbOTUSet.getDNAMatrices()) {
			if (null == find(
							incomingOTUSet.getDNAMatrices(),
							compose(
									equalTo(
										dbMatrix.getPPodId()),
										IWithPPodId.getPPodId),
										null)) {
				toBeRemoveds.add(dbMatrix);
			}
		}
		for (final IDNAMatrix toBeRemoved : toBeRemoveds) {
			dbOTUSet.removeDNAMatrix(toBeRemoved);
		}
		int incomingMatrixPos = -1;
		for (final IDNAMatrix incomingMatrix : incomingOTUSet
				.getDNAMatrices()) {
			incomingMatrixPos++;
			IDNAMatrix dbMatrix;
			if (null == (dbMatrix =
					find(
							dbOTUSet.getDNAMatrices(),
							compose(
									equalTo(
											incomingMatrix.getPPodId()),
									IWithPPodId.getPPodId),
									null
									))) {
				dbMatrix = ModelFactory.newDNAMatrix(newVersionInfo
						.getNewVersionInfo());

				// Do this here because it's non-nullable
				dbMatrix.setLabel(incomingMatrix.getLabel());
				dbOTUSet.addDNAMatrix(incomingMatrixPos, dbMatrix);
				dao.makePersistent(dbMatrix);
			}
			createOrUpdateDNAMatrix
							.createOrUpdateMatrix(
									dbMatrix, incomingMatrix);
		}
	}

	private void handleDNASequenceSets(
			final IOTUSet dbOTUSet,
			final IOTUSet incomingOTUSet) {

		// Let's delete sequences missing from the incoming otu set
		final Set<IDNASequenceSet> toBeRemoveds = newHashSet();
		for (final IDNASequenceSet dbSequenceSet : dbOTUSet
				.getDNASequenceSets()) {
			if (null == find(
					incomingOTUSet.getDNASequenceSets(),
					compose(
							equalTo(
								dbSequenceSet.getPPodId()),
							IWithPPodId.getPPodId),
							null)) {
				toBeRemoveds.add(dbSequenceSet);
			}
		}

		for (final IDNASequenceSet toBeRemoved : toBeRemoveds) {
			dbOTUSet.removeDNASequenceSet(toBeRemoved);
		}
		int incomingSequenceSetPos = -1;
		for (final IDNASequenceSet incomingSequenceSet : incomingOTUSet
				.getDNASequenceSets()) {
			incomingSequenceSetPos++;
			IDNASequenceSet dbDNASequenceSet;
			if (null == (dbDNASequenceSet =
					find(dbOTUSet.getDNASequenceSets(),
							compose(
									equalTo(incomingSequenceSet
											.getPPodId()),
									IWithPPodId.getPPodId),
									null))) {
				dbDNASequenceSet = ModelFactory
						.newDNASequenceSet(newVersionInfo.getNewVersionInfo());
				dbDNASequenceSet.setLabel(incomingSequenceSet.getLabel());
				dbOTUSet.addDNASequenceSet(
						incomingSequenceSetPos, dbDNASequenceSet);
				dao.makePersistent(dbDNASequenceSet);
			}
			mergeDNASequenceSets
					.mergeSequenceSets(dbDNASequenceSet, incomingSequenceSet);
		}
	}

	private void handleStandardMatrices(
			final IOTUSet dbOTUSet,
			final IOTUSet incomingOTUSet) {

		// Let's delete matrices missing from the incoming OTU set
		final Set<IStandardMatrix> toBeRemoveds = newHashSet();
		for (final IStandardMatrix dbMatrix : dbOTUSet.getStandardMatrices()) {
			if (null == find(
							incomingOTUSet.getStandardMatrices(),
							compose(
									equalTo(
										dbMatrix.getPPodId()),
										IWithPPodId.getPPodId),
										null)) {
				toBeRemoveds.add(dbMatrix);
			}
		}

		for (final IStandardMatrix toBeRemoved : toBeRemoveds) {
			dbOTUSet.removeStandardMatrix(toBeRemoved);
		}
		int incomingMatrixPos = -1;
		for (final IStandardMatrix incomingMatrix : incomingOTUSet
				.getStandardMatrices()) {
			incomingMatrixPos++;
			IStandardMatrix dbMatrix;
			if (null == (dbMatrix =
					find(
							dbOTUSet.getStandardMatrices(),
							compose(
									equalTo(
											incomingMatrix.getPPodId()),
									IWithPPodId.getPPodId),
									null))) {
				dbMatrix = ModelFactory.newStandardMatrix(newVersionInfo
						.getNewVersionInfo());
				dbMatrix.setLabel(incomingMatrix.getLabel());
				dbOTUSet.addStandardMatrix(
						incomingMatrixPos,
						dbMatrix);
				dao.makePersistent(dbMatrix);
			}
			createOrUpdateStandardMatrix
					.createOrUpdateMatrix(dbMatrix, incomingMatrix);
		}
	}

	private void handleTreeSets(
			final IOTUSet dbOTUSet,
			final IOTUSet incomingOTUSet) {

		// Let's delete tree sets missing from incoming OTU set
		final Set<ITreeSet> toBeDeleteds = newHashSet();
		for (final ITreeSet dbTreeSet : dbOTUSet.getTreeSets()) {
			if (null == find(incomingOTUSet.getTreeSets(),
						compose(
								equalTo(
										dbTreeSet.getPPodId()),
										IWithPPodId.getPPodId),
										null)) {
				toBeDeleteds.add(dbTreeSet);
			}
		}

		for (final ITreeSet toBeDeleted : toBeDeleteds) {
			dbOTUSet.removeTreeSet(toBeDeleted);
		}

		int incomingTreeSetPos = -1;

		// Now let's add in new ones
		for (final ITreeSet incomingTreeSet : incomingOTUSet.getTreeSets()) {
			incomingTreeSetPos++;
			ITreeSet dbTreeSet;
			if (null == (dbTreeSet =
					find(dbOTUSet.getTreeSets(),
							compose(equalTo(incomingTreeSet.getPPodId()),
									IWithPPodId.getPPodId),
									null))) {
				dbTreeSet = ModelFactory.newTreeSet(newVersionInfo
						.getNewVersionInfo());
				dbTreeSet.setLabel(incomingTreeSet.getLabel());
				dbOTUSet.addTreeSet(incomingTreeSetPos, dbTreeSet);
				dao.makePersistent(dbTreeSet);
			}
			mergeTreeSets.mergeTreeSets(dbTreeSet, incomingTreeSet);
		}
	}
}
