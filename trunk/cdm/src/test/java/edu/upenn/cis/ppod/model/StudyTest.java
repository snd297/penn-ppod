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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.IStudy;

/**
 * @author Sam Donnelly
 * 
 */
@Test(groups = { TestGroupDefs.FAST }, dependsOnGroups = TestGroupDefs.INIT)
public class StudyTest {
	@Inject
	private Provider<Study> studyProvider;

	@Inject
	private Provider<IOTUSet> otuSetProvider;

	@Test
	public void removeOTUSet() {
		final IStudy study = studyProvider.get();
		final IOTUSet otuSet0 = otuSetProvider.get();
		final IOTUSet otuSet1 = otuSetProvider.get();
		final IOTUSet otuSet2 = otuSetProvider.get();
		study.addOTUSet(otuSet0);
		study.addOTUSet(otuSet1);
		study.addOTUSet(otuSet2);
		study.unsetInNeedOfNewVersion();

		study.removeOTUSet(otuSet1);
		assertTrue(study.isInNeedOfNewVersion());
		assertFalse(study.getOTUSets().contains(otuSet1));

		assertEquals(study.getOTUSets(),
				ImmutableList.of(otuSet0, otuSet2));
	}

	@Test
	public void setLabel() {
		final IStudy study = studyProvider.get();
		study.unsetInNeedOfNewVersion();
		final String label = "otu-set-label";
		study.setLabel(label);
		assertTrue(study.isInNeedOfNewVersion());
		study.isInNeedOfNewVersion();

		assertEquals(study.getLabel(), label);

		study.unsetInNeedOfNewVersion();
		study.setLabel(label);

		assertFalse(study.isInNeedOfNewVersion());
		assertEquals(study.getLabel(), label);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void addOTUSetWAlreadyContainedOTUSet() {
		final IStudy study = studyProvider.get();
		final IOTUSet otuSet0 = otuSetProvider.get();
		study.addOTUSet(otuSet0);
		study.addOTUSet(otuSet0);
	}

	@Test
	public void addOTUSet() {
		final IStudy study = studyProvider.get();
		final IOTUSet otuSet0 = otuSetProvider.get();

		study.unsetInNeedOfNewVersion();

		study.addOTUSet(otuSet0);
		assertTrue(study.isInNeedOfNewVersion());
		assertEquals(study.getOTUSets().size(), 1);
		assertTrue(study.getOTUSets().contains(otuSet0));
		assertSame(otuSet0.getParent(), study);
	}

	@Test
	public void addOTUSetPos() {
		final IStudy study = studyProvider.get();
		final IOTUSet otuSet0 = otuSetProvider.get();
		final IOTUSet otuSet1 = otuSetProvider.get();
		final IOTUSet otuSet2 = otuSetProvider.get();
		final IOTUSet otuSet3 = otuSetProvider.get();

		study.addOTUSet(otuSet0);
		study.addOTUSet(otuSet1);
		study.addOTUSet(otuSet2);

		study.unsetInNeedOfNewVersion();

		study.addOTUSet(2, otuSet3);
		assertTrue(study.isInNeedOfNewVersion());
		assertEquals(study.getOTUSets(),
				ImmutableList.of(otuSet0, otuSet1, otuSet3, otuSet2));
		assertSame(otuSet3.getParent(), study);
	}
}
