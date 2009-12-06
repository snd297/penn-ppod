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

import static com.google.common.collect.Sets.newHashSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * Test {@link CharacterStateCell}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class CharacterStateCellTest {

	@Inject
	private Provider<CharacterStateMatrix> matrixProvider;

	@Inject
	private Provider<Character> characterProvider;

	@Inject
	private Provider<CharacterStateRow> rowProvider;

	@Inject
	private Provider<CharacterStateCell> cellProvider;

	@Inject
	private CharacterState.IFactory stateFactory;

	private Set<CharacterState> states;

	private CharacterStateCell cell;

	private CharacterStateMatrix matrix;

	private CharacterState state00;

	private CharacterState state01;

	@BeforeMethod
	public void beforeMethod() {
		cell = cellProvider.get();

		matrix = matrixProvider.get();
		final Character character0 = matrix.addCharacter(characterProvider
				.get().setLabel("character0"));
		final CharacterStateRow row0 = rowProvider.get();
		matrix.setRow(0, row0);

		states = newHashSet();

		// State 0 of character 0
		state00 = stateFactory.create(0);

		character0.addState(state00);

		// State 0 of character 1
		state01 = stateFactory.create(1);
		character0.addState(state01);

	}

	/**
	 * If a cell does not belong to a row, it is illegal to add states to it.
	 */
	@Test(expectedExceptions = IllegalStateException.class)
	public void setStatesForACellThatDoesNotBelongToARow() {
		states.add(state00);
		cell.setTypeAndStates(CharacterStateCell.Type.SINGLE, states);
		// First of all, let's verify that we're testing what we want: a cell w/
		// a single state
		assertEquals(cell.getStates().size(), 1);

		assertEquals(cell.getStates(), states);
	}

	public void getStatesWhenCellHasOneState() {
		states.add(state00);
		matrix.getRow(0).addCell(cell);
		cell.setTypeAndStates(CharacterStateCell.Type.SINGLE, states);
		assertEquals(cell.getStates(), states);
	}

	public void getStatesWhenCellHasMultipleStates() {
		states.add(state00);
		states.add(state01);
		matrix.getRow(0).addCell(cell);

		cell.setTypeAndStates(CharacterStateCell.Type.POLYMORPHIC, states);

		// First of all, let's verify that we're testing what we want: a cell w/
		// multiple states.
		final int cellStatesSize = cell.getStates().size();
		assertTrue(cellStatesSize > 1, "found " + cellStatesSize + " states");

		assertEquals((Object) cell.getStates(), (Object) states);
	}

	public void setTypeAndStatesFromSingleToInapplicable() {
		matrix.getRow(0).addCell(cell);
		states.add(state00);
		cell.setTypeAndStates(CharacterStateCell.Type.SINGLE, states);
		cell.getStates();// to initialize CharacterStateCell.firstStateSet
		cell.setTypeAndStates(CharacterStateCell.Type.INAPPLICABLE,
				new HashSet<CharacterState>());
		assertEquals(cell.getType(), CharacterStateCell.Type.INAPPLICABLE);
		assertEquals(cell.getStates(), new HashSet<CharacterState>());
	}

	public void setTypeAndStatesFromPolymorhpicToInapplicable() {
		matrix.getRow(0).addCell(cell);
		states.add(state00);
		states.add(state01);
		cell.setTypeAndStates(CharacterStateCell.Type.POLYMORPHIC, states);
		cell.setTypeAndStates(CharacterStateCell.Type.INAPPLICABLE,
				new HashSet<CharacterState>());
		assertEquals(cell.getType(), CharacterStateCell.Type.INAPPLICABLE);
		assertEquals(cell.getStates(), new HashSet<CharacterState>());
	}

	public void setTypeAndStatesInapplicable() {
		matrix.getRow(0).addCell(cell);
		cell.setTypeAndStates(CharacterStateCell.Type.INAPPLICABLE, states);
		assertEquals(cell.getType(), CharacterStateCell.Type.INAPPLICABLE);
		assertEquals(cell.getStates(), states);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setTypeAndStatesInapplicableTooManyStates() {
		matrix.getRow(0).addCell(cell);
		states.add(state00);
		cell.setTypeAndStates(CharacterStateCell.Type.INAPPLICABLE, states);
	}

	public void setTypeAndStatesUnassigned() {
		matrix.getRow(0).addCell(cell);
		cell.setTypeAndStates(CharacterStateCell.Type.UNASSIGNED, states);
		assertEquals(cell.getType(), CharacterStateCell.Type.UNASSIGNED);
		assertEquals(cell.getStates(), states);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setTypeAndStatesUnassignedTooManyStates() {
		matrix.getRow(0).addCell(cell);
		states.add(state00);
		cell.setTypeAndStates(CharacterStateCell.Type.UNASSIGNED, states);
	}

	public void setTypeAndStatesSingle() {
		matrix.getRow(0).addCell(cell);
		states.add(state00);
		cell.setTypeAndStates(CharacterStateCell.Type.SINGLE, states);
		assertEquals(cell.getType(), CharacterStateCell.Type.SINGLE);
		assertEquals(cell.getStates(), states);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setTypeAndStatesSingleTooFewStates() {
		matrix.getRow(0).addCell(cell);
		cell.setTypeAndStates(CharacterStateCell.Type.SINGLE, states);
	}

	public void setTypeAndStatesPolymorphic() {
		matrix.getRow(0).addCell(cell);
		states.add(state00);
		states.add(state01);
		cell.setTypeAndStates(CharacterStateCell.Type.POLYMORPHIC, states);
		assertEquals(cell.getType(), CharacterStateCell.Type.POLYMORPHIC);
		assertEquals((Object) cell.getStates(), (Object) states);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setTypeAndStatesPolymorphicTooFewStates() {
		matrix.getRow(0).addCell(cell);
		cell.setTypeAndStates(CharacterStateCell.Type.POLYMORPHIC, states);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setTypeAndStatesSingleTooManyStates() {
		matrix.getRow(0).addCell(cell);
		states.add(state00);
		states.add(state01);
		cell.setTypeAndStates(CharacterStateCell.Type.SINGLE, states);
	}

	public void setTypeAndStatesUncertain() {
		matrix.getRow(0).addCell(cell);
		states.add(state00);
		states.add(state01);
		cell.setTypeAndStates(CharacterStateCell.Type.UNCERTAIN, states);
		assertEquals(cell.getType(), CharacterStateCell.Type.UNCERTAIN);
		assertEquals((Object) cell.getStates(), (Object) states);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setTypeAndStatesUncertainTooFewStates() {
		matrix.getRow(0).addCell(cell);
		states.add(state00);
		cell.setTypeAndStates(CharacterStateCell.Type.UNCERTAIN, states);
	}

	@Test(groups = TestGroupDefs.IN_DEVELOPMENT)
	public void getStatesWXmlStatesNeedsToBePutIntoStatesTrueSingle() {
		cell.setXmlStatesNeedsToBePutIntoStates(true);
		matrix.getRow(0).addCell(cell);
		states.add(state00);
		cell.setTypeAndXmlStates(CharacterStateCell.Type.SINGLE, states);
		assertEquals((Object) cell.getStates(), (Object) states);
		assertFalse(cell.getXmlStatesNeedsToBePutIntoStates());

	}

	public void getStatesWXmlStatesNeedsToBePutIntoStatesTrueInapplicable() {
		cell.setXmlStatesNeedsToBePutIntoStates(true);
		matrix.getRow(0).addCell(cell);
		cell.setTypeAndXmlStates(CharacterStateCell.Type.INAPPLICABLE, null);
		assertEquals((Object) cell.getStates(), (Object) states);
		assertFalse(cell.getXmlStatesNeedsToBePutIntoStates());
	}
}
