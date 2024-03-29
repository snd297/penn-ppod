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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.Date;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * Test {@link VersionInfo}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST)
public class VersionInfoTest {

	@Test
	public void getCreated() {
		final VersionInfo vInfo = new VersionInfo();

		assertNull(vInfo.getCreated());

		final Date now = new Date();
		vInfo.setCreated(now);

		assertEquals(vInfo.getCreated(), now);
	}
}
