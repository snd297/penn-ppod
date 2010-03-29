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

import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;
import edu.upenn.cis.ppod.util.IPair;

// TODO: Auto-generated Javadoc
/**
 * An {@link IStudyDAO} Hibernate DAO.
 * 
 * @author Sam Donnelly
 */
final class StudyDAOHibernate extends GenericHibernateDAO<Study, Long>
		implements IStudyDAOHibernate {

	/** The pair factory. */
	private final IPair.IFactory pairFactory;

	/**
	 * Instantiates a new study dao hibernate.
	 * 
	 * @param pairFactory the pair factory
	 */
	@Inject
	StudyDAOHibernate(final IPair.IFactory pairFactory) {
		this.pairFactory = pairFactory;
	}

	/**
	 * Gets the p pod id label pairs.
	 * 
	 * @return the p pod id label pairs
	 */
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

	/**
	 * Gets the study by p pod id.
	 * 
	 * @param pPodId the pod id
	 * @return the study by p pod id
	 */
	public Study getStudyByPPodId(final String pPodId) {
		return (Study) getSession().getNamedQuery(
				Study.class.getSimpleName() + "-getByPPodId").setParameter(
				"pPodId", pPodId).uniqueResult();
	}

	/**
	 * Gets the study by p pod id eager.
	 * 
	 * @param pPodId the pod id
	 * @return the study by p pod id eager
	 */
	public Study getStudyByPPodIdEager(final String pPodId) {
		return (Study) getSession().createCriteria(Study.class).add(
				Restrictions.eq("pPodId", pPodId)).setFetchMode("otuSets",
				FetchMode.JOIN).createCriteria("otuSets").setFetchMode("otus",
				FetchMode.JOIN).setFetchMode("matrices", FetchMode.JOIN)
				.setFetchMode("dnaSequenceSets", FetchMode.JOIN)
				.setFetchMode("treeSets", FetchMode.JOIN).createCriteria(
						"matrices").setFetchMode("characters", FetchMode.JOIN)
				.setFetchMode("characterIdx", FetchMode.JOIN).setFetchMode(
						"otusToRows",
						FetchMode.JOIN)
				.createCriteria("otusToRows").setFetchMode("otusToRows",
						FetchMode.JOIN).
				uniqueResult();
		// Extending to cells.firstState causes us to run out of memory
	}

	/**
	 * Gets the oTU infos by study p pod id and min p pod version.
	 * 
	 * @param studyPPodId the study p pod id
	 * @param minPPodVersion the min p pod version
	 * @return the oTU infos by study p pod id and min p pod version
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getOTUInfosByStudyPPodIdAndMinPPodVersion(
			String studyPPodId, Long minPPodVersion) {
		return (List<Object[]>) getSession().getNamedQuery(
				"Study-getOTUSetInfosByStudyPPodIdAndMinPPodVersion")
				.setParameter("studyPPodId", studyPPodId).setParameter(
						"minPPodVersion", minPPodVersion).list();
	}

	/**
	 * Gets the p pod version by p pod id.
	 * 
	 * @param pPodId the pod id
	 * @return the p pod version by p pod id
	 */
	public Long getPPodVersionByPPodId(String pPodId) {
		return (Long) getSession()
				.getNamedQuery("Study-getPPodVersionByPPodId").setParameter(
						"pPodId", pPodId).uniqueResult();
	}
}
