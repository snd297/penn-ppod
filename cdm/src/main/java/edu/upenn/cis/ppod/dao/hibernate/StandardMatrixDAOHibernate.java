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
package edu.upenn.cis.ppod.dao.hibernate;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.dao.IStandardMatrixDAO;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;
import edu.upenn.cis.ppod.util.IPair;

/**
 * A default {@link StandardMatrix} Hibernate DAO.
 * 
 * @author Sam Donnelly
 */
final class StandardMatrixDAOHibernate
		extends GenericHibernateDAO<StandardMatrix, Long>
		implements IStandardMatrixDAO {

	private final IPair.IFactory pairFactory;

	@Inject
	protected StandardMatrixDAOHibernate(final IPair.IFactory orderedPairFactory) {
		this.pairFactory = orderedPairFactory;
	}

	public IStandardMatrix getByPPodId(final String pPodId) {
		return (IStandardMatrix) getSession().getNamedQuery(
				StandardMatrix.class.getSimpleName() + "-getByPPodId")
				.setParameter("pPodId", pPodId).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getCharacterInfosByMatrixIdAndMinPPodVersion(
			final Long matrixId, final Long minPPodVersion) {
		return getSession().getNamedQuery(
				StandardMatrix.class.getSimpleName()
						+ "-getCharacterInfosByMatrixIdandMinPPodVersion")
				.setParameter("matrixId", matrixId).setParameter(
						"minPPodVersion", minPPodVersion).list();
	}

	@SuppressWarnings("unchecked")
	public List<Long> getColumnPPodVersionsByMatrixId(final Long matrixId) {
		return getSession().getNamedQuery(
				StandardMatrix.class.getSimpleName()
						+ "-getColumnPPodVersionsByMatrixId").setParameter(
				"matrixId", matrixId).list();
	}

	public Set<IPair<String, String>> getPPodIdLabelPairs() {
		final Set<IPair<String, String>> results = newHashSet();
		for (final Iterator<?> itr = getSession().getNamedQuery(
				StandardMatrix.class.getSimpleName()
						+ "-getPPodIdLabelPairs").iterate(); itr.hasNext();) {
			final Object[] result = (Object[]) itr.next();
			results.add(pairFactory.create((String) result[0],
					(String) result[1]));
		}
		return results;
	}

	public Long getPPodVersionById(final Long id) {
		return (Long) getSession().getNamedQuery(
				StandardMatrix.class.getSimpleName()
						+ "-getPPodVersionById").setParameter("id", id)
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getRowIdxsIdsVersionsByMatrixIdAndMinPPodVersion(
			final Long matrixId, final Long minPPodVersion) {
		return getSession().getNamedQuery(
				StandardMatrix.class.getSimpleName()
						+ "-getRowIdxsIdsVersionsByMatrixIdAndMinPPodVersion")
				.setParameter("matrixId", matrixId).setParameter(
						"minPPodVersion", minPPodVersion).list();
	}

}
