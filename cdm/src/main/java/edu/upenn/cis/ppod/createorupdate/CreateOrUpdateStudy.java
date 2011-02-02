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
import edu.upenn.cis.ppod.dao.ICurrentVersionDAO;
import edu.upenn.cis.ppod.dao.IDnaMatrixDAO;
import edu.upenn.cis.ppod.dao.IDnaRowDAO;
import edu.upenn.cis.ppod.dao.IDnaSequenceSetDAO;
import edu.upenn.cis.ppod.dao.IOtuSetDAO;
import edu.upenn.cis.ppod.dao.IStandardMatrixDAO;
import edu.upenn.cis.ppod.dao.IStudyDAO;
import edu.upenn.cis.ppod.dao.ITreeSetDAO;
import edu.upenn.cis.ppod.dto.IHasPPodId;
import edu.upenn.cis.ppod.dto.PPodDnaMatrix;
import edu.upenn.cis.ppod.dto.PPodDnaSequenceSet;
import edu.upenn.cis.ppod.dto.PPodOtuSet;
import edu.upenn.cis.ppod.dto.PPodStandardMatrix;
import edu.upenn.cis.ppod.dto.PPodStudy;
import edu.upenn.cis.ppod.dto.PPodTreeSet;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.model.CurrentVersion;
import edu.upenn.cis.ppod.model.DnaMatrix;
import edu.upenn.cis.ppod.model.DnaSequenceSet;
import edu.upenn.cis.ppod.model.OtuSet;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.SetVersionInfoVisitor;

/**
 * Create a new study or update an existing one.
 * 
 * @author Sam Donnelly
 */
public final class CreateOrUpdateStudy {

	private final IStudyDAO studyDAO;
	private final INewVersionInfo newVersionInfo;
	private final MergeOtuSets mergeOTUSets;
	private final CreateOrUpdateDnaMatrix createOrUpdateDNAMatrix;
	private final MergeDnaSequenceSets mergeDNASequenceSets;
	private final CreateOrUpdateStandardMatrix createOrUpdateStandardMatrix;
	private final MergeTreeSets mergeTreeSets;
	private final ICurrentVersionDAO currentVersionDAO;
	private final IOtuSetDAO otuSetDAO;
	private final IDnaSequenceSetDAO dnaSequenceSetDAO;
	private final IDnaMatrixDAO dnaMatrixDAO;
	private final IStandardMatrixDAO standardMatrixDAO;
	private final ITreeSetDAO treeSetDAO;

	@Inject
	CreateOrUpdateStudy(
			final IDnaRowDAO dnaRowDao,
			final IAttachmentNamespaceDAO attachmentNamespaceDAO,
			final IAttachmentTypeDAO attachmentTypeDAO,
			final IStudyDAO studyDAO,
			final INewVersionInfo newVersionInfo,
			final MergeOtuSets mergeOTUSets,
			final CreateOrUpdateDnaMatrix createOrUpdateDNAMatrix,
			final MergeDnaSequenceSets mergeDNASequenceSets,
			final CreateOrUpdateStandardMatrix createOrUpdateStandardMatrix,
			final MergeTreeSets mergeTreeSets,
			final ICurrentVersionDAO currentVersionDAO,
			final IOtuSetDAO otuSetDAO,
			final IDnaSequenceSetDAO dnaSequenceSetDAO,
			final IDnaMatrixDAO dnaMatrixDAO,
			final IStandardMatrixDAO standardMatrixDAO,
			final ITreeSetDAO treeSetDAO) {
		this.studyDAO = studyDAO;
		this.newVersionInfo = newVersionInfo;
		this.mergeOTUSets = mergeOTUSets;
		this.createOrUpdateDNAMatrix = createOrUpdateDNAMatrix;
		this.mergeDNASequenceSets = mergeDNASequenceSets;
		this.createOrUpdateStandardMatrix = createOrUpdateStandardMatrix;
		this.mergeTreeSets = mergeTreeSets;
		this.currentVersionDAO = currentVersionDAO;
		this.otuSetDAO = otuSetDAO;
		this.dnaSequenceSetDAO = dnaSequenceSetDAO;
		this.dnaMatrixDAO = dnaMatrixDAO;
		this.standardMatrixDAO = standardMatrixDAO;
		this.treeSetDAO = treeSetDAO;
	}

	public Study createOrUpdateStudy(final PPodStudy incomingStudy) {
		Study dbStudy = null;
		boolean makeStudyPersistent = false;
		if (null == (dbStudy =
				studyDAO.getStudyByPPodId(
						incomingStudy.getPPodId()))) {
			dbStudy = new Study();
			dbStudy.setVersionInfo(newVersionInfo.getNewVersionInfo());
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
				dbOtuSet.setVersionInfo(newVersionInfo.getNewVersionInfo());
				dbOtuSet.setLabel(incomingOtuSet.getLabel()); // non-null, do it
				// now
				dbStudy.addOtuSet(incomingOtuSetPos, dbOtuSet);
				otuSetDAO.makePersistent(dbOtuSet);
			}

			mergeOTUSets.mergeOTUSets(dbOtuSet, incomingOtuSet);

			handleDNAMatrices(dbOtuSet, incomingOtuSet);
			handleStandardMatrices(dbOtuSet, incomingOtuSet);
			handleDNASequenceSets(dbOtuSet, incomingOtuSet);
			handleTreeSets(dbOtuSet, incomingOtuSet);
		}
		final IVisitor setVersionInfoVisitor = new SetVersionInfoVisitor(
				newVersionInfo);

		dbStudy.accept(setVersionInfoVisitor);

		if (newVersionInfo.newVersionWasDealtOut()) {

			CurrentVersion currentVersion = currentVersionDAO.findById(
					CurrentVersion.ID,
					true);

			if (currentVersion == null) {
				currentVersion = new CurrentVersion(1L);
				currentVersionDAO.makePersistent(currentVersion);
			} else {
				currentVersion.setVersion(currentVersion.getVersion() + 1);
			}
			newVersionInfo.getNewVersionInfo().setVersion(
					currentVersion.getVersion());
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
				dbMatrix = new DnaMatrix();
				dbMatrix.setVersionInfo(newVersionInfo.getNewVersionInfo());

				// Do this here because it's non-nullable
				dbMatrix.setLabel(incomingMatrix.getLabel());
				dbOTUSet.addDnaMatrix(incomingMatrixPos, dbMatrix);
				dnaMatrixDAO.makePersistent(dbMatrix);
			}
			createOrUpdateDNAMatrix
							.createOrUpdateMatrix(
									dbMatrix, incomingMatrix);
		}
	}

	private void handleDNASequenceSets(
			final OtuSet dbOtuSet,
			final PPodOtuSet incomingOTUSet) {

		// Let's delete sequences missing from the incoming otu set
		final Set<DnaSequenceSet> toBeRemoveds = newHashSet();
		for (final DnaSequenceSet dbSequenceSet : dbOtuSet
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
			dbOtuSet.removeDnaSequenceSet(toBeRemoved);
		}
		int incomingSequenceSetPos = -1;
		for (final PPodDnaSequenceSet incomingSequenceSet : incomingOTUSet
				.getDnaSequenceSets()) {
			incomingSequenceSetPos++;
			DnaSequenceSet dbDnaSequenceSet;
			if (null == (dbDnaSequenceSet =
					find(dbOtuSet.getDnaSequenceSets(),
							compose(
									equalTo(incomingSequenceSet
											.getPPodId()),
									IHasPPodId.getPPodId),
									null))) {
				dbDnaSequenceSet = new DnaSequenceSet();
				dbDnaSequenceSet.setVersionInfo(newVersionInfo
						.getNewVersionInfo());
				dbDnaSequenceSet.setLabel(incomingSequenceSet.getLabel());
				dbOtuSet.addDnaSequenceSet(
						incomingSequenceSetPos, dbDnaSequenceSet);
				dnaSequenceSetDAO.makePersistent(dbDnaSequenceSet);
			}
			mergeDNASequenceSets
					.mergeSequenceSets(dbDnaSequenceSet, incomingSequenceSet);
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
				dbMatrix.setVersionInfo(newVersionInfo.getNewVersionInfo());
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
				dbTreeSet.setVersionInfo(newVersionInfo.getNewVersionInfo());
				dbTreeSet.setLabel(incomingTreeSet.getLabel());
				dbOtuSet.addTreeSet(incomingTreeSetPos, dbTreeSet);
				treeSetDAO.makePersistent(dbTreeSet);
			}
			mergeTreeSets.mergeTreeSets(dbTreeSet, incomingTreeSet,
					incomingOtuSet);
		}
	}
}
