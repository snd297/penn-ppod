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
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IAttachmentNamespaceDAO;
import edu.upenn.cis.ppod.dao.IAttachmentTypeDAO;
import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.dao.IStudyDAO;
import edu.upenn.cis.ppod.imodel.IDNAMatrix;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.IStudy;
import edu.upenn.cis.ppod.imodel.ITreeSet;
import edu.upenn.cis.ppod.imodel.IWithPPodId;
import edu.upenn.cis.ppod.model.DNASequence;
import edu.upenn.cis.ppod.model.DNASequenceSet;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.Study;

/**
 * Create a new study or update an existing one.
 * 
 * @author Sam Donnelly
 */
final class CreateOrUpdateStudy implements ICreateOrUpdateStudy {

	private final IStudyDAO studyDAO;

	private final Provider<Study> studyProvider;
	private final Provider<OTUSet> otuSetProvider;
	private final Provider<StandardMatrix> standardMatrixProvider;
	private final Provider<IDNAMatrix> dnaMatrixProvider;
	private final Provider<DNASequenceSet> dnaSequenceSetProvider;
	private final Provider<ITreeSet> treeSetProvider;

	private final IMergeOTUSets mergeOTUSets;
	private final ICreateOrUpdateStandardMatrix createOrUpdateStandardMatrix;
	private final IMergeTreeSets mergeTreeSets;
	private final INewVersionInfo newVersionInfo;
	private final IMergeSequenceSets<DNASequenceSet, DNASequence> mergeDNASequenceSets;
	private final IStudy incomingStudy;
	private Study dbStudy;
	private final ICreateOrUpdateDNAMatrix createOrUpdateDNAMatrix;
	private final IDAO<Object, Long> dao;

	@Inject
	CreateOrUpdateStudy(
			final Provider<Study> studyProvider,
			final Provider<OTUSet> otuSetProvider,
			final Provider<StandardMatrix> standardMatrix,
			final Provider<DNASequenceSet> dnaSequenceSetProvider,
			final Provider<ITreeSet> treeSetProvider,
			final IMergeOTUSets.IFactory saveOrUpdateOTUSetFactory,
			final IMergeTreeSets.IFactory mergeTreeSetsFactory,
			final ICreateOrUpdateDNAMatrix.IFactory createOrUpdateDNAMatrixFactory,
			final ICreateOrUpdateStandardMatrix.IFactory createOrUpdateMatrixFactory,
			final IMergeSequenceSets.IFactory<DNASequenceSet, DNASequence> mergeDNASequenceSetsFactory,
			final IMergeAttachments.IFactory mergeAttachmentFactory,
			final Provider<IDNAMatrix> dnaMatrixProvider,
			@Assisted final IStudy incomingStudy,
			@Assisted final IStudyDAO studyDAO,
			@Assisted final IAttachmentNamespaceDAO attachmentNamespaceDAO,
			@Assisted final IAttachmentTypeDAO attachmentTypeDAO,
			@Assisted final IDAO<Object, Long> dao,
			@Assisted final INewVersionInfo newVersionInfo) {
		this.incomingStudy = incomingStudy;
		this.studyDAO = studyDAO;
		this.studyProvider = studyProvider;
		this.otuSetProvider = otuSetProvider;
		this.standardMatrixProvider = standardMatrix;
		this.dnaSequenceSetProvider = dnaSequenceSetProvider;
		this.treeSetProvider = treeSetProvider;
		this.newVersionInfo = newVersionInfo;
		this.mergeOTUSets = saveOrUpdateOTUSetFactory
				.create(newVersionInfo);
		this.createOrUpdateDNAMatrix =
				createOrUpdateDNAMatrixFactory
						.create(newVersionInfo, dao);
		this.createOrUpdateStandardMatrix =
				createOrUpdateMatrixFactory.create(
						mergeAttachmentFactory
								.create(attachmentNamespaceDAO,
										attachmentTypeDAO),
										dao,
										newVersionInfo);
		this.mergeDNASequenceSets =
				mergeDNASequenceSetsFactory.create(
						dao,
						newVersionInfo);
		this.mergeTreeSets = mergeTreeSetsFactory.create(newVersionInfo);
		this.dnaMatrixProvider = dnaMatrixProvider;
		this.dao = dao;
	}

	public void createOrUpdateStudy() {
		boolean makeStudyPersistent = false;
		if (null == (dbStudy =
				studyDAO.getStudyByPPodId(
						incomingStudy.getPPodId()))) {
			dbStudy = studyProvider.get();
			dbStudy.setVersionInfo(
					newVersionInfo.getNewVersionInfo());
			dbStudy.setPPodId();
			makeStudyPersistent = true;
		}
		dbStudy.setLabel(incomingStudy.getLabel());

		if (makeStudyPersistent) {
			// It's okay to pass makePersistent a persistent study, but we do
			// this just in case
			// it's an expensive operation - which one could imagine it may be
			// w/ cascades. Though saveOrUpdate cascades don't get to the cells
			// (they get stopped at the OTUKeyedMaps) so it really may not
			// matter anyway.
			studyDAO.makePersistent(dbStudy);
		}

		// Delete otu sets in persisted study that are not in the incoming
		// study.
		for (final IOTUSet dbOTUSet : dbStudy.getOTUSets()) {
			if (null == findIf(
					incomingStudy.getOTUSets(),
					compose(
							equalTo(
									dbOTUSet.getPPodId()),
							IWithPPodId.getPPodId))) {
				dbStudy.removeOTUSet(dbOTUSet);
			}
		}

		// Save or update incoming otu sets
		for (final IOTUSet incomingOTUSet : incomingStudy.getOTUSets()) {
			IOTUSet dbOTUSet;
			if (null == (dbOTUSet =
					findIf(dbStudy.getOTUSets(),
							compose(
									equalTo(
											incomingOTUSet.getPPodId()),
									IWithPPodId.getPPodId)))) {
				dbOTUSet = otuSetProvider.get();
				dbOTUSet.setVersionInfo(newVersionInfo
						.getNewVersionInfo());
				dbOTUSet.setPPodId();
				dbOTUSet.setLabel(incomingOTUSet.getLabel()); // non-null, do it
																// now
				dbOTUSet = dbStudy.addOTUSet(dbOTUSet);
				dao.makePersistent(dbOTUSet);
			}

			mergeOTUSets.mergeOTUSets(dbOTUSet, incomingOTUSet);

			handleDNAMatrices(dbOTUSet, incomingOTUSet);
			handleStandardMatrices(dbOTUSet, incomingOTUSet);
			handleDNASequenceSets(dbOTUSet, incomingOTUSet);
			handleTreeSets(dbOTUSet, incomingOTUSet);
		}
	}

	public Study getDbStudy() {
		return dbStudy;
	}

	private void handleDNAMatrices(
			final IOTUSet dbOTUSet,
			final IOTUSet incomingOTUSet) {

		// Let's delete matrices missing from the incoming OTU set
		for (final IDNAMatrix dbMatrix : dbOTUSet.getDNAMatrices()) {
			if (null == findIf(
							incomingOTUSet.getDNAMatrices(),
							compose(
									equalTo(
										dbMatrix.getPPodId()),
										IWithPPodId.getPPodId))) {
				dbOTUSet.removeDNAMatrix(dbMatrix);
			}
		}

		for (final IDNAMatrix incomingMatrix : incomingOTUSet
				.getDNAMatrices()) {
			IDNAMatrix dbMatrix;
			if (null == (dbMatrix =
					findIf(
							dbOTUSet.getDNAMatrices(),
							compose(
									equalTo(
											incomingMatrix.getPPodId()),
									IWithPPodId.getPPodId
									)))) {
				dbMatrix = dnaMatrixProvider.get();
				dbMatrix.setVersionInfo(
						newVersionInfo.getNewVersionInfo());
				dbMatrix.setColumnVersionInfos(
								newVersionInfo.getNewVersionInfo());
				dbMatrix.setPPodId();

				// Do this here because it's non-nullable
				dbMatrix.setLabel(incomingMatrix.getLabel());
				dbOTUSet.addDNAMatrix(dbMatrix);
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
		for (final DNASequenceSet dbDNASequenceSet : dbOTUSet
				.getDNASequenceSets()) {
			if (null == findIf(
					incomingOTUSet.getDNASequenceSets(),
					compose(
							equalTo(
								dbDNASequenceSet.getPPodId()),
							IWithPPodId.getPPodId))) {
				dbOTUSet.removeDNASequenceSet(dbDNASequenceSet);
			}
		}

		for (final DNASequenceSet incomingDNASequenceSet : incomingOTUSet
				.getDNASequenceSets()) {
			DNASequenceSet dbDNASequenceSet;
			if (null == (dbDNASequenceSet =
					findIf(dbOTUSet.getDNASequenceSets(),
							compose(
									equalTo(incomingDNASequenceSet
											.getPPodId()),
									IWithPPodId.getPPodId)))) {
				dbDNASequenceSet = dnaSequenceSetProvider.get();
				dbDNASequenceSet.setPPodId();
				dbDNASequenceSet.setVersionInfo(newVersionInfo
						.getNewVersionInfo());
				dbDNASequenceSet.setLabel(incomingDNASequenceSet.getLabel());
				dbOTUSet.addDNASequenceSet(dbDNASequenceSet);
			}
			mergeDNASequenceSets
					.mergeSequenceSets(dbDNASequenceSet, incomingDNASequenceSet);
		}
	}

	private void handleStandardMatrices(
			final IOTUSet dbOTUSet,
			final IOTUSet incomingOTUSet) {

		// Let's delete matrices missing from the incoming OTU set
		for (final StandardMatrix dbMatrix : dbOTUSet.getStandardMatrices()) {
			if (null == findIf(
							incomingOTUSet.getStandardMatrices(),
							compose(
									equalTo(
										dbMatrix.getPPodId()),
										IWithPPodId.getPPodId))) {
				dbOTUSet.removeStandardMatrix(dbMatrix);
			}
		}

		for (final StandardMatrix incomingMatrix : incomingOTUSet
				.getStandardMatrices()) {
			StandardMatrix dbMatrix;
			if (null == (dbMatrix =
					findIf(
							dbOTUSet.getStandardMatrices(),
							compose(
									equalTo(
											incomingMatrix.getPPodId()),
									IWithPPodId.getPPodId
									)))) {
				dbMatrix = standardMatrixProvider.get();
				dbMatrix.setVersionInfo(
						newVersionInfo.getNewVersionInfo());
				dbMatrix.setColumnVersionInfos(
								newVersionInfo.getNewVersionInfo());
				dbMatrix.setPPodId();
				dbMatrix.setLabel(incomingMatrix.getLabel());
				dbOTUSet.addStandardMatrix(dbMatrix);
			}
			createOrUpdateStandardMatrix
					.createOrUpdateMatrix(dbMatrix, incomingMatrix);
		}
	}

	private void handleTreeSets(
			final IOTUSet dbOTUSet,
			final IOTUSet incomingOTUSet) {

		// Let's delete tree sets missing from incoming OTU set
		for (final ITreeSet dbTreeSet : dbOTUSet.getTreeSets()) {
			if (null == findIf(incomingOTUSet.getTreeSets(),
						compose(
								equalTo(
										dbTreeSet.getPPodId()),
										IWithPPodId.getPPodId))) {
				dbOTUSet.removeTreeSet(dbTreeSet);
			}
		}

		// Now let's add in new ones
		for (final ITreeSet incomingTreeSet : incomingOTUSet.getTreeSets()) {
			ITreeSet dbTreeSet;
			if (null == (dbTreeSet =
					findIf(dbOTUSet.getTreeSets(),
							compose(equalTo(incomingTreeSet.getPPodId()),
									IWithPPodId.getPPodId)))) {
				dbTreeSet = treeSetProvider.get();
				dbTreeSet.setVersionInfo(newVersionInfo
						.getNewVersionInfo());
				dbTreeSet.setPPodId();
				dbTreeSet.setLabel(incomingTreeSet.getLabel());
				dbOTUSet.addTreeSet(dbTreeSet);
			}
			mergeTreeSets.mergeTreeSets(dbTreeSet, incomingTreeSet);
		}

	}
}
