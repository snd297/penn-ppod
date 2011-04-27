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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dao.IVersionInfoDAO;

@Test(groups = TestGroupDefs.FAST)
public class NewVersionInfoDBTest {

	@Test
	public void initializeVersionInfo() {

		final IVersionInfoDAO dao = mock(IVersionInfoDAO.class);

		final NewVersionInfoDB newVersionInfo =
				new NewVersionInfoDB(dao);

		final VersionInfo versionInfo = newVersionInfo.getNewVersionInfo();

		verify(dao).makePersistent(versionInfo);

		assertNotNull(versionInfo.getCreated());
		assertNull(versionInfo.getVersion());

		newVersionInfo.getNewVersionInfo();

		// Make sure that newVersionInfo only calls initialization code once.
		verify(dao).makePersistent(versionInfo);

	}

}
