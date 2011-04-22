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
import static com.google.common.collect.Lists.newArrayList;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.dto.Counts;
import edu.upenn.cis.ppod.dto.IHasPPodId;
import edu.upenn.cis.ppod.dto.PPodDnaMatrix;
import edu.upenn.cis.ppod.dto.PPodEntities;
import edu.upenn.cis.ppod.dto.PPodOtuSet;
import edu.upenn.cis.ppod.dto.PPodProteinMatrix;
import edu.upenn.cis.ppod.dto.PPodStandardMatrix;
import edu.upenn.cis.ppod.dto.PPodTreeSet;
import edu.upenn.cis.ppod.model.DnaMatrix;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.OtuSet;
import edu.upenn.cis.ppod.model.ProteinMatrix;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.util.DbStudy2DocStudy;

/**
 * @author Sam Donnelly
 */
class OtuSetsResourceHibernate
		implements IOtuSetsResource {

	private final Session session;
	private static final Logger logger =
			LoggerFactory.getLogger(OtuSetsResourceHibernate.class);

	@Inject
	OtuSetsResourceHibernate(final Session session) {
		this.session = session;
	}

	private Counts count(final String query) {

		final StringBuilder querySb = new StringBuilder("select ");

		querySb.append("count(distinct os)");

		if (query.contains("os.standardMatrices")) {
			querySb.append(", count(distinct sm)");
		} else {
			querySb.append(", 0L");
		}
		if (query.contains("os.dnaMatrices")) {
			querySb.append(", count(distinct dm)");
		} else {
			querySb.append(", 0L");
		}
		if (query.contains("os.proteinMatrices")) {
			querySb.append(", count(distinct pm)");
		} else {
			querySb.append(", 0L");
		}
		if (query.contains("os.treeSets")) {
			querySb.append(", count(distinct ts)");
		} else {
			querySb.append(", 0L");
		}

		querySb.append(" ");

		querySb.append(
					query.substring(query.indexOf("from"),
							query.length()));

		final Object[] result = (Object[])
					session.createQuery(querySb.toString())
							.setReadOnly(true)
							.uniqueResult();

		final Counts counts = new Counts();

		counts.setOtuSetCount((Long) result[0]);
		counts.setStandardMatrixCount((Long) result[1]);
		counts.setDnaMatrixCount((Long) result[2]);
		counts.setProteinMatrixCount((Long) result[3]);
		counts.setTreeSetCount((Long) result[4]);

		return counts;
	}

	public Counts countHqlQuery(final String query, Integer timeoutSeconds) {
		final String METHOD = "countHqlQuery(...)";
		final long inTime = new Date().getTime();

		timeoutSeconds = Math.min(timeoutSeconds, 60);

		Transaction trx = null;
		try {
			// not beginTransaction so we can
			// now set the timeout
			trx = session.getTransaction();
			trx.setTimeout(timeoutSeconds);
			trx.begin();

			final Counts counts = count(query);
			trx.commit();
			return counts;

		} catch (final Throwable t) {
			if (trx != null && trx.isActive()) {
				try {
					trx.rollback();
				} catch (final Throwable rbEx) {
					logger.error("caught exception while rolling back", rbEx);
				}
			}
			long endTime = new Date().getTime();

			if (endTime - inTime >= (timeoutSeconds * 1000) - 500) {
				logger.error(
						"caught timeout exception, returning flagged Counts", t);
				final Counts counts = new Counts();
				counts.setTimedOut(true);
				counts.setOtuSetCount(-1);
				counts.setStandardMatrixCount(-1);
				counts.setDnaMatrixCount(-1);
				counts.setTreeSetCount(-1);
				return counts;
			}
			final String exceptionUuid = UUID.randomUUID().toString();
			logger.error(METHOD + ": " + exceptionUuid, t);
			throw new IllegalStateException(t.getMessage() +
					": error id [" + exceptionUuid + "]",
					t);
		} finally {
			session.close();
			logger.info("{}: response time: {} milliseconds",
					METHOD, Long.valueOf(new Date().getTime() - inTime));
		}
	}

	public PPodEntities getEntitiesByHqlQuery(final String query) {
		final String METHOD = "getEntitiesByHqlQuery(...)";
		final long inTime = new Date().getTime();
		Transaction trx = null;

		final DbStudy2DocStudy dbStudy2DocStudy = new DbStudy2DocStudy();

		final int TIMEOUT_SECONDS = 60;

		boolean countTooHigh = false;
		try {
			// not beginTransaction so we can
			// now set the timeout
			trx = session.getTransaction();
			trx.setTimeout(TIMEOUT_SECONDS);
			trx.begin();

			@SuppressWarnings("unchecked")
			final List<Object> queryResults =
					session.createQuery(query)
							.setReadOnly(true).list();
			final PPodEntities entities = new PPodEntities();

			final List<Object> flattenedAndDenulledResults = newArrayList();

			for (final Object queryResult : queryResults) {
				if (queryResult instanceof Object[]) {
					final Object[] queryResultObjectArray = (Object[]) queryResult;
					for (final Object o : queryResultObjectArray) {
						if (o != null) {
							flattenedAndDenulledResults.add(o);
						}
					}
				} else {
					flattenedAndDenulledResults.add(queryResult);
				}
			}

			for (final Object queryResult : flattenedAndDenulledResults) {
				if (entities.countMembers() > 15) {
					countTooHigh = true;
					break;
				}

				if (queryResult instanceof OtuSet) {
					final OtuSet otuSet = (OtuSet) queryResult;
					handleOtuSet(entities, otuSet, dbStudy2DocStudy);
				} else if (queryResult instanceof StandardMatrix) {
					final StandardMatrix dbMatrix = (StandardMatrix) queryResult;

					// Note that otu set may have already been added in any
					// of
					// the other if clauses so we must make check before
					// adding

					final OtuSet dbOtuSet = dbMatrix.getParent();

					final PPodOtuSet docOtuSet = handleOtuSet(
								entities,
								dbOtuSet,
								dbStudy2DocStudy);

					// Let's not add in the same matrix twice
					if (find(docOtuSet.getStandardMatrices(),
								compose(equalTo(dbMatrix.getPPodId()),
										IHasPPodId.getPPodId), null) == null) {
						final PPodStandardMatrix docMatrix = dbStudy2DocStudy
									.dbStandardMatrix2DocStandardMatrix(dbMatrix);
						docMatrix.setLabel(dbOtuSet.getParent()
									.getLabel()
									+ "/" + docMatrix.getLabel());
						docOtuSet
									.getStandardMatrices()
									.add(docMatrix);
					}

				} else if (queryResult instanceof DnaMatrix) {
					final DnaMatrix dbMatrix = (DnaMatrix) queryResult;

					// Note that otu set may have already been added in any
					// of
					// the other if clauses so we must make check before
					// adding

					final OtuSet dbOtuSet = dbMatrix.getParent();

					final PPodOtuSet docOtuSet = handleOtuSet(
								entities,
								dbOtuSet,
								dbStudy2DocStudy);

					// Let's not add in the same matrix twice
					if (find(docOtuSet.getDnaMatrices(),
								compose(equalTo(dbMatrix.getPPodId()),
										IHasPPodId.getPPodId), null) == null) {
						final PPodDnaMatrix docMatrix = dbStudy2DocStudy
									.dbDnaMatrix2DocDnaMatrix(dbMatrix);
						docMatrix.setLabel(dbOtuSet.getParent()
									.getLabel()
									+ "/" + docMatrix.getLabel());
						docOtuSet
									.getDnaMatrices()
									.add(docMatrix);
					}
				} else if (queryResult instanceof ProteinMatrix) {
					final ProteinMatrix dbMatrix = (ProteinMatrix) queryResult;

					// Note that otu set may have already been added in any
					// of
					// the other if clauses so we must make check before
					// adding

					final OtuSet dbOtuSet = dbMatrix.getParent();

					final PPodOtuSet docOtuSet = handleOtuSet(
								entities,
								dbOtuSet,
								dbStudy2DocStudy);

					// Let's not add in the same matrix twice
					if (find(docOtuSet.getProteinMatrices(),
								compose(equalTo(dbMatrix.getPPodId()),
										IHasPPodId.getPPodId), null) == null) {
						final PPodProteinMatrix docMatrix = dbStudy2DocStudy
									.dbProteinMatrix2DocProteinMatrix(dbMatrix);
						docMatrix.setLabel(dbOtuSet.getParent()
									.getLabel()
									+ "/" + docMatrix.getLabel());
						docOtuSet
									.getProteinMatrices()
									.add(docMatrix);
					}
				} else if (queryResult instanceof TreeSet) {
					final TreeSet dbTreeSet = (TreeSet) queryResult;

					// Note that otu set may have already been added in any
					// of
					// the other if clauses so we must make check before
					// adding

					final OtuSet dbOtuSet = dbTreeSet.getParent();

					final PPodOtuSet docOtuSet = handleOtuSet(
								entities,
								dbOtuSet,
								dbStudy2DocStudy);

					// Let's not add in the same tree set twice
					if (find(docOtuSet.getTreeSets(),
								compose(equalTo(dbTreeSet.getPPodId()),
										IHasPPodId.getPPodId), null) == null) {
						final PPodTreeSet docTreeSet = dbStudy2DocStudy
									.dbTreeSet2DocTreeSet(dbTreeSet);
						docTreeSet.setLabel(dbOtuSet.getParent()
									.getLabel()
									+ "/" + docTreeSet.getLabel());
						docOtuSet
									.getTreeSets()
									.add(docTreeSet);
					}
				} else if (queryResult instanceof Otu) {
					final Otu otu = (Otu) queryResult;
					entities.getOtus().add(
								dbStudy2DocStudy.dbOtu2DocOtu(otu));
				} else if (queryResult instanceof Object[]) {
					throw new IllegalArgumentException(
								"multiple results in one row not supported. query: ["
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
			return entities;
		} catch (final Throwable t) {
			if (trx != null && trx.isActive()) {
				try {
					trx.rollback();
				} catch (final Throwable rbEx) {
					logger.error("caught exception while rolling back", rbEx);
				}
			}
			logger.error(METHOD, t);
			long endTime = new Date().getTime();

			if (endTime - inTime >= (TIMEOUT_SECONDS * 1000) - 500) {
				logger.error(
						"caught timeout exception, swalling exception and throwing one that indicates a timeout");
				throw new IllegalStateException("query was taking longer than "
						+ TIMEOUT_SECONDS + " seconds");
			}
			throw new IllegalStateException(t);
		} finally {
			session.close();
			logger.info("{}: response time: {} milliseconds",
					METHOD, Long.valueOf(new Date().getTime() - inTime));
			if (countTooHigh) {
				throw new IllegalArgumentException(
						"more than 15 results not supported");
			}
		}
	}

	private PPodOtuSet handleOtuSet(
			final PPodEntities pPodEntities,
			final OtuSet otuSet,
			final DbStudy2DocStudy dbStudy2DocStudy) {
		// Note that otu set may have already been added in any of
		// the
		// other if clauses so we must make check before adding

		PPodOtuSet docOtuSet;

		if ((docOtuSet = find(
				pPodEntities.getOtuSets(),
				compose(equalTo(otuSet.getPPodId()),
						IHasPPodId.getPPodId), null)) == null) {
			docOtuSet = dbStudy2DocStudy
					.dbOtuSet2DocOtuSetJustOtus(otuSet);

			pPodEntities.getOtuSets().add(docOtuSet);
			docOtuSet.setLabel(
					otuSet.getParent().getLabel()
							+ "/"
							+ docOtuSet.getLabel());
		}
		return docOtuSet;
	}
}
