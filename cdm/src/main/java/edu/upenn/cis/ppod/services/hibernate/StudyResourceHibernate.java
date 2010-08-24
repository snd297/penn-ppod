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

import edu.upenn.cis.ppod.createorupdate.ICreateOrUpdateStudy;
import edu.upenn.cis.ppod.dao.IAttachmentNamespaceDAO;
import edu.upenn.cis.ppod.dao.IAttachmentTypeDAO;
import edu.upenn.cis.ppod.dao.IOTUSetDAO;
import edu.upenn.cis.ppod.dao.IObjectWithLongIdDAO;
import edu.upenn.cis.ppod.dao.IStudyDAO;
import edu.upenn.cis.ppod.dao.hibernate.IAttachmentNamespaceDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.IAttachmentTypeDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.IOTUSetDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.IObjectWithLongIdDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.IStudyDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.IVersionInfoDAOHibernate;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.imodel.INewVersionInfoDB;
import edu.upenn.cis.ppod.imodel.IStudy;
import edu.upenn.cis.ppod.services.IStudyResource;
import edu.upenn.cis.ppod.services.StringPair;
import edu.upenn.cis.ppod.services.ppodentity.IStudy2StudyInfo;
import edu.upenn.cis.ppod.services.ppodentity.StudyInfo;
import edu.upenn.cis.ppod.thirdparty.util.HibernateUtil;
import edu.upenn.cis.ppod.util.IAfterUnmarshalVisitor;
import edu.upenn.cis.ppod.util.IPair;
import edu.upenn.cis.ppod.util.ISetVersionInfoVisitor;
import edu.upenn.cis.ppod.util.ISetDocIdVisitor;

/**
 * @author Sam Donnelly
 */
final class StudyResourceHibernate implements IStudyResource {

	private final IStudyDAO studyDAO;

	private final IOTUSetDAO otuSetDAO;

	private final IAttachmentNamespaceDAO attachmentNamespaceDAO;

	private final IAttachmentTypeDAO attachmentTypeDAO;

	private final ICreateOrUpdateStudy.IFactory saveOrUpdateStudyFactory;

	private final IObjectWithLongIdDAO dao;

	private final IStudy2StudyInfo study2StudyInfo;

	private final ISetDocIdVisitor setDocIdVisitor;

	private final INewVersionInfo newVersionInfo;

	private final Provider<IAfterUnmarshalVisitor> afterUnmarshalVisitorProvider;

	private final StringPair.IFactory stringPairFactory;

	private final ISetVersionInfoVisitor.IFactory setPPodVersionInfoVisitorFactory;

	@Inject
	StudyResourceHibernate(
			final IStudyDAOHibernate studyDAO,
			final IOTUSetDAOHibernate otuSetDAO,
			final ICreateOrUpdateStudy.IFactory createOrUpdateStudyFactory,
			final IStudy2StudyInfo study2StudyInfo,
			final IVersionInfoDAOHibernate versionInfoDAO,
			final ISetDocIdVisitor setDocIdVisitor,
			final Provider<IAfterUnmarshalVisitor> afterUnmarshalVisitorProvider,
			final INewVersionInfoDB.IFactory newVersionInfoFactory,
			final ISetVersionInfoVisitor.IFactory setVersionInfoVisitorFactory,
			final StringPair.IFactory stringPairFactory,
			final IAttachmentNamespaceDAOHibernate attachmentNamespaceDAO,
			final IAttachmentTypeDAOHibernate attachmentTypeDAO,
			final IObjectWithLongIdDAOHibernate dao) {
		final Session currentSession =
				HibernateUtil.getSessionFactory().getCurrentSession();
		studyDAO.setSession(currentSession);
		this.studyDAO = studyDAO;

		versionInfoDAO.setSession(currentSession);

		otuSetDAO.setSession(currentSession);
		this.otuSetDAO = otuSetDAO;

		attachmentNamespaceDAO.setSession(currentSession);
		this.attachmentNamespaceDAO = attachmentNamespaceDAO;

		this.attachmentTypeDAO = attachmentTypeDAO;
		attachmentTypeDAO.setSession(currentSession);

		dao.setSession(currentSession);
		this.dao = dao;

		this.saveOrUpdateStudyFactory = createOrUpdateStudyFactory;

		this.study2StudyInfo = study2StudyInfo;
		this.setDocIdVisitor = setDocIdVisitor;
		this.afterUnmarshalVisitorProvider = afterUnmarshalVisitorProvider;
		this.stringPairFactory = stringPairFactory;

		this.newVersionInfo =
				newVersionInfoFactory.create(versionInfoDAO);

		this.setPPodVersionInfoVisitorFactory = setVersionInfoVisitorFactory;
	}

	public StudyInfo createStudy(final IStudy incomingStudy) {
		return createOrUpdateStudy(incomingStudy);
	}

	public IStudy getStudyByPPodId(final String pPodId) {
		final IStudy study = studyDAO.getStudyByPPodId(pPodId);
		study.accept(setDocIdVisitor);
		return study;
	}

	public Set<StringPair> getStudyPPodIdLabelPairs() {
		return newHashSet(transform(
						studyDAO.getPPodIdLabelPairs(),
				new Function<IPair<String, String>, StringPair>() {
					public StringPair apply(final IPair<String, String> from) {
						return stringPairFactory.create(from.getFirst(), from
								.getSecond());
					}
				}));
	}

	private StudyInfo createOrUpdateStudy(final IStudy incomingStudy) {
		incomingStudy.accept(afterUnmarshalVisitorProvider.get());

		final ICreateOrUpdateStudy createOrUpdateStudy =
				saveOrUpdateStudyFactory.create(
						incomingStudy,
						studyDAO,
						otuSetDAO,
						attachmentNamespaceDAO,
						attachmentTypeDAO,
						dao,
						newVersionInfo);
		createOrUpdateStudy.createOrUpdateStudy();
		final IStudy dbStudy = createOrUpdateStudy.getDbStudy();
		final ISetVersionInfoVisitor setVersionInfoVisitor =
				setPPodVersionInfoVisitorFactory.create(newVersionInfo);
		dbStudy.accept(setVersionInfoVisitor);

		return study2StudyInfo.toStudyInfo(dbStudy);
	}

	public StudyInfo updateStudy(final IStudy incomingStudy, final String pPodId) {
		return createOrUpdateStudy(incomingStudy);
	}

}