package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Lists.newArrayList;
import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

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

	private final List<OTU> rowIdxs = newArrayList();

	private CharacterStateMatrix matrix;

	@BeforeMethod
	public void beforeMethod() {
		matrix = matrixProvider.get();
		matrix.setOTUSet(otuSetProvider.get());
		rowIdxs.add(otuProvider.get().setLabel("OTU-0"));
		matrix.getOTUSet().addOTU(rowIdxs.get(0));
		matrix.setOTUs(rowIdxs);
		matrix.addCharacter(characterProvider.get().setLabel("CHARACTER-0"));
	}

	public void addCellToMatrixWOneCharacter() {
		matrix.setRow(rowIdxs.get(0), rowProvider.get());
		final CharacterStateCell cell = cellProvider.get();
		cell.setUnassigned();
		matrix.getRow(matrix.getOTUs().get(0)).addCell(cell);
		ModelAssert.assertEqualsCharacterStateCells(cell, matrix.getRow(
				rowIdxs.get(0)).getCells().get(0));
	}
}
