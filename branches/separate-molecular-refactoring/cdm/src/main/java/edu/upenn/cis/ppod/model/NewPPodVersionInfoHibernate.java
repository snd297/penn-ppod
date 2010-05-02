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
package edu.upenn.cis.ppod.model;

import java.util.Date;

import org.hibernate.Session;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.hibernate.PPodVersionInfoDAOHibernate;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfoHibernate;

/**
 * @author Sam Donnelly
 */
final class NewPPodVersionInfoHibernate implements
		INewPPodVersionInfoHibernate {

	private final PPodVersionInfo newPPodVersionInfo;
	private final PPodVersionInfoDAOHibernate pPodVersionInfoDAO;

	private boolean pPodVersionInfoInitialized = false;

	@Inject
	NewPPodVersionInfoHibernate(
			final PPodVersionInfoDAOHibernate pPodVersionInfoDAO,
			final PPodVersionInfo newPPodVersionInfo,
			@Assisted final Session session) {
		this.newPPodVersionInfo = newPPodVersionInfo;
		this.pPodVersionInfoDAO = pPodVersionInfoDAO;
		this.pPodVersionInfoDAO.setSession(session);
	}

	public PPodVersionInfo getNewPPodVersionInfo() {
		initializePPodVersionInfo();
		return newPPodVersionInfo;
	}

	private void initializePPodVersionInfo() {
		if (pPodVersionInfoInitialized) {

		} else {
			final Long newPPodVersion = Long.valueOf(pPodVersionInfoDAO
					.getMaxPPodVersion() + 1);
			newPPodVersionInfo.setPPodVersion(newPPodVersion);
			newPPodVersionInfo.setCreated(new Date());
			pPodVersionInfoDAO.saveOrUpdate(newPPodVersionInfo);
			pPodVersionInfoInitialized = true;
		}
	}
}
