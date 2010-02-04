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

import static edu.upenn.cis.ppod.util.PPodIterables.equalTo;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.List;
import java.util.Map;

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
import edu.upenn.cis.ppod.dao.hibernate.StudyDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.HibernateDAOFactory.OTUSetDAOHibernate;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.DNACharacter;
import edu.upenn.cis.ppod.model.DNAStateMatrix;
import edu.upenn.cis.ppod.model.IUUPPodEntity;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.saveorupdate.IMergeAttachment;
import edu.upenn.cis.ppod.saveorupdate.IMergeCharacterStateMatrix;
import edu.upenn.cis.ppod.saveorupdate.IMergeOTUSet;
import edu.upenn.cis.ppod.saveorupdate.IMergeTreeSet;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateStudy;
import edu.upenn.cis.ppod.saveorupdate.MergeTreeSet;

/**
 * @author Sam Donnelly
 */
public class SaveOrUpdateStudyHibernate implements ISaveOrUpdateStudy {

	private final IStudyDAO studyDAO;
	private final IOTUSetDAO otuSetDAO;
	private final IDNACharacterDAO dnaCharacterDAO;

	private final Provider<Study> studyProvider;
	private final Provider<OTUSet> otuSetProvider;
	private final Provider<CharacterStateMatrix> characterStateMatrixProvider;
	private final Provider<DNAStateMatrix> dnaMatrixProvider;
	private final Provider<TreeSet> treeSetProvider;

	private final IMergeOTUSet mergeOTUSet;
	private final IMergeCharacterStateMatrix mergeMatrix;
	private final IMergeTreeSet mergeTreeSet;
	private boolean save;

	@Inject
	SaveOrUpdateStudyHibernate(
			final StudyDAOHibernate studyDAOHibernate,
			final OTUSetDAOHibernate otuSetDAO,
			final DNACharacterDAOHibernate dnaCharacterDAO,
			final Provider<Study> studyProvider,
			final Provider<OTUSet> otuSetProvider,
			final Provider<CharacterStateMatrix> characterStateMatrixProvider,
			final Provider<DNAStateMatrix> dnaMatrixProvider,
			final Provider<TreeSet> treeSetProvider,
			final IMergeOTUSetHibernateFactory saveOrUpdateOTUSetFactory,
			final IMergeCharacterStateMatrix.IFactory mergeMatrixFactory,
			final IAttachmentNamespaceDAOHibernateFactory attachmentNamespaceDAOFactory,
			final IAttachmentTypeDAOHibernateFactory attachmentTypeDAOFactory,
			final IMergeAttachment.IFactory mergeAttachmentFactory,
			final MergeTreeSet mergeTreeSet, @Assisted final Session session) {
		this.studyDAO = (IStudyDAO) studyDAOHibernate.setSession(session);
		this.otuSetDAO = (IOTUSetDAO) otuSetDAO.setSession(session);
		this.dnaCharacterDAO = (IDNACharacterDAO) dnaCharacterDAO
				.setSession(session);
		this.studyProvider = studyProvider;
		this.otuSetProvider = otuSetProvider;
		this.characterStateMatrixProvider = characterStateMatrixProvider;
		this.dnaMatrixProvider = dnaMatrixProvider;
		this.treeSetProvider = treeSetProvider;
		this.mergeOTUSet = saveOrUpdateOTUSetFactory.create(session);
		this.mergeMatrix = mergeMatrixFactory.create(mergeAttachmentFactory
				.create(attachmentNamespaceDAOFactory.create(session),
						attachmentTypeDAOFactory.create(session)), session);
		this.mergeTreeSet = mergeTreeSet;
	}

	public Study save(final Study incomingStudy) {
		this.save = true;
		return saveOrUpdate((Study) studyProvider.get().setPPodId(),
				incomingStudy);
	}

	public Study saveOrUpdate(final Study dbStudy, final Study incomingStudy) {

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
			if (null == (dbOTUSet = dbStudy.getOTUSetByPPodId(incomingOTUSet
					.getPPodId()))) {
				dbOTUSet = dbStudy.addOTUSet(otuSetProvider.get());
				dbOTUSet.setPPodId();
			}
			final Map<OTU, OTU> dbOTUsByIncomingOTU = mergeOTUSet.saveOrUpdate(
					dbOTUSet, incomingOTUSet);
			for (final CharacterStateMatrix incomingMatrix : incomingOTUSet
					.getMatrices()) {
				CharacterStateMatrix dbMatrix;
				if (null == (dbMatrix = findIf(dbOTUSet.getMatrices(), equalTo(
						incomingMatrix.getPPodId(), IUUPPodEntity.getPPodId)))) {
					switch (incomingMatrix.getType()) {
						case STANDARD:
							dbMatrix = characterStateMatrixProvider.get();
							break;
						case DNA:
							dbMatrix = dnaMatrixProvider.get();
							break;
						default:
							throw new IllegalArgumentException(
									"unsupported matrix type: "
											+ incomingMatrix.getType());
					}
					dbMatrix.setPPodId();
				}

				mergeMatrix.saveOrUpdate(dbMatrix, incomingMatrix, dbOTUSet,
						dbOTUsByIncomingOTU, dbDNACharacter);
			}
			for (final TreeSet incomingTreeSet : incomingOTUSet.getTreeSets()) {
				TreeSet dbTreeSet;
				if (null == (dbTreeSet = findIf(dbOTUSet.getTreeSets(),
						equalTo(incomingTreeSet.getPPodId(),
								IUUPPodEntity.getPPodId)))) {
					dbTreeSet = treeSetProvider.get();
					dbTreeSet.setPPodId();
				}
				mergeTreeSet.merge(dbTreeSet, incomingTreeSet, dbOTUSet,
						dbOTUsByIncomingOTU);
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
		return saveOrUpdate(persistentStudy, incomingStudy);
	}
}
