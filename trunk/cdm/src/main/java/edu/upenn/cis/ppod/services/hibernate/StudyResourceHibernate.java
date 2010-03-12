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

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.dao.IStudyDAO;
import edu.upenn.cis.ppod.dao.hibernate.StudyDAOHibernate;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateStudies;
import edu.upenn.cis.ppod.saveorupdate.hibernate.ISaveOrUpdateStudyHibernateFactory;
import edu.upenn.cis.ppod.services.IStudyResource;
import edu.upenn.cis.ppod.services.PairStringString;
import edu.upenn.cis.ppod.services.ppodentity.IStudy2StudyInfo;
import edu.upenn.cis.ppod.services.ppodentity.StudyInfo;
import edu.upenn.cis.ppod.thirdparty.util.HibernateUtil;
import edu.upenn.cis.ppod.util.AfterUnmarshalVisitor;
import edu.upenn.cis.ppod.util.SetDocIdVisitor;
import edu.upenn.cis.ppod.util.SetPPodVersionInfoVisitor;

/**
 * @author Sam Donnelly
 */
public final class StudyResourceHibernate implements IStudyResource {

	private final IStudyDAO studyDAO;

	private final ISaveOrUpdateStudies saveOrUpdateStudies;

	private final IStudy2StudyInfo study2StudyInfo;

	private final SetDocIdVisitor otuSetAndOTUSetDocIdVisitor;

	private final Provider<AfterUnmarshalVisitor> afterUnmarshalVisitorProvider;

	private final SetPPodVersionInfoVisitor setPPodVersionInfoVisitor;

	@Inject
	StudyResourceHibernate(
			final StudyDAOHibernate studyDAO,
			final ISaveOrUpdateStudyHibernateFactory saveOrUpdateStudyFactory,
			final IStudy2StudyInfo study2StudyInfo,
			final SetDocIdVisitor otuSetAndOTUSetDocIdVisitor,
			final Provider<AfterUnmarshalVisitor> afterUnmarshalVisitorProvider,
			final INewPPodVersionInfo newPPodVersionInfo,
			final SetPPodVersionInfoVisitor.IFactory setPPodVersionInfoVisitorFactory) {
		this.studyDAO = (IStudyDAO) studyDAO.setSession(HibernateUtil
				.getSessionFactory().getCurrentSession());
		this.setPPodVersionInfoVisitor = setPPodVersionInfoVisitorFactory
				.create(newPPodVersionInfo);
		this.saveOrUpdateStudies = saveOrUpdateStudyFactory.create(
				HibernateUtil
						.getSessionFactory().getCurrentSession(),
				newPPodVersionInfo);
		this.study2StudyInfo = study2StudyInfo;
		this.otuSetAndOTUSetDocIdVisitor = otuSetAndOTUSetDocIdVisitor;
		this.afterUnmarshalVisitorProvider = afterUnmarshalVisitorProvider;

	}

	public StudyInfo create(final Study incomingStudy) {
		incomingStudy.accept(afterUnmarshalVisitorProvider.get());
		final Study dbStudy = saveOrUpdateStudies.save(incomingStudy);
		dbStudy.accept(setPPodVersionInfoVisitor);
		return study2StudyInfo.go(dbStudy);
	}

	public Study getStudyByPPodId(final String pPodId) {
		final Study study = studyDAO.getStudyByPPodId(pPodId);
		study.accept(otuSetAndOTUSetDocIdVisitor);
		return study;
	}

	public Set<PairStringString> getStudyPPodIdLabelPairs() {
		return newHashSet(transform(studyDAO.getPPodIdLabelPairs(),
				PairStringString.of));
	}

	public StudyInfo update(final Study incomingStudy, final String pPodId) {
		incomingStudy.accept(afterUnmarshalVisitorProvider.get());
		final Study dbStudy = saveOrUpdateStudies.update(incomingStudy);
		dbStudy.accept(setPPodVersionInfoVisitor);
		return study2StudyInfo.go(dbStudy);
	}
}