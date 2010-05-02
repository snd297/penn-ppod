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
import static com.google.common.collect.Sets.newHashSet;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.hibernate.FlushMode;
import org.hibernate.Session;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.services.ppodentity.PPodEntities;
import edu.upenn.cis.ppod.thirdparty.util.HibernateUtil;
import edu.upenn.cis.ppod.util.ISetDocIdVisitor;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * @author Sam Donnelly
 */
class PPodEntitiesResourceHibernate implements
		IPPodEntitiesResourceHibernate {

	private final Provider<PPodEntities> pPodEntitiesProvider;

	private final IVisitor setDocIdVisitor;

	@CheckForNull
	private Session session;

	@Inject
	PPodEntitiesResourceHibernate(final Provider<PPodEntities> pPodEntities,
			final ISetDocIdVisitor setDocIdVisitor) {
		this.pPodEntitiesProvider = pPodEntities;
		this.setDocIdVisitor = setDocIdVisitor;
	}

	public PPodEntities getEntitiesByHqlQuery(final String query) {
		checkNotNull(query);

		if (session == null) {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
		}

		// These queries are read only so set this for efficiency, security, and
		// so we can modify the entities for the response
		// without the modifications being committed to the database.
		session.setFlushMode(FlushMode.MANUAL);

		@SuppressWarnings("unchecked")
		final List<Object> queryResults = session.createQuery(query).list();
		final PPodEntities pPodEntities = pPodEntitiesProvider.get();

		final Set<CharacterStateMatrix> addedMatrices = newHashSet();
		final Set<TreeSet> addedTreeSets = newHashSet();

// final List<Object> flattenedQueryResults = newArrayList();
// for (final Object queryResult : queryResults) {
// if (queryResult instanceof Object[]) {
// final Object[] objects = (Object[]) queryResult;
// for (final Object object : objects) {
// flattenedQueryResults.add(object);
// }
// } else {
// flattenedQueryResults.add(queryResult);
// }
// }

		for (final Object queryResult : queryResults) {
			if (queryResult instanceof OTUSet) {
				final OTUSet otuSet = (OTUSet) queryResult;

				// Extra insurance against accidental sync with database
				session.setReadOnly(otuSet, true);

				if (otuSet.getDocId() == null) {
					otuSet.accept(setDocIdVisitor);
				}

				// Note that otu set may have already been added in any of the
				// other if clauses: Hibernate identity takes care of us
				pPodEntities.addOTUSet(otuSet);
			} else if (queryResult instanceof CharacterStateMatrix) {

				final CharacterStateMatrix matrix = (CharacterStateMatrix) queryResult;

				// Extra insurance against accidental sync with database
				session.setReadOnly(matrix, true);

				addedMatrices.add(matrix);
				if (matrix.getOTUSet().getDocId() == null) {
					matrix.getOTUSet().accept(setDocIdVisitor);
				}

				// Note that otu set may have already been added in any of the
				// other if clauses: Hibernate identity takes care of us
				pPodEntities.addOTUSet(matrix.getOTUSet());
			} else if (queryResult instanceof TreeSet) {
				final TreeSet treeSet = (TreeSet) queryResult;

				// Extra insurance against accidental sync with database
				session.setReadOnly(treeSet, true);

				addedTreeSets.add(treeSet);
				if (treeSet.getOTUSet().getDocId() == null) {
					treeSet.getOTUSet().accept(setDocIdVisitor);
				}

				// Note that otu set may have already been added in any of the
				// other if clauses: Hibernate identity takes care of us
				pPodEntities.addOTUSet(treeSet.getOTUSet());
			} else if (queryResult instanceof OTU) {
				final OTU otu = (OTU) queryResult;
				session.setReadOnly(otu, true);
				pPodEntities.addOTU(otu);
			} else if (queryResult instanceof Object[]) {
				throw new IllegalArgumentException(
						"nested query results not supported. query: [" + query
								+ "]");
			} else {
				throw new IllegalArgumentException("unsupported entity type ["
													+ queryResult.getClass()
													+ "], result: ["
													+ queryResult.toString()
													+ "]");
			}

			// Now we clean up our response so we don't include any extra
			// matrices or tree sets that were pulled over with the OTUSet's
			for (final Iterator<OTUSet> otuSetsItr = pPodEntities
					.getOTUSetsIterator(); otuSetsItr.hasNext();) {

				final OTUSet otuSet = otuSetsItr.next();

				final Set<CharacterStateMatrix> matricesToReturn = newHashSet();

				for (final Iterator<CharacterStateMatrix> matrixItr = otuSet
						.getCategoricalMatricesIterator(); matrixItr.hasNext();) {
					final CharacterStateMatrix matrix = matrixItr.next();
					if (addedMatrices.contains(matrix)) {
						matricesToReturn.add(matrix);
					}
				}
				otuSet.setCategoricalMatrices(matricesToReturn);

				final Set<TreeSet> treeSetsToReturn = newHashSet();
				for (final Iterator<TreeSet> treeSetItr = otuSet
						.getTreeSetsIterator(); treeSetItr.hasNext();) {
					final TreeSet treeSet = treeSetItr.next();
					if (addedTreeSets.contains(treeSet)) {
						treeSetsToReturn.add(treeSet);
					}
				}
				otuSet.setTreeSets(treeSetsToReturn);
			}
		}
		return pPodEntities;
	}

	public PPodEntitiesResourceHibernate setSession(final Session session) {
		this.session = session;
		return this;
	}
}
