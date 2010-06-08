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
import static org.testng.Assert.assertNotNull;
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
	private StandardCell cell;

	@Inject
	private Provider<StandardCell> cellProvider;

	@Inject
	private CellTestSupport<StandardMatrix, StandardRow, StandardCell, StandardState> cellTestSupport;

	@Inject
	private Provider<StandardCharacter> characterProvider;

	@Nullable
	private StandardMatrix matrix;

	@Inject
	private Provider<StandardMatrix> matrixProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<StandardRow> rowProvider;

	@Nullable
	private StandardState state00;

	@Nullable
	private StandardState state01;

	@Inject
	private StandardState.IFactory stateFactory;

	@Nullable
	private Set<StandardState> states;

	/**
	 * Straight {@code beforeMarshal(...) test.
	 */
	@Test
	public void beforeMarshal() {
		matrix.getRow(matrix.getOTUSet().getOTUs().get(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		states.add(state01);
		cell.setPolymorphicElements(states);
		cell.beforeMarshal(null);
		final Set<StandardState> xmlStates = cell.getElementsXml();
		assertNotNull(xmlStates);
		assertEquals(xmlStates.size(), states.size());
		for (final StandardState expectedState : states) {

			// find(...) will throw an exception if what we're looking for is
			// not there
			final StandardState xmlState = find(xmlStates,
						compose(equalTo(expectedState.getStateNumber()),
								StandardState.getStateNumber));
			assertEquals(xmlState.getLabel(), expectedState.getLabel());
		}
	}

	/**
	 * {@code beforeMarshal(...)} should throw an exception if the type has not
	 * bee set yet.
	 */
	@Test(expectedExceptions = IllegalStateException.class)
	public void beforeMarshalBeforeTypeHasBeenSet() {
		cellTestSupport.beforeMarshalBeforeTypeHasBeenSet();
	}

	@BeforeMethod
	public void beforeMethod() {
		cell = cellProvider.get();

		matrix = matrixProvider.get();

		final OTUSet otuSet = otuSetProvider.get();

		final OTU otu0 = otuProvider.get().setLabel("otu0");
		otuSet.setOTUs(newArrayList(otu0));
		matrix.setOTUSet(otuSet);

		final StandardCharacter character0 =
				characterProvider
						.get()
						.setLabel("character0");
		matrix.setCharacters(newArrayList(character0));
		final StandardRow row0 = rowProvider.get();
		matrix.putRow(matrix.getOTUSet().getOTUs().get(0), row0);

		states = newHashSet();

		// State 0 of character 0
		state00 = stateFactory.create(0);

		character0.addState(state00);

		// State 0 of character 1
		state01 = stateFactory.create(1);
		character0.addState(state01);

	}

	@Test
	public void getElementsWhenCellHasMultipleElements() {
		states.add(state00);
		states.add(state01);
		cellTestSupport.getStatesWhenCellHasMultipleElements(matrix, states);
	}

	@Test
	public void getElementsWhenCellHasOneElement() {
		cellTestSupport.getStatesWhenCellHasOneElement(matrix, state00);
	}

	/**
	 * If a cell does not belong to a row, it is illegal to add states to it.
	 */
	@Test(expectedExceptions = IllegalStateException.class)
	public void setStatesForACellThatDoesNotBelongToARow() {
		cellTestSupport.setStatesForACellThatDoesNotBelongToARow(state00);
	}

	@Test
	public void setTypeAndStatesFromPolymorhpicToInapplicable() {
		matrix.getRow(matrix.getOTUSet().getOTUs().get(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		states.add(state01);
		cell.setPolymorphicElements(states);
		cell.setInapplicable();
		assertEquals(cell.getType(), Cell.Type.INAPPLICABLE);
		assertTrue(isEmpty(cell.getElements()));
	}

	@Test
	public void setTypeAndStatesFromSingleToInapplicable() {
		matrix.getRow(matrix.getOTUSet().getOTUs().get(0)).setCells(
				Arrays.asList(cell));
		cell.setSingleElement(state00);
		cell.setInapplicable();
		assertEquals(cell.getType(), Cell.Type.INAPPLICABLE);

		// Make sure it's empty
		assertEquals(cell.getElements().size(), 0);
	}

	@Test
	public void setTypeAndStatesInapplicable() {
		matrix
				.getRow(matrix.getOTUSet().getOTUs().get(0)).setCells(
						Arrays.asList(cell));
		cell.setInapplicable();
		assertEquals(cell.getType(), Cell.Type.INAPPLICABLE);
		assertEquals((Object) cell.getElements(), (Object) states);
	}

	@Test
	public void setTypeAndStatesPolymorphic() {
		matrix.getRow(matrix.getOTUSet().getOTUs().get(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		states.add(state01);
		cell.setPolymorphicElements(states);
		assertEquals(cell.getType(), Cell.Type.POLYMORPHIC);
		assertEquals((Object) cell.getElements(), (Object) states);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setTypeAndStatesPolymorphicTooFewStates() {
		matrix.getRow(matrix.getOTUSet().getOTUs().get(0)).setCells(
				Arrays.asList(cell));
		cell.setPolymorphicElements(states);
	}

	@Test
	public void setTypeAndStatesSingle() {
		matrix.getRow(matrix.getOTUSet().getOTUs().get(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		cell.setSingleElement(state00);
		assertEquals(cell.getType(), Cell.Type.SINGLE);
		assertEquals((Object) cell.getElements(), (Object) states);
	}

	@Test
	public void setTypeAndStatesUnassigned() {
		matrix.getRow(matrix.getOTUSet().getOTUs().get(0)).setCells(
				Arrays.asList(cell));
		cell.setUnassigned();
		assertEquals(cell.getType(), Cell.Type.UNASSIGNED);
		assertEquals((Object) cell.getElements(), (Object) states);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setTypeAndStatesUncertainTooFewStates() {
		matrix.getRow(matrix.getOTUSet().getOTUs().get(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		cell.setUncertainElements(states);
	}

	@Test
	public void setUncertainStates() {
		matrix.getRow(matrix.getOTUSet().getOTUs().get(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		states.add(state01);
		cell.setUncertainElements(states);
		assertEquals(cell.getType(), Cell.Type.UNCERTAIN);
		assertEquals((Object) cell.getElements(), (Object) newHashSet(states));
	}
}
