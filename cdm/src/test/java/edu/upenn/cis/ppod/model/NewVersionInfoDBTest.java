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

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dao.ICurrentVersionDAO;
import edu.upenn.cis.ppod.dao.TestVersionInfoDAO;

@Test(groups = TestGroupDefs.FAST)
public class NewVersionInfoDBTest {

	@Test
	public void initializeVersionInfo() {

		final TestVersionInfoDAO dao = new TestVersionInfoDAO();
		final ICurrentVersionDAO currentVersionDAO = mock(ICurrentVersionDAO.class);
		final CurrentVersion currentVersion = new CurrentVersion(42L);
		final Long originalVersion = currentVersion.getVersion();
		when(currentVersionDAO.findById(anyLong(), anyBoolean())).thenReturn(
				currentVersion);

		final NewVersionInfoDB newVersionInfo =
				new NewVersionInfoDB(dao);

		final VersionInfo versionInfo = newVersionInfo.getNewVersionInfo();

		assertEquals(dao.getMadePersistent().size(), 1);

		assertSame(getOnlyElement(dao.getMadePersistent()), versionInfo);

		assertNotNull(versionInfo.getCreated());
		assertEquals(versionInfo.getVersion(),
				Long.valueOf(originalVersion + 1));
		assertEquals(currentVersion.getVersion(),
				Long.valueOf(originalVersion + 1));
		verify(currentVersionDAO, never()).makePersistent(currentVersion);

		newVersionInfo.getNewVersionInfo();

		// Make sure that newVersionInfo only calls initialization code once.
		assertEquals(dao.getMadePersistent().size(), 1);
		verify(currentVersionDAO, never()).makePersistent(currentVersion);
		assertEquals(currentVersion.getVersion(),
				Long.valueOf(originalVersion + 1));

	}

	@Test
	public void initializeVersionInfoNewCurrentVersion() {

		final TestVersionInfoDAO dao = new TestVersionInfoDAO();
		final ICurrentVersionDAO currentVersionDAO = mock(ICurrentVersionDAO.class);

		when(currentVersionDAO.findById(anyLong(), anyBoolean())).thenReturn(
				null);

		final NewVersionInfoDB newVersionInfo = new NewVersionInfoDB(dao);

		final VersionInfo versionInfo = newVersionInfo.getNewVersionInfo();

		assertEquals(dao.getMadePersistent().size(), 1);
		verify(currentVersionDAO).makePersistent(any(CurrentVersion.class));

		assertSame(getOnlyElement(dao.getMadePersistent()), versionInfo);

		assertNotNull(versionInfo.getCreated());

		assertEquals(versionInfo.getVersion(), Long.valueOf(1L));

		newVersionInfo.getNewVersionInfo();

		// Make sure that newVersionInfo only calls initialization code once.
		assertEquals(dao.getMadePersistent().size(), 1);
		verify(currentVersionDAO).makePersistent(any(CurrentVersion.class));
		verify(currentVersionDAO).findById(anyLong(), anyBoolean());
		assertEquals(versionInfo.getVersion(), Long.valueOf(1L));

	}
}
