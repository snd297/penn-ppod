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

import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.find;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.dto.IHasPPodId;
import edu.upenn.cis.ppod.dto.PPodEntities;
import edu.upenn.cis.ppod.dto.PPodOtuSet;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.OtuSet;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.util.DbStudy2DocStudy;

/**
 * @author Sam Donnelly
 */
class PPodEntitiesResourceHibernate implements
		IPPodEntitiesResource {

	private final Session session;
	private final DbStudy2DocStudy dbStudy2DocStudy;
	private static final Logger logger = LoggerFactory
			.getLogger(PPodEntitiesResourceHibernate.class);

	@Inject
	PPodEntitiesResourceHibernate(
			final Session session,
			final DbStudy2DocStudy dbStudy2DocStudy) {
		this.session = session;
		this.dbStudy2DocStudy = dbStudy2DocStudy;
	}

	public PPodEntities getEntitiesByHqlQuery(final String query) {
		final long inTime = new Date().getTime();
		Transaction trx = null;
		try {
			trx = session.beginTransaction();

			@SuppressWarnings("unchecked")
			final List<Object> queryResults =
					session.createQuery(query)
							.setReadOnly(true)
							.list();
			final PPodEntities pPodEntities = new PPodEntities();

			for (final Object queryResult : queryResults) {
				if (queryResult instanceof OtuSet) {
					final OtuSet otuSet = (OtuSet) queryResult;

					// Note that otu set may have already been added in any of
					// the
					// other if clauses so we must make check before adding
					if (find(
							pPodEntities.getOtuSets(),
							compose(equalTo(otuSet.getPPodId()),
									IHasPPodId.getPPodId), null) == null) {
						final PPodOtuSet docOtuSet = dbStudy2DocStudy
								.dbOtuSet2DocOtuSetJustOtus(otuSet);

						pPodEntities.getOtuSets().add(docOtuSet);
						docOtuSet.setLabel(docOtuSet.getLabel() + "/"
								+ otuSet.getParent().getLabel());
					}
				} else if (queryResult instanceof StandardMatrix) {
					final StandardMatrix matrix = (StandardMatrix) queryResult;

					// Note that otu set may have already been added in any of
					// the other if clauses so we must make check before adding

					PPodOtuSet docOtuSet;

					if ((docOtuSet = find(
							pPodEntities.getOtuSets(),
							compose(equalTo(matrix.getParent().getPPodId()),
									IHasPPodId.getPPodId), null)) == null) {
						docOtuSet =
								dbStudy2DocStudy
										.dbOtuSet2DocOtuSetJustOtus(
												matrix.getParent());
						pPodEntities.getOtuSets().add(docOtuSet);
						docOtuSet.setLabel(docOtuSet.getLabel() + "/"
								+ matrix.getParent().getParent().getLabel());
					}
					docOtuSet
							.getStandardMatrices()
							.add(dbStudy2DocStudy
									.dbStandardMatrix2DocStandardMatrix(matrix));

				} else if (queryResult instanceof TreeSet) {
					// final TreeSet treeSet = (TreeSet) queryResult;
					throw new IllegalArgumentException(
							"tree set queries not supported");
				} else if (queryResult instanceof Otu) {
					final Otu otu = (Otu) queryResult;
					pPodEntities.getOtus().add(
							dbStudy2DocStudy.dbOtu2DocOtu(otu));
				} else if (queryResult instanceof Object[]) {
					throw new IllegalArgumentException(
							"nested query results not supported. query: ["
									+ query
									+ "]");
				} else {
					throw new IllegalArgumentException(
							"unsupported entity type ["
													+ queryResult.getClass()
													+ "], result: ["
													+ queryResult.toString()
													+ "]");
				}
			}
			trx.commit();
			return pPodEntities;
		} catch (Throwable t) {
			if (trx != null && trx.isActive()) {
				try {
					trx.rollback();
				} catch (Throwable rbEx) {
					logger.error("caught exception while rolling back", rbEx);
				}
			}
			throw new IllegalStateException(t);
		} finally {
			logger.debug("createOrUpdateStudy(...): response time: "
					+ (new Date().getTime() - inTime) + " milliseconds");
		}
	}
}
