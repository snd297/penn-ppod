/*
 * Copyright (C) 2009 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dto.PPodCellType;

/**
 * Test {@link CharacterStateCell}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST)
public class StandardCellTest {

	private StandardCell cell;

	private StandardState state00;

	private StandardState state01;

	private Set<StandardState> states;

	private StandardMatrix matrix;

	@Test
	public void setInapplcableWasSingle() {
		matrix.getRows()
				.get(matrix.getParent().getOtus().get(0)).setCells(
						Arrays.asList(cell));
		cell.setSingleWithStateNo(state00.getStateNumber());
		cell.setInapplicable();
		assertEquals(cell.getType(), PPodCellType.INAPPLICABLE);

		// Make sure it's empty
		assertEquals(cell.getElements().size(), 0);
	}

	@Test
	public void setInapplicableWasPolymorphic() {
		matrix.getRows().get(
				matrix.getParent()
						.getOtus()
						.get(0))
				.setCells(
						Arrays.asList(cell));
		states.add(state00);
		states.add(state01);
		cell.setPolymorphicWithStateNos(
				newHashSet(
						transform(states, StandardState.getStateNumber)));
		cell.setInapplicable();
		assertEquals(cell.getType(), PPodCellType.INAPPLICABLE);
		assertTrue(isEmpty(cell.getElements()));
	}

	@Test
	public void setInNeedOfNewVersion() {
		matrix.getRows().get(
				matrix.getParent()
						.getOtus()
						.get(0))
				.setCells(
						Arrays.asList(cell));
		assertFalse(cell.isInNeedOfNewVersion());
		cell.setInNeedOfNewVersion();
		assertTrue(cell.isInNeedOfNewVersion());
		assertNull(matrix.getColumnVersionInfos().get(cell.getPosition()));
	}

	@Test
	public void setPolymorphicElements() {
		matrix.getRows().get(matrix.getParent().getOtus().get(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		states.add(state01);
		cell.setPolymorphicWithStateNos(
				newHashSet(
						transform(states, StandardState.getStateNumber)));
		assertEquals(cell.getType(), PPodCellType.POLYMORPHIC);
		assertEquals((Object) cell.getElements(), (Object) states);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setPolymorphicElementsTooFewStates() {
		matrix.getRows().get(
				matrix.getParent()
						.getOtus()
						.get(0))
				.setCells(
						Arrays.asList(cell));
		states.add(state00);
		cell.setPolymorphicWithStateNos(
				newHashSet(
						transform(states, StandardState.getStateNumber)));
	}

	@Test
	public void setSingleElement() {
		matrix
				.getRows()
				.get(matrix.getParent().getOtus().get(0))
				.setCells(Arrays.asList(cell));
		states.add(state00);

		cell.unsetInNeedOfNewVersion();
		cell.setSingleWithStateNo(state00.getStateNumber());
		assertTrue(cell.isInNeedOfNewVersion());
		assertEquals(cell.getType(), PPodCellType.SINGLE);
		assertEquals(cell.getElements(), states);

		cell.unsetInNeedOfNewVersion();
		cell.setSingleWithStateNo(state00.getStateNumber());
		assertFalse(cell.isInNeedOfNewVersion());
		assertEquals(cell.getType(), PPodCellType.SINGLE);
		assertEquals(cell.getElements(), states);
	}

	/**
	 * If a cell does not belong to a row, it is illegal to add states to it.
	 */
	@Test(expectedExceptions = IllegalStateException.class)
	public void setStatesForACellThatDoesNotBelongToARow() {
		final StandardCell cell = new StandardCell();
		cell.setSingleWithStateNo(state00.getStateNumber());
	}

	@Test
	public void setUncertainElements() {
		matrix.getRows().get(matrix.getParent().getOtus().get(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		states.add(state01);
		cell.setUncertain(states);
		assertEquals(cell.getType(), PPodCellType.UNCERTAIN);
		assertEquals((Object) cell.getElements(), (Object) newHashSet(states));
	}

	@BeforeMethod
	public void setUp() {
		cell = new StandardCell();

		matrix = new StandardMatrix();

		final OtuSet otuSet = new OtuSet();

		final Otu otu0 = new Otu("otu0");
		otuSet.setOtus(newArrayList(otu0));
		matrix.setParent(otuSet);

		final StandardCharacter character0 =
				new StandardCharacter();
		character0.setLabel("character0");
		matrix.setCharacters(newArrayList(character0));
		final StandardRow row0 = new StandardRow();
		matrix.putRow(matrix.getParent().getOtus().get(0), row0);

		states = newHashSet();

		// State 0 of character 0
		state00 = new StandardState(0);

		character0.addState(state00);

		// State 0 of character 1
		state01 = new StandardState(1);
		character0.addState(state01);

	}
}
