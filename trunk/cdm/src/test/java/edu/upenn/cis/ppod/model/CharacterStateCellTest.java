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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

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

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Nullable
	private Set<CharacterState> states;

	@Nullable
	private CharacterStateCell cell;

	@Nullable
	private CharacterStateMatrix matrix;

	@Nullable
	private CharacterState state00;

	@Nullable
	private CharacterState state01;

	@BeforeMethod
	public void beforeMethod() {
		cell = cellProvider.get();

		matrix = matrixProvider.get();

		final OTUSet otuSet = otuSetProvider.get();

		final OTU otu0 = otuProvider.get().setLabel("otu0");
		otuSet.setOTUs(newArrayList(otu0));
		matrix.setOTUSet(otuSet);

		final Character character0 = characterProvider.get().setLabel(
				"character0");
		matrix.setCharacters(newArrayList(character0));
		final CharacterStateRow row0 = rowProvider.get();
		matrix.putRow(matrix.getOTUSet().getOTU(0), row0);

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
		cell.setSingleState(state00);
	}

	public void getStatesWhenCellHasOneState() {
		states.add(state00);
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));

		cell.setSingleState(state00);
		assertEquals(newHashSet(cell), states);
	}

	public void getStatesWhenCellHasMultipleStates() {
		states.add(state00);
		states.add(state01);
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));

		cell.setPolymorphicStates(states);

		// First of all, let's verify that we're testing what we want: a cell w/
		// multiple states.
		final int cellStatesSize = cell.getStatesSize();
		assertTrue(cellStatesSize > 1, "found " + cellStatesSize + " states");

		assertEquals((Object) newHashSet(cell), (Object) states);
	}

	public void setTypeAndStatesFromSingleToInapplicable() {
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		cell.setSingleState(state00);
		cell.setInapplicable();
		assertEquals(cell.getType(), CharacterStateCell.Type.INAPPLICABLE);
		assertEquals(newHashSet(cell), new HashSet<CharacterState>());
	}

	public void setTypeAndStatesFromPolymorhpicToInapplicable() {
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		states.add(state01);
		cell.setPolymorphicStates(states);
		cell.setInapplicable();
		assertEquals(cell.getType(), CharacterStateCell.Type.INAPPLICABLE);
		assertEquals(newHashSet(cell), new HashSet<CharacterState>());
	}

	public void setTypeAndStatesInapplicable() {
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		cell.setInapplicable();
		assertEquals(cell.getType(), CharacterStateCell.Type.INAPPLICABLE);
		assertEquals(newHashSet(cell), states);
	}

	public void setTypeAndStatesUnassigned() {
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		cell.setUnassigned();
		assertEquals(cell.getType(), CharacterStateCell.Type.UNASSIGNED);
		assertEquals(newHashSet(cell), states);
	}

	public void setTypeAndStatesSingle() {
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		cell.setSingleState(state00);
		assertEquals(cell.getType(), CharacterStateCell.Type.SINGLE);
		assertEquals(newHashSet(cell), states);
	}

	public void setTypeAndStatesPolymorphic() {
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		states.add(state01);
		cell.setPolymorphicStates(states);
		assertEquals(cell.getType(), CharacterStateCell.Type.POLYMORPHIC);
		assertEquals((Object) newHashSet(cell), (Object) states);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setTypeAndStatesPolymorphicTooFewStates() {
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		cell.setPolymorphicStates(states);
	}

	public void setUncertainStates() {
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		states.add(state01);
		cell.setUncertainStates(states);
		assertEquals(cell.getType(), CharacterStateCell.Type.UNCERTAIN);
		assertEquals(newHashSet(cell), newHashSet(states));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setTypeAndStatesUncertainTooFewStates() {
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		cell.setUncertainStates(states);
	}

	@Test(groups = TestGroupDefs.IN_DEVELOPMENT)
	public void getStatesWXmlStatesNeedsToBePutIntoStatesTrueSingle() {
		cell.setXmlStatesNeedsToBePutIntoStates(true);
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		cell.setTypeAndXmlStates(CharacterStateCell.Type.SINGLE, states);
		assertEquals(newHashSet(cell), states);
		assertFalse(cell.getXmlStatesNeedsToBePutIntoStates());

	}

	public void afterUnmarshal() {
		cell.setXmlStatesNeedsToBePutIntoStates(true);
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		cell.setTypeAndXmlStates(CharacterStateCell.Type.UNCERTAIN, states);
		cell.afterUnmarshal();
		// assertEquals((Object) cell.getStates(), (Object) states);
		assertFalse(cell.getXmlStatesNeedsToBePutIntoStates());
	}
}
