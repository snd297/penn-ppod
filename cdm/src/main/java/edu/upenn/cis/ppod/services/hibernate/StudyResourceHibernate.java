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

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.createorupdate.ICreateOrUpdateStudy;
import edu.upenn.cis.ppod.dao.IStudyDAO;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.imodel.IStudy;
import edu.upenn.cis.ppod.services.IStudyResource;
import edu.upenn.cis.ppod.services.StringPair;
import edu.upenn.cis.ppod.services.ppodentity.IStudy2StudyInfo;
import edu.upenn.cis.ppod.services.ppodentity.StudyInfo;
import edu.upenn.cis.ppod.util.IAfterUnmarshalVisitor;
import edu.upenn.cis.ppod.util.IPair;
import edu.upenn.cis.ppod.util.ISetDocIdVisitor;
import edu.upenn.cis.ppod.util.ISetVersionInfoVisitor;

/**
 * @author Sam Donnelly
 */
public final class StudyResourceHibernate implements IStudyResource {

	private final IStudyDAO studyDAO;

	private final ICreateOrUpdateStudy.IFactory createOrUpdateStudyFactory;

	private final IStudy2StudyInfo study2StudyInfo;

	private final ISetDocIdVisitor setDocIdVisitor;

	private final INewVersionInfo newVersionInfo;

	private final Provider<IAfterUnmarshalVisitor> afterUnmarshalVisitorProvider;

	private final StringPair.IFactory stringPairFactory;

	private final ISetVersionInfoVisitor.IFactory setPPodVersionInfoVisitorFactory;

	@Inject
	StudyResourceHibernate(
			final IStudyDAO studyDAO,
			final ICreateOrUpdateStudy.IFactory createOrUpdateStudyFactory,
			final IStudy2StudyInfo study2StudyInfo,
			final ISetDocIdVisitor setDocIdVisitor,
			final Provider<IAfterUnmarshalVisitor> afterUnmarshalVisitorProvider,
			final INewVersionInfo newVersionInfo,
			final ISetVersionInfoVisitor.IFactory setVersionInfoVisitorFactory,
			final StringPair.IFactory stringPairFactory) {

		this.studyDAO = studyDAO;

		this.createOrUpdateStudyFactory = createOrUpdateStudyFactory;

		this.study2StudyInfo = study2StudyInfo;
		this.setDocIdVisitor = setDocIdVisitor;
		this.afterUnmarshalVisitorProvider = afterUnmarshalVisitorProvider;
		this.stringPairFactory = stringPairFactory;

		this.newVersionInfo = newVersionInfo;

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
				createOrUpdateStudyFactory.create(
						incomingStudy);
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