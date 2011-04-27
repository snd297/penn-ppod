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
import static org.testng.Assert.assertSame;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * Test {@link StandardCharacter}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST })
public class StandardCharacterTest {

	@Test
	public void addState() {
		final StandardCharacter character = new StandardCharacter();
		final StandardState state0 = new StandardState(0);

		character.addState(state0);
		assertEquals(character.getStates().size(), 1);
		assertSame(character.getStates().get(0), state0);
		assertSame(state0.getParent(), character);

		character.addState(state0);
		assertEquals(character.getStates().size(), 1);
		assertSame(character.getStates().get(0), state0);
		assertSame(state0.getParent(), character);
		assertSame(state0.getParent(), character);

		final StandardState state01 = new StandardState(0);
		character.addState(state01);
		assertEquals(character.getStates().size(), 1);
		assertSame(character.getStates().get(0), state01);
		assertSame(state01.getParent(), character);
		assertNull(state0.getParent());
	}

}
