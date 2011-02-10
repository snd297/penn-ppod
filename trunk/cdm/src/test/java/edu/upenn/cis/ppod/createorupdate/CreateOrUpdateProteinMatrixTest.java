package edu.upenn.cis.ppod.createorupdate;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;

import java.util.EnumSet;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dto.PPodCellType;
import edu.upenn.cis.ppod.dto.PPodProtein;
import edu.upenn.cis.ppod.dto.PPodProteinRow;
import edu.upenn.cis.ppod.model.ProteinCell;

@Test(groups = TestGroupDefs.FAST)
public class CreateOrUpdateProteinMatrixTest {

	@Test
	public void docCellDbCellUnassigned() {
		final ProteinCell dbCell = new ProteinCell();

		CreateOrUpdateProteinMatrix.docCell2DbCell(dbCell,
				PPodCellType.UNASSIGNED, "?");

		assertEquals(dbCell.getType(), PPodCellType.UNASSIGNED);
	}

	@Test
	public void docCell2DbCellSingle() {

		final ProteinCell dbCell = new ProteinCell();

		for (final char proteinChar : PPodProteinRow.LEGAL_CHARS) {
			if (proteinChar == '?' || proteinChar == '-') {
				continue;
			}
			final String proteinCharStr = String.valueOf(proteinChar);
			CreateOrUpdateProteinMatrix.docCell2DbCell(dbCell,
					PPodCellType.SINGLE,
					proteinCharStr);
			assertEquals(dbCell.getType(), PPodCellType.SINGLE);
			assertEquals(getOnlyElement(dbCell.getElements()).toString(),
					proteinCharStr);
		}

	}

	@Test
	public void docCellDbCellPolymorphic() {
		final ProteinCell dbCell = new ProteinCell();
		CreateOrUpdateProteinMatrix.docCell2DbCell(dbCell,
				PPodCellType.POLYMORPHIC, "AHX");
		assertEquals(dbCell.getType(), PPodCellType.POLYMORPHIC);
		assertEquals(dbCell.getElements(),
				EnumSet.of(PPodProtein.A, PPodProtein.H, PPodProtein.X));
	}

	@Test
	public void docCellDbCellUncertain() {
		final ProteinCell dbCell = new ProteinCell();
		CreateOrUpdateProteinMatrix.docCell2DbCell(dbCell,
				PPodCellType.UNCERTAIN, "AHX");
		assertEquals(dbCell.getType(), PPodCellType.UNCERTAIN);
		assertEquals(dbCell.getElements(),
				EnumSet.of(PPodProtein.A, PPodProtein.H, PPodProtein.X));
	}

	@Test
	public void docCellDbCellInapplicable() {
		final ProteinCell dbCell = new ProteinCell();

		CreateOrUpdateProteinMatrix.docCell2DbCell(dbCell,
				PPodCellType.INAPPLICABLE, "-");

		assertEquals(dbCell.getType(), PPodCellType.INAPPLICABLE);
	}

}
