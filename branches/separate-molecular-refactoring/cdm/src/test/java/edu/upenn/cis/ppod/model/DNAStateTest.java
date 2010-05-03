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

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class DNAStateTest {

	/**
	 * Make sure that the state numbers that Mesquite gives us map onto the
	 * correct nucleotides.
	 */
	public void of() {
		assertEquals(DNA.Nucleotide.of(0), DNA.Nucleotide.A);
		assertEquals(DNA.Nucleotide.of(1), DNA.Nucleotide.C);
		assertEquals(DNA.Nucleotide.of(2), DNA.Nucleotide.G);
		assertEquals(DNA.Nucleotide.of(3), DNA.Nucleotide.T);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void ofIllegalArg() {
		assertEquals(DNA.Nucleotide.of(4), DNA.Nucleotide.A);
	}
}
