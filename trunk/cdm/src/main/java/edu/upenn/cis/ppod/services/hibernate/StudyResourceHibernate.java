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
package edu.upenn.cis.ppod.services.hibernate;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import org.hibernate.Session;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.dao.IAttachmentNamespaceDAO;
import edu.upenn.cis.ppod.dao.IAttachmentTypeDAO;
import edu.upenn.cis.ppod.dao.IDNACharacterDAO;
import edu.upenn.cis.ppod.dao.IOTUSetDAO;
import edu.upenn.cis.ppod.dao.IObjectWithLongIdDAO;
import edu.upenn.cis.ppod.dao.IStudyDAO;
import edu.upenn.cis.ppod.dao.hibernate.IAttachmentNamespaceDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.IAttachmentTypeDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.IDNACharacterDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.IOTUSetDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.IObjectWithLongIdDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.IStudyDAOHibernate;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfoHibernate;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateStudy;
import edu.upenn.cis.ppod.services.IStudyResource;
import edu.upenn.cis.ppod.services.StringPair;
import edu.upenn.cis.ppod.services.ppodentity.IStudy2StudyInfo;
import edu.upenn.cis.ppod.services.ppodentity.StudyInfo;
import edu.upenn.cis.ppod.thirdparty.util.HibernateUtil;
import edu.upenn.cis.ppod.util.IAfterUnmarshalVisitor;
import edu.upenn.cis.ppod.util.IPair;
import edu.upenn.cis.ppod.util.ISetPPodVersionInfoVisitor;
import edu.upenn.cis.ppod.util.SetDocIdVisitor;

/**
 * @author Sam Donnelly
 */
final class StudyResourceHibernate implements IStudyResource {

	private final IStudyDAO studyDAO;

	private final ISaveOrUpdateStudy saveOrUpdateStudy;

	private final IStudy2StudyInfo study2StudyInfo;

	private final SetDocIdVisitor setDocIdVisitor;

	private final Provider<IAfterUnmarshalVisitor> afterUnmarshalVisitorProvider;

	private final ISetPPodVersionInfoVisitor setPPodVersionInfoVisitor;

	private final StringPair.IFactory stringPairFactory;

	@Inject
	StudyResourceHibernate(
			final IStudyDAOHibernate studyDAO,
			final IOTUSetDAOHibernate otuSetDAO,
			final ISaveOrUpdateStudy.IFactory saveOrUpdateStudyFactory,
			final IStudy2StudyInfo study2StudyInfo,
			final SetDocIdVisitor setDocIdVisitor,
			final Provider<IAfterUnmarshalVisitor> afterUnmarshalVisitorProvider,
			final INewPPodVersionInfoHibernate.IFactory newPPodVersionInfoFactory,
			final ISetPPodVersionInfoVisitor.IFactory setPPodVersionInfoVisitorFactory,
			final StringPair.IFactory stringPairFactory,
			final IDNACharacterDAOHibernate dnaCharacterDAO,
			final IAttachmentNamespaceDAOHibernate attachmentNamespaceDAO,
			final IAttachmentTypeDAOHibernate attachmentTypeDAO,
			final IObjectWithLongIdDAOHibernate dao) {
		this.studyDAO = (IStudyDAO) studyDAO.setSession(HibernateUtil
				.getSessionFactory().getCurrentSession());
		final Session currentSession = HibernateUtil
				.getSessionFactory().getCurrentSession();

		final INewPPodVersionInfo newPPodVersionInfo = newPPodVersionInfoFactory
				.create(currentSession);
		this.setPPodVersionInfoVisitor = setPPodVersionInfoVisitorFactory
				.create(newPPodVersionInfo);
		this.saveOrUpdateStudy = saveOrUpdateStudyFactory.create(
				currentSession,
				(IStudyDAO) studyDAO.setSession(currentSession),
				(IOTUSetDAO) otuSetDAO.setSession(currentSession),
				(IDNACharacterDAO) dnaCharacterDAO.setSession(currentSession),
				(IAttachmentNamespaceDAO) attachmentNamespaceDAO
						.setSession(currentSession),
				(IAttachmentTypeDAO) attachmentTypeDAO
						.setSession(currentSession),
				(IObjectWithLongIdDAO) dao.setSession(currentSession),
				newPPodVersionInfo);
		this.study2StudyInfo = study2StudyInfo;
		this.setDocIdVisitor = setDocIdVisitor;
		this.afterUnmarshalVisitorProvider = afterUnmarshalVisitorProvider;
		this.stringPairFactory = stringPairFactory;
	}

	public StudyInfo create(final Study incomingStudy) {
		incomingStudy.accept(afterUnmarshalVisitorProvider.get());
		final Study dbStudy = saveOrUpdateStudy.save(incomingStudy);
		dbStudy.accept(setPPodVersionInfoVisitor);
		return study2StudyInfo.toStudyInfo(dbStudy);
	}

	public Study getStudyByPPodId(final String pPodId) {
		final Study study = studyDAO.getStudyByPPodId(pPodId);
		study.accept(setDocIdVisitor);
		return study;
	}

	public Set<StringPair> getStudyPPodIdLabelPairs() {
		return newHashSet(transform(studyDAO.getPPodIdLabelPairs(),
				new Function<IPair<String, String>, StringPair>() {
					public StringPair apply(IPair<String, String> from) {
						return stringPairFactory.create(from.getFirst(), from
								.getSecond());
					}
				}));
	}

	public StudyInfo update(final Study incomingStudy, final String pPodId) {
		incomingStudy.accept(afterUnmarshalVisitorProvider.get());
		final Study dbStudy = saveOrUpdateStudy.update(incomingStudy);
		dbStudy.accept(setPPodVersionInfoVisitor);
		return study2StudyInfo.toStudyInfo(dbStudy);
	}
}