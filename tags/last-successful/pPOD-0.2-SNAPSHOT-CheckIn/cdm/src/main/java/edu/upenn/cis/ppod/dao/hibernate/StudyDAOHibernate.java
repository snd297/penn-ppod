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

import edu.upenn.cis.ppod.dao.IStudyDAO;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.util.IPair;

/**
 * An {@link IStudyDAO} Hibernate DAO.
 * 
 * @author Sam Donnelly
 */
public class StudyDAOHibernate extends GenericHibernateDAO<Study, Long>
		implements IStudyDAO {

	private final IPair.IFactory pairFactory;

	@Inject
	StudyDAOHibernate(final IPair.IFactory pairFactory) {
		this.pairFactory = pairFactory;
	}

	public Set<IPair<String, String>> getPPodIdLabelPairs() {
		final Set<IPair<String, String>> results = newHashSet();
		for (final Iterator<?> itr = getSession().getNamedQuery(
				"Study-getPPodIdLabelPairs").iterate(); itr.hasNext();) {
			final Object[] result = (Object[]) itr.next();
			results.add(pairFactory.create((String) result[0],
					(String) result[1]));
		}
		return results;
	}

	public Study getStudyByPPodId(final String pPodId) {
		return (Study) getSession().getNamedQuery("Study-getByPPodId")
				.setParameter("pPodId", pPodId).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getOTUInfosByStudyPPodIdAndMinPPodVersion(
			String studyPPodId, Long minPPodVersion) {
		return (List<Object[]>) getSession().getNamedQuery(
				"Study-getOTUSetInfosByStudyPPodIdAndMinPPodVersion")
				.setParameter("studyPPodId", studyPPodId).setParameter(
						"minPPodVersion", minPPodVersion).list();
	}

	public Long getPPodVersionByPPodId(String pPodId) {
		return (Long) getSession()
				.getNamedQuery("Study-getPPodVersionByPPodId").setParameter(
						"pPodId", pPodId).uniqueResult();
	}
}