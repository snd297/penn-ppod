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

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * @author Sam Donnelly
 * 
 */
@Test(groups = { TestGroupDefs.FAST })
public class StudyTest {

	@Test
	public void removeOTUSet() {
		final Study study = new Study();
		final OtuSet otuSet0 = new OtuSet();
		otuSet0.setLabel("otu-set-0");
		final OtuSet otuSet1 = new OtuSet();
		otuSet1.setLabel("otu-set-1");
		final OtuSet otuSet2 = new OtuSet();
		otuSet2.setLabel("otu-set-2");

		study.addOtuSet(otuSet0);
		study.addOtuSet(otuSet1);
		study.addOtuSet(otuSet2);

		study.removeOtuSet(otuSet1);
		assertFalse(study.getOtuSets().contains(otuSet1));

		assertEquals(study.getOtuSets(),
				ImmutableList.of(otuSet0, otuSet2));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void addOTUSetWAlreadyContainedOTUSet() {
		final Study study = new Study();
		final OtuSet otuSet0 = new OtuSet();
		study.addOtuSet(otuSet0);
		study.addOtuSet(otuSet0);
	}

	@Test
	public void addOTUSet() {
		final Study study = new Study();
		final OtuSet otuSet0 = new OtuSet();

		study.addOtuSet(otuSet0);
		assertEquals(study.getOtuSets().size(), 1);
		assertTrue(study.getOtuSets().contains(otuSet0));
		assertSame(otuSet0.getParent(), study);
	}

	@Test
	public void addOTUSetPos() {
		final Study study = new Study();
		final OtuSet otuSet0 = new OtuSet();
		otuSet0.setLabel("otu-set-0");
		final OtuSet otuSet1 = new OtuSet();
		otuSet1.setLabel("otu-set-1");
		final OtuSet otuSet2 = new OtuSet();
		otuSet2.setLabel("otu-set-2");
		final OtuSet otuSet3 = new OtuSet();
		otuSet3.setLabel("otu-set-3");

		study.addOtuSet(otuSet0);
		study.addOtuSet(otuSet1);
		study.addOtuSet(otuSet2);

		study.addOtuSet(2, otuSet3);
		assertEquals(study.getOtuSets(),
				ImmutableList.of(otuSet0, otuSet1, otuSet3, otuSet2));
		assertSame(otuSet3.getParent(), study);
	}
}
