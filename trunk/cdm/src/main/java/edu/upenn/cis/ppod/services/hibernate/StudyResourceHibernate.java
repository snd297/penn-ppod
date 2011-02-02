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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Date;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.inject.Inject;

import edu.upenn.cis.ppod.createorupdate.CreateOrUpdateStudy;
import edu.upenn.cis.ppod.dao.ICurrentVersionDAO;
import edu.upenn.cis.ppod.dao.IStudyDAO;
import edu.upenn.cis.ppod.dto.PPodStudy;
import edu.upenn.cis.ppod.dto.Study2StudyInfo;
import edu.upenn.cis.ppod.dto.StudyInfo;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.services.IStudyResource;
import edu.upenn.cis.ppod.services.StringPair;
import edu.upenn.cis.ppod.util.DbStudy2DocStudy;
import edu.upenn.cis.ppod.util.Pair;

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

	private final CreateOrUpdateStudy createOrUpdateStudy;

	private final SessionFactory sessionFactory;

	private DbStudy2DocStudy dbStudy2DocStudy;

	private static final Logger logger = LoggerFactory
			.getLogger(StudyResourceHibernate.class);

	@Inject
	StudyResourceHibernate(
			final IStudyDAO studyDAO,
			final CreateOrUpdateStudy createOrUpdateStudy,
			final INewVersionInfo newVersionInfo,
			final ICurrentVersionDAO currentVersionDAO,
			final SessionFactory sessionFactory,
			final DbStudy2DocStudy dbStudy2DocStudy) {
		this.studyDAO = studyDAO;
		this.createOrUpdateStudy = createOrUpdateStudy;
		this.sessionFactory = sessionFactory;
		this.dbStudy2DocStudy = dbStudy2DocStudy;
	}

	private StudyInfo createOrUpdateStudy(final PPodStudy incomingStudy) {
		checkNotNull(incomingStudy);
		final long inTime = new Date().getTime();
		try {
			final Session session = sessionFactory.getCurrentSession();
			session.beginTransaction();

			final Study dbStudy = createOrUpdateStudy
					.createOrUpdateStudy(incomingStudy);

			final StudyInfo studyInfo = Study2StudyInfo.toStudyInfo(dbStudy);

			session.getTransaction().commit();

			return studyInfo;

		} catch (Throwable t) {
			try {
				sessionFactory.getCurrentSession().getTransaction().rollback();
			} catch (Throwable rbEx) {
				logger.error("error rolling back transaction", rbEx);
			}
			throw new IllegalStateException(t);
		} finally {
			logger.debug("createOrUpdateStudy(...): response time: "
					+ ((new Date().getTime() - inTime) / 1000) + " seconds");
		}
	}

	public StudyInfo createStudy(final PPodStudy incomingStudy) {
		final StudyInfo studyInfo = createOrUpdateStudy(incomingStudy);
		return studyInfo;
	}

	public PPodStudy getStudyByPPodId(final String pPodId) {
		final long inTime = new Date().getTime();
		try {
			final Session session = sessionFactory.getCurrentSession();
			session.beginTransaction();

			final Study dbStudy = studyDAO.getStudyByPPodId(pPodId);
			final PPodStudy docStudy = dbStudy2DocStudy
					.dbStudy2DocStudy(dbStudy);

			session.getTransaction().commit();

			return docStudy;
		} catch (Throwable t) {
			try {
				sessionFactory.getCurrentSession().getTransaction().rollback();
			} catch (Throwable rbEx) {
				logger.error("error rolling back transaction", rbEx);
			}
			throw new IllegalStateException(t);
		} finally {
			logger.debug("getStudyByPPodId(...): response time: "
					+ ((new Date().getTime() - inTime) / 1000) + " seconds");
		}
	}

	public Set<StringPair> getStudyPPodIdLabelPairs() {
		try {
			final Session session = sessionFactory.getCurrentSession();
			session.beginTransaction();

			final Set<Pair<String, String>> studyPPodIdPairs = studyDAO
					.getPPodIdLabelPairs();

			final Set<StringPair> studyPPodIdStringPairs = newHashSet(transform(
					studyPPodIdPairs,

					new Function<Pair<String, String>, StringPair>() {
						public StringPair apply(final Pair<String, String> from) {
							return new StringPair(from.getFirst(), from
									.getSecond());
						}
					}));

			session.getTransaction().commit();
			return studyPPodIdStringPairs;
		} catch (Throwable t) {
			try {
				sessionFactory.getCurrentSession().getTransaction().rollback();
			} catch (Throwable rbEx) {
				logger.error("error rolling back transaction", rbEx);
			}
			throw new IllegalStateException(t);
		}
	}

	public StudyInfo updateStudy(final PPodStudy incomingStudy,
			final String pPodId) {
		final StudyInfo studyInfo = createOrUpdateStudy(incomingStudy);
		return studyInfo;
	}

}