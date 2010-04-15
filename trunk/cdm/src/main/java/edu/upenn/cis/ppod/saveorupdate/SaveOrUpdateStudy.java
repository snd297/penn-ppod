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

import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IAttachmentNamespaceDAO;
import edu.upenn.cis.ppod.dao.IAttachmentTypeDAO;
import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.dao.IDNACharacterDAO;
import edu.upenn.cis.ppod.dao.IStudyDAO;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.DNACharacter;
import edu.upenn.cis.ppod.model.DNASequence;
import edu.upenn.cis.ppod.model.DNASequenceSet;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.IWithPPodId;
import edu.upenn.cis.ppod.services.ppodentity.CharacterStateMatrixInfo;
import edu.upenn.cis.ppod.services.ppodentity.OTUSetInfo;
import edu.upenn.cis.ppod.services.ppodentity.StudyInfo;
import edu.upenn.cis.ppod.util.ICharacterStateMatrixFactory;

/**
 * Save a new study or update an existing one.
 * 
 * @author Sam Donnelly
 */
final class SaveOrUpdateStudy implements ISaveOrUpdateStudy {

	private final IStudyDAO studyDAO;

	private final IDNACharacterDAO dnaCharacterDAO;

	private final Provider<Study> studyProvider;
	private final Provider<OTUSet> otuSetProvider;
	private final ICharacterStateMatrixFactory matrixFactory;
	private final Provider<DNASequenceSet> dnaSequenceSetProvider;
	private final Provider<TreeSet> treeSetProvider;

	private final IMergeOTUSets mergeOTUSets;
	private final ISaveOrUpdateMatrix mergeMatrices;
	private final IMergeTreeSets mergeTreeSets;
	private final INewPPodVersionInfo newPPodVersionInfo;
	private final IMergeMolecularSequenceSets<DNASequenceSet, DNASequence> mergeDNASequenceSets;
	private final Study incomingStudy;
	private Study dbStudy;
	private final StudyInfo dbStudyInfo;
	private final Provider<OTUSetInfo> otuSetInfoProvider;

	@Inject
	SaveOrUpdateStudy(
			final Provider<Study> studyProvider,
			final Provider<OTUSet> otuSetProvider,
			final ICharacterStateMatrixFactory matrixFactory,
			final Provider<DNASequenceSet> dnaSequenceSetProvider,
			final Provider<TreeSet> treeSetProvider,
			final IMergeOTUSets.IFactory saveOrUpdateOTUSetFactory,
			final IMergeTreeSets.IFactory mergeTreeSetsFactory,
			final ISaveOrUpdateMatrix.IFactory saveOrUpdateMatrixFactory,
			final IMergeMolecularSequenceSets.IFactory<DNASequenceSet, DNASequence> mergeDNASequenceSetsFactory,
			final IMergeAttachments.IFactory mergeAttachmentFactory,
			final Provider<OTUSetInfo> otuSetInfoProvider,
			final StudyInfo studyInfo,
			@Assisted final Study incomingStudy,
			@Assisted final IStudyDAO studyDAO,
			@Assisted final IDNACharacterDAO dnaCharacterDAO,
			@Assisted final IAttachmentNamespaceDAO attachmentNamespaceDAO,
			@Assisted final IAttachmentTypeDAO attachmentTypeDAO,
			@Assisted final IDAO<Object, Long> dao,
			@Assisted final INewPPodVersionInfo newPPodVersionInfo) {
		this.incomingStudy = incomingStudy;
		this.studyDAO = studyDAO;
		this.dnaCharacterDAO = dnaCharacterDAO;
		this.studyProvider = studyProvider;
		this.otuSetProvider = otuSetProvider;
		this.matrixFactory = matrixFactory;
		this.dnaSequenceSetProvider = dnaSequenceSetProvider;
		this.treeSetProvider = treeSetProvider;
		this.newPPodVersionInfo = newPPodVersionInfo;
		this.mergeOTUSets = saveOrUpdateOTUSetFactory
				.create(newPPodVersionInfo);
		this.mergeMatrices = saveOrUpdateMatrixFactory.create(
				mergeAttachmentFactory
						.create(attachmentNamespaceDAO,
								attachmentTypeDAO), dao,
				newPPodVersionInfo);
		this.otuSetInfoProvider = otuSetInfoProvider;
		this.mergeDNASequenceSets = mergeDNASequenceSetsFactory.create(
				dao,
				newPPodVersionInfo);
		this.mergeTreeSets = mergeTreeSetsFactory.create(newPPodVersionInfo);
		this.dbStudyInfo = studyInfo;
	}

	public void saveOrUpdate() {
		dbStudy = (Study) studyProvider.get().setPPodId();
		dbStudy.setPPodVersionInfo(newPPodVersionInfo.getNewPPodVersionInfo());

		if (null == (dbStudy = studyDAO.getStudyByPPodId(incomingStudy
				.getPPodId()))) {
			dbStudy = studyProvider.get();
			dbStudy.setPPodVersionInfo(newPPodVersionInfo
					.getNewPPodVersionInfo());
			dbStudy.setPPodId();
		}

		dbStudy.setLabel(incomingStudy.getLabel());

		// Delete otu sets in persisted study that are not in the incoming
		// study.
		for (final Iterator<OTUSet> dbOTUSetsItr = dbStudy.getOTUSetsIterator(); dbOTUSetsItr
				.hasNext();) {
			final OTUSet dbOTUSet = dbOTUSetsItr.next();
			if (null == findIf(incomingStudy.getOTUSetsIterator(), compose(
					equalTo(dbOTUSet.getPPodId()), IWithPPodId.getPPodId))) {
				dbStudy.removeOTUSet(dbOTUSet);
			}
		}

		final List<DNACharacter> dnaCharacters = dnaCharacterDAO.findAll();
		if (dnaCharacters.size() == 0) {
			throw new IllegalStateException(
					"there are no DNACharacter's in the database: has it been populated with a DNA_STATE character and DNA_STATE states?");
		} else if (dnaCharacters.size() > 1) {
			throw new AssertionError(
					"there are "
							+ dnaCharacters.size()
							+ " DNACharacter's in the database, it should not be possible for there to be more than 1");
		}
		final DNACharacter dbDNACharacter = dnaCharacters.get(0);

		// Save or update incoming otu sets
		for (final Iterator<OTUSet> incomingOTUSetsItr = incomingStudy
				.getOTUSetsIterator(); incomingOTUSetsItr.hasNext();) {
			final OTUSet incomingOTUSet = incomingOTUSetsItr.next();
			OTUSet dbOTUSet;
			if (null == (dbOTUSet = findIf(dbStudy.getOTUSetsIterator(),
					compose(
							equalTo(incomingOTUSet.getPPodId()),
							IWithPPodId.getPPodId)))) {
				dbOTUSet = dbStudy.addOTUSet(otuSetProvider.get());
				dbOTUSet.setPPodVersionInfo(newPPodVersionInfo
						.getNewPPodVersionInfo());
				dbOTUSet.setPPodId();
			}

			mergeOTUSets.merge(dbOTUSet, incomingOTUSet);

			// mergeMatrices needs for dbOTUSet to have an id so that
			// we can give it to the matrix. dbOTUSet needs for dbStudy to
			// have an id. Note that cascade from the study takes care of
			// the OTUSet.
			studyDAO.saveOrUpdate(dbStudy);

			final OTUSetInfo otuSetInfo = otuSetInfoProvider.get();
			dbStudyInfo.getOTUSetInfos().add(otuSetInfo);

			otuSetInfo.setPPodId(dbOTUSet.getPPodId());

			final Set<CharacterStateMatrix> newDbMatrices = newHashSet();
			for (final Iterator<CharacterStateMatrix> incomingMatrixItr = incomingOTUSet
					.getMatricesIterator(); incomingMatrixItr.hasNext();) {
				final CharacterStateMatrix incomingMatrix = incomingMatrixItr
						.next();
				CharacterStateMatrix dbMatrix;
				if (null == (dbMatrix = findIf(dbOTUSet.getMatricesIterator(),
						compose(
								equalTo(incomingMatrix.getPPodId()),
								IWithPPodId.getPPodId)))) {
					dbMatrix = matrixFactory.create(incomingMatrix);
					dbMatrix.setPPodVersionInfo(newPPodVersionInfo
							.getNewPPodVersionInfo());
					dbMatrix.setColumnPPodVersionInfos(newPPodVersionInfo
							.getNewPPodVersionInfo());
					dbMatrix.setPPodId();
				}
				newDbMatrices.add(dbMatrix);
				dbOTUSet.setMatrices(newDbMatrices);
				final CharacterStateMatrixInfo dbMatrixInfo = mergeMatrices
						.saveOrUpdate(dbMatrix,
								incomingMatrix, dbDNACharacter);
				otuSetInfo.getMatrixInfos().add(dbMatrixInfo);
			}

			final Set<DNASequenceSet> newDbDNASequenceSets = newHashSet();
			for (final Iterator<DNASequenceSet> incomingDNASequenceSetItr = incomingOTUSet
					.getDNASequenceSetsIterator(); incomingDNASequenceSetItr
					.hasNext();) {
				final DNASequenceSet incomingDNASequenceSet = incomingDNASequenceSetItr
						.next();
				DNASequenceSet dbDNASequenceSet;
				if (null == (dbDNASequenceSet = findIf(dbOTUSet
						.getDNASequenceSetsIterator(), compose(
						equalTo(incomingDNASequenceSet.getPPodId()),
						IWithPPodId.getPPodId)))) {
					dbDNASequenceSet = dnaSequenceSetProvider.get();
					dbDNASequenceSet.setPPodId();
					dbDNASequenceSet.setPPodVersionInfo(newPPodVersionInfo
							.getNewPPodVersionInfo());
				}
				newDbDNASequenceSets.add(dbDNASequenceSet);
				dbOTUSet.setDNASequenceSets(newDbDNASequenceSets);
				mergeDNASequenceSets.merge(dbDNASequenceSet,
						incomingDNASequenceSet);
			}
			final Set<TreeSet> newDbTreeSets = newHashSet();
			for (final Iterator<TreeSet> incomingTreeSetItr = incomingOTUSet
					.getTreeSetsIterator(); incomingTreeSetItr.hasNext();) {
				final TreeSet incomingTreeSet = incomingTreeSetItr.next();
				TreeSet dbTreeSet;
				if (null == (dbTreeSet = findIf(dbOTUSet.getTreeSetsIterator(),
						compose(equalTo(incomingTreeSet.getPPodId()),
								IWithPPodId.getPPodId)))) {
					dbTreeSet = treeSetProvider.get();
					dbTreeSet.setPPodVersionInfo(newPPodVersionInfo
							.getNewPPodVersionInfo());
					dbTreeSet.setPPodId();
				}
				newDbTreeSets.add(dbTreeSet);
				dbOTUSet.setTreeSets(newDbTreeSets);
				mergeTreeSets.merge(dbTreeSet, incomingTreeSet);

			}
		}
		studyDAO.saveOrUpdate(dbStudy);
	}

	public StudyInfo getStudyInfo() {
		return dbStudyInfo;
	}

	public Study getDbStudy() {
		return dbStudy;
	}

}
