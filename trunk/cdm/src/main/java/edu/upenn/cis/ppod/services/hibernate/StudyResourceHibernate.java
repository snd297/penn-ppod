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

import edu.upenn.cis.ppod.createorupdate.ICreateOrUpdateStudy;
import edu.upenn.cis.ppod.dao.IStudyDAO;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.imodel.IStudy;
import edu.upenn.cis.ppod.services.IStudyResource;
import edu.upenn.cis.ppod.services.StringPair;
import edu.upenn.cis.ppod.services.ppodentity.IStudy2StudyInfo;
import edu.upenn.cis.ppod.services.ppodentity.StudyInfo;
import edu.upenn.cis.ppod.util.AfterUnmarshalVisitor;
import edu.upenn.cis.ppod.util.IPair;
import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.SetDocIdVisitor;
import edu.upenn.cis.ppod.util.SetVersionInfoVisitor;

/**
 * We commit the transactions in this class so that the resteasy response will
 * know that something went wrong if the commit goes wrong. We used to do it in
 * a resteasy interceptor, but that didn't work cleanly whe we switched over to
 * a guice managed session factory: we couldn't get at the current session
 * inside the interceptor without putting a kludge static reference to it in
 * {@link edu.upenn.cis.ppod.persistence.SessionFactoryProvider}.
 * 
 * @author Sam Donnelly
 */
public final class StudyResourceHibernate implements IStudyResource {

	private final IStudyDAO studyDAO;

	private final ICreateOrUpdateStudy createOrUpdateStudy;

	private final IStudy2StudyInfo study2StudyInfo;

	private final INewVersionInfo newVersionInfo;

	@Inject
	StudyResourceHibernate(
			final IStudyDAO studyDAO,
			final ICreateOrUpdateStudy createOrUpdateStudy,
			final IStudy2StudyInfo study2StudyInfo,
			final INewVersionInfo newVersionInfo) {
		this.studyDAO = studyDAO;
		this.createOrUpdateStudy = createOrUpdateStudy;
		this.study2StudyInfo = study2StudyInfo;
		this.newVersionInfo = newVersionInfo;
	}

	private StudyInfo createOrUpdateStudy(final IStudy incomingStudy) {
		incomingStudy.accept(new AfterUnmarshalVisitor());

		final IStudy dbStudy = createOrUpdateStudy
				.createOrUpdateStudy(incomingStudy);

		final IVisitor setVersionInfoVisitor = new SetVersionInfoVisitor(
				newVersionInfo);

		dbStudy.accept(setVersionInfoVisitor);

		return study2StudyInfo.toStudyInfo(dbStudy);
	}

	public StudyInfo createStudy(final IStudy incomingStudy) {
		final StudyInfo studyInfo = createOrUpdateStudy(incomingStudy);
		return studyInfo;
	}

	public IStudy getStudyByPPodId(final String pPodId) {
		final IStudy study = studyDAO.getStudyByPPodId(pPodId);
		study.accept(new SetDocIdVisitor());
		return study;
	}

	public Set<StringPair> getStudyPPodIdLabelPairs() {
		final Set<StringPair> studyPPodIdLabelPairs = newHashSet(transform(
						studyDAO.getPPodIdLabelPairs(),
				new Function<IPair<String, String>, StringPair>() {
					public StringPair apply(final IPair<String, String> from) {
						return new StringPair(from.getFirst(), from.getSecond());
					}
				}));
		return studyPPodIdLabelPairs;
	}

	public StudyInfo updateStudy(final IStudy incomingStudy, final String pPodId) {
		final StudyInfo studyInfo = createOrUpdateStudy(incomingStudy);
		return studyInfo;
	}

}