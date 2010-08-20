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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * @author Sam Donnelly
 * 
 */
@Test(groups = { TestGroupDefs.FAST }, dependsOnGroups = TestGroupDefs.INIT)
public class StudyTest {
	@Inject
	private Provider<Study> studyProvider;

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Test
	public void removeOTUSet() {
		final Study study = studyProvider.get();
		final OTUSet otuSet = otuSetProvider.get();
		study.addOTUSet(otuSet);
		study.unsetInNeedOfNewVersion();
		boolean returnedValue = study.removeOTUSet(otuSet);
		assertTrue(returnedValue);
		assertTrue(study.isInNeedOfNewVersion());
		assertFalse(study.getOTUSets().contains(otuSet));
		assertNull(otuSet.getPosition());

		study.unsetInNeedOfNewVersion();

		study.removeOTUSet(otuSet);
		assertFalse(study.isInNeedOfNewVersion());
		assertNull(otuSet.getParent());
	}

	@Test
	public void setLabel() {
		final Study study = studyProvider.get();
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

	@Test
	public void addOTUSet() {
		final Study study = studyProvider.get();
		final OTUSet otuSet = otuSetProvider.get();
		study.unsetInNeedOfNewVersion();

		final boolean returnedBoolean = study.addOTUSet(otuSet);

		assertTrue(returnedBoolean);
		assertTrue(study.isInNeedOfNewVersion());
		assertEquals(study.getOTUSets().size(), 1);
		assertTrue(study.getOTUSets().contains(otuSet));
		assertEquals(otuSet.getPosition(), Integer.valueOf(0));

		study.unsetInNeedOfNewVersion();

		final boolean returnedBoolean2 = study.addOTUSet(otuSet);
		assertFalse(returnedBoolean2);
		assertFalse(study.isInNeedOfNewVersion());
		assertEquals(study.getOTUSets().size(), 1);
		assertTrue(study.getOTUSets().contains(otuSet));
	}
}
