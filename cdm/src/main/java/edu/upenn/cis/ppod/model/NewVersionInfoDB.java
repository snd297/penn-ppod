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

import com.google.inject.Inject;

import edu.upenn.cis.ppod.dao.IVersionInfoDAO;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;

/**
 * We put this class in here so it can create {@link VersionInfo}
 * {@link VersionInfo#setVersion(Long)} and {@link VersionInfo#setCreated(Date)}
 * .
 * 
 * @author Sam Donnelly
 */
public final class NewVersionInfoDB implements
		INewVersionInfo {

	private final VersionInfo newVersionInfo = new VersionInfo();
	private final IVersionInfoDAO versionInfoDAO;

	private boolean versionInfoInitialized = false;

	@Inject
	NewVersionInfoDB(
			final IVersionInfoDAO versionInfoDAO) {
		this.versionInfoDAO = versionInfoDAO;
	}

	public VersionInfo getNewVersionInfo() {
		initializeVersionInfo();
		return newVersionInfo;
	}

	private void initializeVersionInfo() {
		if (versionInfoInitialized) {

		} else {
			final Long newVersion =
					Long.valueOf(versionInfoDAO.getMaxVersion() + 1);
			newVersionInfo.setVersion(newVersion);
			newVersionInfo.setCreated(new Date());
			versionInfoDAO.makePersistent(newVersionInfo);
			versionInfoInitialized = true;
		}
	}
}
