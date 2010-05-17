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

import static com.google.common.collect.Lists.newArrayList;
import static org.testng.Assert.assertSame;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * Test {@link MolecularStateMatrix}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class MolecularStateMatrixTest {

	@Inject
	private Provider<DNAStateMatrix> dnaMatrixProvider;

	@Inject
	private Provider<DNACharacter> dnaCharacterProvider;

	/**
	 * Set the characters on a molecular matrix and make sure that it makes all
	 * of them the same value.
	 */
	@Test
	public void setCharacters() {
		final MolecularStateMatrix molecularMatrix = dnaMatrixProvider.get();
		final MolecularCharacter molecularCharacter = dnaCharacterProvider
				.get();
		molecularMatrix.setCharacters(newArrayList(molecularCharacter));

		for (int i = 0; i < molecularMatrix.getColumnsSize(); i++) {
			assertSame(molecularMatrix.getCharacters().get(i),
					molecularCharacter);
		}
	}
}
