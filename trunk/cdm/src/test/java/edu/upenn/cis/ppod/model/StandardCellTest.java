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

import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
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
public class StandardCellTest {

	@Nullable
	private CharacterStateCell cell;

	@Inject
	private Provider<CharacterStateCell> cellProvider;

	@Inject
	private CellTest<CharacterStateMatrix, CharacterStateRow, CharacterStateCell, CharacterState> cellTest;

	@Inject
	private Provider<Character> characterProvider;

	@Nullable
	private CharacterStateMatrix matrix;

	@Inject
	private Provider<CharacterStateMatrix> matrixProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<CharacterStateRow> rowProvider;

	@Nullable
	private CharacterState state00;

	@Nullable
	private CharacterState state01;

	@Inject
	private CharacterState.IFactory stateFactory;

	@Nullable
	private Set<CharacterState> states;

	@Test
	public void afterUnmarshal() {
		states.add(state00);
		states.add(state01);
		cellTest.afterUnmarshal(matrix, states);
	}

	/**
	 * Straight {@code beforeMarshal(...) test.
	 */
	@Test
	public void beforeMarshal() {
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		states.add(state01);
		cell.setPolymorphicElements(states);
		cell.beforeMarshal(null);
		final Set<CharacterState> xmlStates = cell.getXmlElements();
		assertEquals(xmlStates.size(), states.size());
		for (final CharacterState expectedState : states) {

			// find(...) will throw an exception if what we're looking for is
			// not there
			final CharacterState xmlState = find(xmlStates,
						compose(equalTo(expectedState.getStateNumber()),
								CharacterState.getStateNumber));
			assertEquals(xmlState.getLabel(), expectedState.getLabel());
		}
	}

	/**
	 * {@code beforeMarshal(...)} should throw an exception if the type has not
	 * bee set yet.
	 */
	@Test(expectedExceptions = IllegalStateException.class)
	public void beforeMarshalBeforeTypeHasBeenSet() {
		cellTest.beforeMarshalBeforeTypeHasBeenSet();
	}

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

		character0.putState(state00);

		// State 0 of character 1
		state01 = stateFactory.create(1);
		character0.putState(state01);

	}

	@Test
	public void getStatesWhenCellHasMultipleElements() {
		states.add(state00);
		states.add(state01);
		cellTest.getStatesWhenCellHasMultipleElements(matrix, states);
	}

	@Test
	public void getStatesWhenCellHasOneElement() {
		cellTest.getStatesWhenCellHasOneElement(matrix, state00);
	}

	@Test(groups = TestGroupDefs.IN_DEVELOPMENT)
	public void getStatesWXmlStatesNeedsToBePutIntoStatesTrueSingle() {
		cell.setXmlStatesNeedsToBePutIntoStates(true);
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		cell.setTypeAndXmlElements(Cell.Type.SINGLE, states);
		assertEquals(newHashSet(cell), states);
		assertFalse(cell.getXmlStatesNeedsToBePutIntoStates());
	}

	/**
	 * If a cell does not belong to a row, it is illegal to add states to it.
	 */
	@Test(expectedExceptions = IllegalStateException.class)
	public void setStatesForACellThatDoesNotBelongToARow() {
		cellTest.setStatesForACellThatDoesNotBelongToARow(state00);
	}

	@Test
	public void setTypeAndStatesFromPolymorhpicToInapplicable() {
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		states.add(state01);
		cell.setPolymorphicElements(states);
		cell.setInapplicable();
		assertEquals(cell.getType(), Cell.Type.INAPPLICABLE);
		assertTrue(isEmpty(newHashSet(cell)));
	}

	@Test
	public void setTypeAndStatesFromSingleToInapplicable() {
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		cell.setSingleElement(state00);
		cell.setInapplicable();
		assertEquals(cell.getType(), Cell.Type.INAPPLICABLE);

		// Make sure it's empty
		assertFalse(cell.iterator().hasNext());
	}

	@Test
	public void setTypeAndStatesInapplicable() {
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		cell.setInapplicable();
		assertEquals(cell.getType(), Cell.Type.INAPPLICABLE);
		assertEquals((Object) newHashSet(cell), (Object) states);
	}

	@Test
	public void setTypeAndStatesPolymorphic() {
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		states.add(state01);
		cell.setPolymorphicElements(states);
		assertEquals(cell.getType(), Cell.Type.POLYMORPHIC);
		assertEquals((Object) newHashSet(cell), (Object) states);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setTypeAndStatesPolymorphicTooFewStates() {
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		cell.setPolymorphicElements(states);
	}

	@Test
	public void setTypeAndStatesSingle() {
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		cell.setSingleElement(state00);
		assertEquals(cell.getType(), Cell.Type.SINGLE);
		assertEquals((Object) newHashSet(cell), (Object) states);
	}

	@Test
	public void setTypeAndStatesUnassigned() {
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		cell.setUnassigned();
		assertEquals(cell.getType(), Cell.Type.UNASSIGNED);
		assertEquals((Object) newHashSet(cell), (Object) states);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setTypeAndStatesUncertainTooFewStates() {
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		cell.setUncertainElements(states);
	}

	@Test
	public void setUncertainStates() {
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		states.add(state01);
		cell.setUncertainElements(states);
		assertEquals(cell.getType(), Cell.Type.UNCERTAIN);
		assertEquals((Object) newHashSet(cell), (Object) newHashSet(states));
	}
}
