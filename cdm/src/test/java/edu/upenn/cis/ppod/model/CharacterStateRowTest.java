package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sun.xml.bind.v2.runtime.reflect.Lister.CollectionLister;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * {@link CharacterStateRow} test.
 * 
 * @author Sam Donnelly
 * 
 */
@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class CharacterStateRowTest {

	@Inject
	private Provider<CharacterStateMatrix> matrixProvider;

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Inject
	private Provider<Character> characterProvider;

	@Inject
	private Provider<CharacterStateRow> rowProvider;

	@Inject
	private Provider<CharacterStateCell> cellProvider;

	private List<OTU> rowIdxs;

	private CharacterStateMatrix matrix;

	@BeforeMethod
	public void beforeMethod() {
		matrix = matrixProvider.get();
		matrix.setOTUSet(otuSetProvider.get());
		rowIdxs = newArrayList();
		rowIdxs.add(otuProvider.get().setLabel("OTU-0"));
		matrix.getOTUSet().setOTUs(newHashSet(rowIdxs.get(0)));
		matrix.setOTUs(rowIdxs);
		matrix.setRow(rowIdxs.get(0), rowProvider.get());
		matrix.addCharacter(characterProvider.get().setLabel("CHARACTER-0"));
	}

	public void addCellToMatrixWOneCharacter() {
		final CharacterStateCell cell = cellProvider.get().setUnassigned();
		matrix.getRow(matrix.getOTUs().get(0)).setCells(newArrayList(cell));
		ModelAssert.assertEqualsCharacterStateCells(cell, matrix.getRow(
				rowIdxs.get(0)).getCells().get(0));
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void addCellToRowThatsNotInAMatrix() {
		// Just call setUnassigned so that the cell is in a legal state - it
		// shouldn't really matterJust call setUnassigned so that the cell is in
		// a legal state - it shouldn't really matter
		rowProvider.get().setCells(
				newArrayList(cellProvider.get().setUnassigned()));
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void addCellToMatrixThatHasNoCharacters() {
		matrix.setCharacters((List<Character>) Collections.EMPTY_LIST);

		// Just call setUnassigned so that the cell is in a legal state - it
		// shouldn't really matterJust call setUnassigned so that the cell is in
		// a legal state - it shouldn't really matter
		matrix.getRow(matrix.getOTUs().get(0)).setCells(
				newArrayList(cellProvider.get().setUnassigned()));
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void addCellToMatrixWTooFewCharacters() {
		final List<CharacterStateCell> cells = newArrayList(cellProvider.get()
				.setUnassigned(), cellProvider.get().setUnassigned());
		matrix.getRow(matrix.getOTUs().get(0)).setCells(cells);
	}
}
