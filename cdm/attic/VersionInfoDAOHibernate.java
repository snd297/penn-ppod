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

import org.hibernate.Session;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.model.VersionInfo;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;

/**
 * A Hibernate <code>IVersionInfoDAO</code>.
 * 
 * @author Sam Donnelly
 */
public final class VersionInfoDAOHibernate
		extends GenericHibernateDAO<VersionInfo, Long>
		implements IVersionInfoDAO {

	@Inject
	VersionInfoDAOHibernate(final Session session) {
		setSession(session);
	}

	public Long getMaxVersion() {
		final Long maxPPodVersion =
				(Long) getSession()
						.getNamedQuery("VersionInfo-getMaxVersionInfo")
						.uniqueResult();
		return maxPPodVersion == null ? Long.valueOf(0L) : maxPPodVersion;
	}
}
