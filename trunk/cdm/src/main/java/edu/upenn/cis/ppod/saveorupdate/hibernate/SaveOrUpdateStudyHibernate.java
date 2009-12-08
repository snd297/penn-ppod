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
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.Map;

import org.hibernate.Session;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IOTUSetDAO;
import edu.upenn.cis.ppod.dao.IStudyDAO;
import edu.upenn.cis.ppod.dao.hibernate.StudyDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.HibernateDAOFactory.OTUSetDAOHibernate;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.IUUPPodEntity;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.saveorupdate.IMergeCharacterStateMatrix;
import edu.upenn.cis.ppod.saveorupdate.IMergeOTUSet;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateStudy;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateTreeSet;

/**
 * @author Sam Donnelly
 */
public class SaveOrUpdateStudyHibernate implements ISaveOrUpdateStudy {

	private final IStudyDAO studyDAO;
	private final IOTUSetDAO otuSetDAO;

	private final Provider<Study> studyProvider;
	private final Provider<OTUSet> otuSetProvider;
	private final Provider<CharacterStateMatrix> matrixProvider;
	private final Provider<TreeSet> treeSetProvider;
	private final IMergeOTUSet mergeOTUSet;
	private final IMergeCharacterStateMatrix mergeCharacterStateMatrix;
	private final ISaveOrUpdateTreeSet saveOrUpdateTreeSet;

	@Inject
	SaveOrUpdateStudyHibernate(
			final StudyDAOHibernate studyDAOHibernate,
			final OTUSetDAOHibernate otuSetDAO,
			final Provider<Study> studyProvider,
			final Provider<OTUSet> otuSetProvider,
			final Provider<CharacterStateMatrix> matrixProvider,
			final Provider<TreeSet> treeSetProvider,
			final IMergeOTUSetHibernateFactory saveOrUpdateOTUSetFactory,
			final IMergeCharacterStateMatrix.IFactory saveOrUpdateMatrixFactory,
			final ISaveOrUpdateTreeSetHibernateFactory saveOrUpdateTreeSetFactory,
			final IMergeAttachmentHibernateFactory saveOrUpdateAttachmentFactory,
			@Assisted final Session session) {
		this.studyDAO = (IStudyDAO) studyDAOHibernate.setSession(session);
		this.otuSetDAO = (IOTUSetDAO) otuSetDAO.setSession(session);
		this.studyProvider = studyProvider;
		this.otuSetProvider = otuSetProvider;
		this.matrixProvider = matrixProvider;
		this.treeSetProvider = treeSetProvider;
		this.mergeOTUSet = saveOrUpdateOTUSetFactory.create(session);
		this.mergeCharacterStateMatrix = saveOrUpdateMatrixFactory
				.create(saveOrUpdateAttachmentFactory.create(session));
		this.saveOrUpdateTreeSet = saveOrUpdateTreeSetFactory.create(session);
	}

	public Study save(final Study incomingStudy) {
		return saveOrUpdate(incomingStudy, (Study) studyProvider.get()
				.setPPodId());
	}

	public Study saveOrUpdate(final Study incomingStudy, final Study dbStudy) {

		dbStudy.setLabel(incomingStudy.getLabel());

		// Delete otu sets in persisted study that are not in the incoming
		// study.
		for (final OTUSet persistedOTUSet : dbStudy.getOTUSets()) {
			if (null == incomingStudy.getOTUSetByPPodId(persistedOTUSet
					.getPPodId())) {
				dbStudy.removeOTUSet(persistedOTUSet);
				otuSetDAO.delete(persistedOTUSet);
			}
		}

		// Save or update incoming otu sets
		for (final OTUSet incomingOTUSet : incomingStudy.getOTUSets()) {
			OTUSet dbOTUSet;
			if (null == (dbOTUSet = dbStudy.getOTUSetByPPodId(incomingOTUSet
					.getPPodId()))) {
				dbOTUSet = dbStudy.addOTUSet(otuSetProvider.get());
				dbOTUSet.setPPodId();
			}
			final Map<OTU, OTU> dbOTUsByIncomingOTU = mergeOTUSet
					.saveOrUpdate(dbOTUSet, incomingOTUSet);
			for (final CharacterStateMatrix incomingMatrix : incomingOTUSet
					.getMatrices()) {
				CharacterStateMatrix dbMatrix;
				if (null == (dbMatrix = findIf(dbOTUSet.getMatrices(), compose(
						equalTo(incomingMatrix.getPPodId()),
						IUUPPodEntity.getPPodId)))) {
					dbMatrix = dbOTUSet.addMatrix(matrixProvider.get());
					dbMatrix.setPPodId();
				}
				dbOTUSet.addMatrix(dbMatrix);
				mergeCharacterStateMatrix.merge(dbMatrix, incomingMatrix,
						dbOTUsByIncomingOTU);
			}
			for (final TreeSet incomingTreeSet : incomingOTUSet.getTreeSets()) {
				TreeSet dbTreeSet;
				if (null == (dbTreeSet = findIf(dbOTUSet.getTreeSets(),
						compose(equalTo(incomingTreeSet.getPPodId()),
								IUUPPodEntity.getPPodId)))) {
					dbTreeSet = dbOTUSet.addTreeSet(treeSetProvider.get());
					dbTreeSet.setPPodId();
				}
				saveOrUpdateTreeSet.saveOrUpdate(incomingTreeSet, dbTreeSet,
						dbOTUSet, dbOTUsByIncomingOTU);
			}
		}

		studyDAO.saveOrUpdate(dbStudy);
		return dbStudy;
	}

	public Study update(final Study incomingStudy) {
		Study persistentStudy;
		if (null == (persistentStudy = studyDAO.getStudyByPPodId(incomingStudy
				.getPPodId()))) {
			throw new IllegalArgumentException("study "
					+ incomingStudy.getLabel() + " "
					+ incomingStudy.getPPodId() + " is not persisted");
		}
		return saveOrUpdate(incomingStudy, persistentStudy);
	}
}
