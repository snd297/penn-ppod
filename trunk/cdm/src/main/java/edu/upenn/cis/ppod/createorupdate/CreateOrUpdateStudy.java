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
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IAttachmentNamespaceDAO;
import edu.upenn.cis.ppod.dao.IAttachmentTypeDAO;
import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.dao.IStudyDAO;
import edu.upenn.cis.ppod.model.DNAMatrix;
import edu.upenn.cis.ppod.model.DNASequence;
import edu.upenn.cis.ppod.model.DNASequenceSet;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.modelinterfaces.INewVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.IWithPPodId;
import edu.upenn.cis.ppod.services.ppodentity.MatrixInfo;
import edu.upenn.cis.ppod.services.ppodentity.OTUSetInfo;
import edu.upenn.cis.ppod.services.ppodentity.StudyInfo;

/**
 * Save a new study or update an existing one.
 * 
 * @author Sam Donnelly
 */
final class CreateOrUpdateStudy implements ICreateOrUpdateStudy {

	private final IStudyDAO studyDAO;

	private final Provider<Study> studyProvider;
	private final Provider<OTUSet> otuSetProvider;
	private final Provider<StandardMatrix> standardMatrixProvider;
	private final Provider<DNAMatrix> dnaMatrixProvider;
	private final Provider<DNASequenceSet> dnaSequenceSetProvider;
	private final Provider<TreeSet> treeSetProvider;

	private final IMergeOTUSets mergeOTUSets;
	private final ICreateOrUpdateStandardMatrix createOrUpdateStandardMatrix;
	private final IMergeTreeSets mergeTreeSets;
	private final INewVersionInfo newVersionInfo;
	private final IMergeSequenceSets<DNASequenceSet, DNASequence> mergeDNASequenceSets;
	private final Study incomingStudy;
	private Study dbStudy;
	private final StudyInfo dbStudyInfo;
	private final Provider<OTUSetInfo> otuSetInfoProvider;
	private final ICreateOrUpdateDNAMatrix createOrUpdateDNAMatrix;

	@Inject
	CreateOrUpdateStudy(
			final Provider<Study> studyProvider,
			final Provider<OTUSet> otuSetProvider,
			final Provider<StandardMatrix> standardMatrix,
			final Provider<DNASequenceSet> dnaSequenceSetProvider,
			final Provider<TreeSet> treeSetProvider,
			final IMergeOTUSets.IFactory saveOrUpdateOTUSetFactory,
			final IMergeTreeSets.IFactory mergeTreeSetsFactory,
			final ICreateOrUpdateDNAMatrix.IFactory saveOrUpdateDNAMatrixFactory,
			final ICreateOrUpdateStandardMatrix.IFactory saveOrUpdateMatrixFactory,
			final IMergeSequenceSets.IFactory<DNASequenceSet, DNASequence> mergeDNASequenceSetsFactory,
			final IMergeAttachments.IFactory mergeAttachmentFactory,
			final Provider<OTUSetInfo> otuSetInfoProvider,
			final StudyInfo studyInfo,
			final Provider<DNAMatrix> dnaMatrixProvider,
			@Assisted final Study incomingStudy,
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
				saveOrUpdateDNAMatrixFactory.create(
						newVersionInfo, dao);
		this.createOrUpdateStandardMatrix =
				saveOrUpdateMatrixFactory.create(
						mergeAttachmentFactory
								.create(attachmentNamespaceDAO,
										attachmentTypeDAO),
										dao,
										newVersionInfo);
		this.otuSetInfoProvider = otuSetInfoProvider;
		this.mergeDNASequenceSets =
				mergeDNASequenceSetsFactory.create(
						dao,
						newVersionInfo);
		this.mergeTreeSets = mergeTreeSetsFactory.create(newVersionInfo);
		this.dbStudyInfo = studyInfo;
		this.dnaMatrixProvider = dnaMatrixProvider;
	}

	public void createOrUpdateStudy() {
		if (null == (dbStudy =
				studyDAO.getStudyByPPodId(
						incomingStudy.getPPodId()))) {
			dbStudy = studyProvider.get();
			dbStudy.setVersionInfo(newVersionInfo
									.getNewVersionInfo());
			dbStudy.setPPodId();
		}

		dbStudy.setLabel(incomingStudy.getLabel());

		// Delete otu sets in persisted study that are not in the incoming
		// study.
		for (final OTUSet dbOTUSet : dbStudy.getOTUSets()) {
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
		for (final OTUSet incomingOTUSet : incomingStudy.getOTUSets()) {
			OTUSet dbOTUSet;
			if (null == (dbOTUSet =
					findIf(dbStudy.getOTUSets(),
							compose(
									equalTo(
											incomingOTUSet.getPPodId()),
									IWithPPodId.getPPodId)))) {
				dbOTUSet = dbStudy.addOTUSet(otuSetProvider.get());
				dbOTUSet.setVersionInfo(newVersionInfo
						.getNewVersionInfo());
				dbOTUSet.setPPodId();
			}

			mergeOTUSets.mergeOTUSets(dbOTUSet, incomingOTUSet);

			// mergeMatrices needs for dbOTUSet to have an id so that
			// we can give it to the matrix. dbOTUSet needs for dbStudy to
			// have an id. Note that cascade from the study takes care of
			// the OTUSet.
			studyDAO.makePersistent(dbStudy);

			final OTUSetInfo otuSetInfo = otuSetInfoProvider.get();
			dbStudyInfo.getOTUSetInfos().add(otuSetInfo);

			otuSetInfo.setPPodId(dbOTUSet.getPPodId());

			handleStandardMatrices(dbOTUSet, incomingOTUSet, otuSetInfo);
			handleDNAMatrices(dbOTUSet, incomingOTUSet, otuSetInfo);
			handleDNASequenceSets(dbOTUSet, incomingOTUSet);

			final Set<TreeSet> newDbTreeSets = newHashSet();
			for (final TreeSet incomingTreeSet : incomingOTUSet.getTreeSets()) {
				TreeSet dbTreeSet;
				if (null == (dbTreeSet =
						findIf(dbOTUSet.getTreeSets(),
								compose(equalTo(incomingTreeSet.getPPodId()),
										IWithPPodId.getPPodId)))) {
					dbTreeSet = treeSetProvider.get();
					dbTreeSet.setVersionInfo(newVersionInfo
							.getNewVersionInfo());
					dbTreeSet.setPPodId();
				}
				newDbTreeSets.add(dbTreeSet);
				dbOTUSet.setTreeSets(newDbTreeSets);
				mergeTreeSets.mergeTreeSets(dbTreeSet, incomingTreeSet);

			}
		}
		studyDAO.makePersistent(dbStudy);
	}

	public Study getDbStudy() {
		return dbStudy;
	}

	public StudyInfo getStudyInfo() {
		return dbStudyInfo;
	}

	private void handleDNAMatrices(
			final OTUSet dbOTUSet,
			final OTUSet incomingOTUSet,
			final OTUSetInfo otuSetInfo) {

		// Let's delete matrices missing from the incoming OTU set
		for (final DNAMatrix dbMatrix : dbOTUSet.getDNAMatrices()) {
			if (null == findIf(
							incomingOTUSet.getDNAMatrices(),
							compose(
									equalTo(
										dbMatrix.getPPodId()),
										IWithPPodId.getPPodId))) {
				dbOTUSet.removeDNAMatrix(dbMatrix);
			}
		}

		for (final DNAMatrix incomingMatrix : incomingOTUSet
				.getDNAMatrices()) {
			DNAMatrix dbMatrix;
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
				dbOTUSet.addDNAMatrix(dbMatrix);
			}
			final MatrixInfo dbMatrixInfo =
					createOrUpdateDNAMatrix
							.createOrUpdateMatrix(
									dbMatrix, incomingMatrix);
			otuSetInfo.getMatrixInfos().add(dbMatrixInfo);
		}
	}

	private void handleDNASequenceSets(
			final OTUSet dbOTUSet,
			final OTUSet incomingOTUSet) {

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
			}
			dbOTUSet.addDNASequenceSet(dbDNASequenceSet);
			mergeDNASequenceSets
					.mergeSequenceSets(dbDNASequenceSet, incomingDNASequenceSet);
		}
	}

	private void handleStandardMatrices(
			final OTUSet dbOTUSet,
			final OTUSet incomingOTUSet,
			final OTUSetInfo otuSetInfo) {

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
				dbOTUSet.addStandardMatrix(dbMatrix);
			}
			final MatrixInfo dbMatrixInfo = createOrUpdateStandardMatrix
					.createOrUpdateMatrix(dbMatrix, incomingMatrix);
			otuSetInfo.getMatrixInfos().add(dbMatrixInfo);
		}
	}
}
