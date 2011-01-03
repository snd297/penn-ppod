/*
 * Copyright (C) 2010 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.dao.hibernate;

import java.util.List;

import edu.upenn.cis.ppod.dao.IOTUSetDAO;
import edu.upenn.cis.ppod.imodel.IOtuSet;
import edu.upenn.cis.ppod.model.OtuSetChangeSet;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;

/**
 * An {@link OTUSet} Hibernate DAO.
 */
public class OTUSetDAOHibernate
		extends GenericHibernateDAO<IOtuSet, Long>
		implements IOTUSetDAO {

	public IOtuSet getOTUSetByPPodId(final String pPodId) {
		return (IOtuSet) getSession()
				.getNamedQuery(OtuSetChangeSet.class.getSimpleName() + "-getByPPodId")
				.setParameter("pPodId", pPodId)
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getOTUIdsVersionsByOTUSetIdAndMinPPodVersion(
			final Long otuId, final Long minPPodVersion) {
		return (List<Object[]>) getSession()
				.getNamedQuery(
						OtuSetChangeSet.class.getSimpleName()
								+ "-getOTUPPodIdsVersionsByOTUSetIdAndMinPPodVersion")
				.setParameter("otuId", otuId)
				.setParameter("minPPodVersion", minPPodVersion)
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getMatrixInfosByOTUSetPPodIdAndMinPPodVersion(
			final String otuSetPPodId, final Long minPPodVersion) {
		return (List<Object[]>) getSession().getNamedQuery(
				OtuSetChangeSet.class.getSimpleName()
						+ "-getMatrixInfosByOTUSetPPodIdAndMinPPodVersion")
				.setParameter("otuSetPPodId", otuSetPPodId)
				.setParameter("minPPodVersion", minPPodVersion)
				.list();
	}

}