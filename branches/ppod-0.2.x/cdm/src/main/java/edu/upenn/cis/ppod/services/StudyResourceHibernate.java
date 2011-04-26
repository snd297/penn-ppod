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
package edu.upenn.cis.ppod.services;

import java.util.Date;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.createorupdate.ICreateOrUpdateStudy;
import edu.upenn.cis.ppod.dao.IStudyDAO;
import edu.upenn.cis.ppod.dto.PPodLabelAndId;
import edu.upenn.cis.ppod.dto.PPodStudy;
import edu.upenn.cis.ppod.dto.StudyInfo;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.util.DbStudy2DocStudy;
import edu.upenn.cis.ppod.util.Study2StudyInfo;

/**
 * We commit the transactions in this class - instead of a servlet filter - so
 * that the resteasy response will know that something went wrong if the commit
 * goes wrong. We used to do it in a resteasy interceptor, but that didn't work
 * cleanly whe we switched over to a guice managed session factory: we couldn't
 * get at the current session inside the interceptor without putting a kludge
 * static reference to it in
 * {@link edu.upenn.cis.ppod.persistence.SessionFactoryProvider}.
 * 
 * @author Sam Donnelly
 */
final class StudyResourceHibernate implements IStudyResource {

	private final IStudyDAO studyDAO;

	private final ICreateOrUpdateStudy createOrUpdateStudy;

	private final Session session;

	private final DbStudy2DocStudy dbStudy2DocStudy;

	private static final Logger logger = LoggerFactory
			.getLogger(StudyResourceHibernate.class);

	@Inject
	StudyResourceHibernate(
			final IStudyDAO studyDAO,
			final ICreateOrUpdateStudy createOrUpdateStudy,
			final Session session) {
		this.studyDAO = studyDAO;
		this.createOrUpdateStudy = createOrUpdateStudy;
		this.session = session;
		dbStudy2DocStudy = new DbStudy2DocStudy();
	}

	private StudyInfo createOrUpdateStudy(final PPodStudy incomingStudy) {
		final String METHOD = "createOrUpdateStudy(...)";
		final long inTime = new Date().getTime();

		Transaction trx = null;

		try {

			trx = session.beginTransaction();

			final Study dbStudy =
					createOrUpdateStudy.createOrUpdateStudy(incomingStudy);

			final StudyInfo studyInfo = Study2StudyInfo.toStudyInfo(dbStudy);

			trx.commit();

			return studyInfo;

		} catch (final Throwable t) {
			try {
				if (trx != null && trx.isActive()) {
					trx.rollback();
				}
			} catch (final Throwable rbEx) {
				logger.error("error rolling back transaction", rbEx);
			}
			logger.error("caught", t);
			if (t instanceof RuntimeException) {
				throw (RuntimeException) t;
			}
			throw new IllegalStateException(t);
		} finally {
			session.close();
			logger.info("{}: response time: {} milliseconds",
					METHOD,
					Long.valueOf(new Date().getTime() - inTime));
		}
	}

	public StudyInfo createStudy(final PPodStudy incomingStudy) {
		final StudyInfo studyInfo = createOrUpdateStudy(incomingStudy);
		return studyInfo;
	}

	public PPodStudy getStudyByPPodId(final String pPodId) {
		final String METHOD = "getStudyByPPodId(...)";
		final long inTime = new Date().getTime();
		Transaction trx = null;
		try {

			trx = session.beginTransaction();

			final Study dbStudy = studyDAO.getStudyByPPodId(pPodId);
			final PPodStudy docStudy = dbStudy2DocStudy
					.dbStudy2DocStudy(dbStudy);

			trx.commit();

			return docStudy;
		} catch (final Throwable t) {
			try {
				if (trx != null && trx.isActive()) {
					trx.rollback();
				}
			} catch (final Throwable rbEx) {
				logger.error("error rolling back transaction", rbEx);
			}
			logger.error("caught", t);
			throw new IllegalStateException(t);
		} finally {
			session.close();
			logger.info("{}: response time: {} milliseconds",
					METHOD,
					Long.valueOf(new Date().getTime() - inTime));
		}
	}

	public Set<PPodLabelAndId> getStudyPPodIdLabelPairs() {
		final String METHOD = "getStudyPPodIdLabelPairs()";
		final long inTime = new Date().getTime();
		Transaction trx = null;
		try {
			trx = session.beginTransaction();

			final Set<PPodLabelAndId> studyLabelAndIds = studyDAO
					.getPPodIdLabelPairs();
			trx.commit();
			return studyLabelAndIds;
		} catch (final Throwable t) {
			try {
				if (trx != null && trx.isActive()) {
					trx.rollback();
				}
			} catch (final Throwable rbEx) {
				logger.error("error rolling back transaction", rbEx);
			}
			logger.error("caught", t);
			throw new IllegalStateException(t);
		} finally {
			session.close();
			logger.info("{}: response time: {} milliseconds",
					METHOD,
					Long.valueOf(new Date().getTime() - inTime));
		}
	}

	public StudyInfo updateStudy(
			final PPodStudy incomingStudy,
			final String pPodId) {
		final StudyInfo studyInfo = createOrUpdateStudy(incomingStudy);
		return studyInfo;
	}

}