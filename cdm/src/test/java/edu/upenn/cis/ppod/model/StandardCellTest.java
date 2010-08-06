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
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Set;

import javax.annotation.Nullable;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.modelinterfaces.ICell;

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
	private Provider<StandardCharacter> characterProvider;

	@Nullable
	private StandardMatrix matrix;

	@Inject
	private Provider<StandardMatrix> standardMatrixProvider;

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

	@BeforeMethod
	public void beforeMethod() {
		cell = cellProvider.get();

		matrix = standardMatrixProvider.get();

		final OTUSet otuSet = otuSetProvider.get();

		final OTU otu0 = otuProvider.get().setLabel("otu0");
		otuSet.setOTUs(newArrayList(otu0));
		matrix.setParent(otuSet);

		final StandardCharacter character0 =
				characterProvider
						.get()
						.setLabel("character0");
		matrix.setCharacters(newArrayList(character0));
		final StandardRow row0 = rowProvider.get();
		matrix.putRow(matrix.getParent().getOTUs().get(0), row0);

		states = newHashSet();

		// State 0 of character 0
		state00 = stateFactory.create(0);

		character0.addState(state00);

		// State 0 of character 1
		state01 = stateFactory.create(1);
		character0.addState(state01);

	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setPolymorphicElementsTooFewStates() {
		matrix.getRow(
				matrix.getParent()
						.getOTUs()
						.get(0))
				.setCells(
						Arrays.asList(cell));
		states.add(state00);
		cell.setPolymorphicElements(states);
	}

	@Test
	public void setInapplcableWasSingle() {
		matrix.getRow(matrix.getParent().getOTUs().get(0)).setCells(
				Arrays.asList(cell));
		cell.setSingleElement(state00);
		cell.setInapplicable();
		assertEquals(cell.getType(), ICell.Type.INAPPLICABLE);

		// Make sure it's empty
		assertEquals(cell.getElements().size(), 0);
	}

	@Test
	public void setInapplicableWasPolymorphic() {
		matrix.getRow(
				matrix.getParent()
						.getOTUs()
						.get(0))
				.setCells(
						Arrays.asList(cell));
		states.add(state00);
		states.add(state01);
		cell.setPolymorphicElements(states);
		cell.setInapplicable();
		assertEquals(cell.getType(), ICell.Type.INAPPLICABLE);
		assertTrue(isEmpty(cell.getElements()));
	}

	@Test
	public void setPolymorphicElements() {
		matrix.getRow(matrix.getParent().getOTUs().get(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		states.add(state01);
		cell.setPolymorphicElements(states);
		assertEquals(cell.getType(), ICell.Type.POLYMORPHIC);
		assertEquals((Object) cell.getElements(), (Object) states);
	}

	@Test
	public void setSingleElement() {
		matrix.getRow(matrix.getParent().getOTUs().get(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		cell.setSingleElement(state00);
		assertEquals(cell.getType(), ICell.Type.SINGLE);
		assertEquals((Object) cell.getElements(), (Object) states);
	}

	/**
	 * If a cell does not belong to a row, it is illegal to add states to it.
	 */
	@Test(expectedExceptions = IllegalStateException.class)
	public void setStatesForACellThatDoesNotBelongToARow() {
		final StandardCell cell = cellProvider.get();
		cell.setSingleElement(state00);
	}

	@Test
	public void setUncertainElements() {
		matrix.getRow(matrix.getParent().getOTUs().get(0)).setCells(
				Arrays.asList(cell));
		states.add(state00);
		states.add(state01);
		cell.setUncertainElements(states);
		assertEquals(cell.getType(), ICell.Type.UNCERTAIN);
		assertEquals((Object) cell.getElements(), (Object) newHashSet(states));
	}

}
