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
package edu.upenn.cis.ppod.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import com.google.inject.Inject;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.dto.PPodLabelAndId;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;

/**
 * An {@link IStudyDAO} Hibernate DAO.
 * 
 * @author Sam Donnelly
 */
final class StudyDAOHibernate
		extends GenericHibernateDAO<Study, Long>
		implements IStudyDAO {

	@Inject
	StudyDAOHibernate(final Session session) {
		setSession(session);
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getOTUInfosByStudyPPodIdAndMinPPodVersion(
			final String studyPPodId, final Long minPPodVersion) {
		return getSession()
				.getNamedQuery(
						"Study-getOTUSetInfosByStudyPPodIdAndMinPPodVersion")
				.setParameter("studyPPodId", studyPPodId).setParameter(
						"minPPodVersion", minPPodVersion).list();
	}

	public Set<PPodLabelAndId> getPPodIdLabelPairs() {
		final Set<PPodLabelAndId> results = newHashSet();
		for (final Iterator<?> itr = getSession().getNamedQuery(
				Study.class.getSimpleName()
						+ "-getPPodIdLabelPairs").iterate(); itr.hasNext();) {
			final Object[] result = (Object[]) itr.next();
			results.add(new PPodLabelAndId((String) result[0],
					(String) result[1]));
		}
		return results;
	}

	public Long getPPodVersionByPPodId(final String pPodId) {
		return (Long) getSession()
				.getNamedQuery("Study-getPPodVersionByPPodId")
				// .setReadOnly(true)
				.setParameter("pPodId", pPodId)
				.uniqueResult();
	}

	@Nullable
	public Study getStudyByPPodId(@CheckForNull final String pPodId) {
		return (Study) getSession()
				.getNamedQuery(
						Study.class.getSimpleName() + "-getByPPodId")
				.setParameter("pPodId", pPodId)
				.uniqueResult();
	}

	@Nullable
	public Study getStudyByLabel(final String label) {
		checkNotNull(label);
		return (Study) getSession()
				.getNamedQuery(
						Study.class.getSimpleName() + "-getByLabel")
				.setParameter("label", label)
				.uniqueResult();
	}

	public Study getStudyByPPodIdEager(final String pPodId) {

		throw new UnsupportedOperationException();
		// return (Study) getSession()
		// .createCriteria(Study.class)
		// .add(Restrictions.eq("pPodId", pPodId))
		// .setFetchMode("otuSets", FetchMode.JOIN)
		// .createCriteria("otuSets")
		// .setFetchMode("otus", FetchMode.JOIN)
		// .setFetchMode("matrices", FetchMode.JOIN)
		// .setFetchMode("dnaSequenceSets", FetchMode.JOIN)
		// .setFetchMode("treeSets", FetchMode.JOIN)
		// .createCriteria("matrices")
		// .setFetchMode("characters", FetchMode.JOIN)
		// .setFetchMode("characterIdx", FetchMode.JOIN)
		// .setFetchMode("otusToRows", FetchMode.JOIN)
		// .createCriteria("otusToRows")
		// .setFetchMode("otusToRows", FetchMode.JOIN)
		// .uniqueResult();
	}
}
