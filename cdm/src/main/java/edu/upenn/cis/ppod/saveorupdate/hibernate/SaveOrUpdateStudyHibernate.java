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

import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IDNACharacterDAO;
import edu.upenn.cis.ppod.dao.IOTUSetDAO;
import edu.upenn.cis.ppod.dao.IStudyDAO;
import edu.upenn.cis.ppod.dao.hibernate.DNACharacterDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.IAttachmentNamespaceDAOHibernateFactory;
import edu.upenn.cis.ppod.dao.hibernate.IAttachmentTypeDAOHibernateFactory;
import edu.upenn.cis.ppod.dao.hibernate.ObjectWLongIdDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.StudyDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.HibernateDAOFactory.OTUSetDAOHibernate;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.DNACharacter;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.IUUPPodEntity;
import edu.upenn.cis.ppod.saveorupdate.IMergeAttachments;
import edu.upenn.cis.ppod.saveorupdate.IMergeOTUSetFactory;
import edu.upenn.cis.ppod.saveorupdate.IMergeOTUSets;
import edu.upenn.cis.ppod.saveorupdate.IMergeTreeSets;
import edu.upenn.cis.ppod.saveorupdate.IMergeTreeSetsFactory;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateMatrix;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateMatrixFactory;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateStudy;
import edu.upenn.cis.ppod.util.ICharacterStateMatrixFactory;

/**
 * Save a new study or update an existing one.
 * 
 * @author Sam Donnelly
 */
public class SaveOrUpdateStudyHibernate implements ISaveOrUpdateStudy {

	private final IStudyDAO studyDAO;
	private final IOTUSetDAO otuSetDAO;
	private final IDNACharacterDAO dnaCharacterDAO;

	private final Provider<Study> studyProvider;
	private final Provider<OTUSet> otuSetProvider;
	private final ICharacterStateMatrixFactory matrixFactory;
	private final Provider<TreeSet> treeSetProvider;

	private final IMergeOTUSets mergeOTUSets;
	private final ISaveOrUpdateMatrix saveOrUpdateMatrix;
	private final IMergeTreeSets mergeTreeSets;
	private final INewPPodVersionInfo newPPodVersionInfo;

	@Inject
	SaveOrUpdateStudyHibernate(
			final StudyDAOHibernate studyDAO,
			final OTUSetDAOHibernate otuSetDAO,
			final DNACharacterDAOHibernate dnaCharacterDAO,
			final ObjectWLongIdDAOHibernate dao,
			final Provider<Study> studyProvider,
			final Provider<OTUSet> otuSetProvider,
			final ICharacterStateMatrixFactory matrixFactory,
			final Provider<TreeSet> treeSetProvider,
			final IMergeOTUSetFactory saveOrUpdateOTUSetFactory,
			final IMergeTreeSetsFactory mergeTreeSetsFactory,
			final ISaveOrUpdateMatrixFactory mergeMatrixFactory,
			final IAttachmentNamespaceDAOHibernateFactory attachmentNamespaceDAOFactory,
			final IAttachmentTypeDAOHibernateFactory attachmentTypeDAOFactory,
			final IMergeAttachments.IFactory mergeAttachmentFactory,
			@Assisted final Session session,
			@Assisted final INewPPodVersionInfo newPPodVersionInfo) {

		this.studyDAO = (IStudyDAO) studyDAO.setSession(session);
		this.otuSetDAO = (IOTUSetDAO) otuSetDAO.setSession(session);
		this.dnaCharacterDAO = (IDNACharacterDAO) dnaCharacterDAO
				.setSession(session);
		this.studyProvider = studyProvider;
		this.otuSetProvider = otuSetProvider;
		this.matrixFactory = matrixFactory;
		this.treeSetProvider = treeSetProvider;
		this.newPPodVersionInfo = newPPodVersionInfo;
		this.mergeOTUSets = saveOrUpdateOTUSetFactory.create(newPPodVersionInfo);
		this.saveOrUpdateMatrix = mergeMatrixFactory.create(
				mergeAttachmentFactory.create(attachmentNamespaceDAOFactory
						.create(session), attachmentTypeDAOFactory
						.create(session)), dao.setSession(session),
				newPPodVersionInfo);
		this.mergeTreeSets = mergeTreeSetsFactory.create(newPPodVersionInfo);
	}

	public Study save(final Study incomingStudy) {
		final Study dbStudy = (Study) studyProvider.get().setPPodId();
		dbStudy.setPPodVersionInfo(newPPodVersionInfo.getNewPPodVersionInfo());
		saveOrUpdate(dbStudy, incomingStudy);
		return dbStudy;
	}

	public void saveOrUpdate(final Study dbStudy, final Study incomingStudy) {

		dbStudy.setLabel(incomingStudy.getLabel());

		// Delete otu sets in persisted study that are not in the incoming
		// study.
		for (final OTUSet dbOTUSet : dbStudy.getOTUSets()) {
			if (null == findIf(incomingStudy.getOTUSets(), compose(
					equalTo(dbOTUSet.getPPodId()), IUUPPodEntity.getPPodId))) {
				dbStudy.removeOTUSet(dbOTUSet);
				otuSetDAO.delete(dbOTUSet);
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
		for (final OTUSet incomingOTUSet : incomingStudy.getOTUSets()) {
			OTUSet dbOTUSet;
			if (null == (dbOTUSet = findIf(dbStudy.getOTUSets(), compose(
					equalTo(incomingOTUSet.getPPodId()),
					IUUPPodEntity.getPPodId)))) {
				dbOTUSet = dbStudy.addOTUSet(otuSetProvider.get());
				dbOTUSet.setPPodVersionInfo(newPPodVersionInfo
						.getNewPPodVersionInfo());
				dbOTUSet.setPPodId();
			}

			final Map<OTU, OTU> dbOTUsByIncomingOTU = mergeOTUSets.merge(
					dbOTUSet, incomingOTUSet);

			// saveOrUpdateMatrix needs for dbOTUSet to have an id so that
			// we can give it to the matrix. dbOTUSet needs for dbStudy to
			// have an id. Note that cascade from the study takes care of
			// the OTUSet.
			studyDAO.saveOrUpdate(dbStudy);

			final Set<CharacterStateMatrix> newDbMatrices = newHashSet();
			for (final CharacterStateMatrix incomingMatrix : incomingOTUSet
					.getMatrices()) {
				CharacterStateMatrix dbMatrix;
				if (null == (dbMatrix = findIf(dbOTUSet.getMatrices(), compose(
						equalTo(incomingMatrix.getPPodId()),
						IUUPPodEntity.getPPodId)))) {
					dbMatrix = matrixFactory.create(incomingMatrix);
					dbMatrix.setPPodVersionInfo(newPPodVersionInfo
							.getNewPPodVersionInfo());
					dbMatrix.setColumnPPodVersionInfos(newPPodVersionInfo
							.getNewPPodVersionInfo());
					dbMatrix.setPPodId();
				}
				newDbMatrices.add(dbMatrix);
				dbOTUSet.setMatrices(newDbMatrices);

				saveOrUpdateMatrix.saveOrUpdate(dbMatrix, incomingMatrix,
						dbOTUsByIncomingOTU, dbDNACharacter);

			}
			final Set<TreeSet> newDbTreeSets = newHashSet();
			for (final TreeSet incomingTreeSet : incomingOTUSet.getTreeSets()) {
				TreeSet dbTreeSet;
				if (null == (dbTreeSet = findIf(dbOTUSet.getTreeSets(),
						compose(equalTo(incomingTreeSet.getPPodId()),
								IUUPPodEntity.getPPodId)))) {
					dbTreeSet = treeSetProvider.get();
					dbTreeSet.setPPodVersionInfo(newPPodVersionInfo
							.getNewPPodVersionInfo());
					dbTreeSet.setPPodId();
				}
				newDbTreeSets.add(dbTreeSet);
				dbOTUSet.setTreeSets(newDbTreeSets);
				mergeTreeSets.merge(dbTreeSet, incomingTreeSet,
						dbOTUsByIncomingOTU);
			}
		}
		studyDAO.saveOrUpdate(dbStudy);
	}

	public Study update(final Study incomingStudy) {
		Study persistentStudy;
		if (null == (persistentStudy = studyDAO.getStudyByPPodId(incomingStudy
				.getPPodId()))) {
			throw new IllegalArgumentException("study "
					+ incomingStudy.getLabel() + " "
					+ incomingStudy.getPPodId() + " is not persisted");
		}
		saveOrUpdate(persistentStudy, incomingStudy);
		return persistentStudy;
	}
}
