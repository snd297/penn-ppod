package edu.upenn.cis.ppod.createorupdate;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dto.PPodCellType;
import edu.upenn.cis.ppod.dto.PPodDnaNucleotide;
import edu.upenn.cis.ppod.model.DnaCell;

@Test(groups = TestGroupDefs.FAST)
public class CreateOrUpdateDnaMatrixTest {

	@Test
	public void docCell2DbCellUnassigned() {

		final DnaCell dbCell = new DnaCell();
		CreateOrUpdateDnaMatrix.docCell2DbCell(dbCell, PPodCellType.UNASSIGNED,
				"?");

		assertEquals(dbCell.getType(), PPodCellType.UNASSIGNED);
	}

	@Test
	public void docCell2DbCellSingleUpperCase() {

		final DnaCell dbCell = new DnaCell();
		CreateOrUpdateDnaMatrix.docCell2DbCell(dbCell, PPodCellType.SINGLE,
				"A");

		assertEquals(dbCell.getType(), PPodCellType.SINGLE);
		assertFalse(dbCell.getLowerCase());
		assertEquals(getOnlyElement(dbCell.getElements()), PPodDnaNucleotide.A);

		CreateOrUpdateDnaMatrix.docCell2DbCell(dbCell, PPodCellType.SINGLE,
				"C");

		assertEquals(dbCell.getType(), PPodCellType.SINGLE);
		assertFalse(dbCell.getLowerCase());
		assertEquals(getOnlyElement(dbCell.getElements()), PPodDnaNucleotide.C);

		CreateOrUpdateDnaMatrix.docCell2DbCell(dbCell, PPodCellType.SINGLE,
				"G");

		assertEquals(dbCell.getType(), PPodCellType.SINGLE);
		assertFalse(dbCell.getLowerCase());
		assertEquals(getOnlyElement(dbCell.getElements()), PPodDnaNucleotide.G);

		CreateOrUpdateDnaMatrix.docCell2DbCell(dbCell, PPodCellType.SINGLE,
				"T");

		assertEquals(dbCell.getType(), PPodCellType.SINGLE);
		assertFalse(dbCell.getLowerCase());
		assertEquals(getOnlyElement(dbCell.getElements()), PPodDnaNucleotide.T);
	}

	@Test
	public void docCell2DbCellSingleLowerCase() {

		final DnaCell dbCell = new DnaCell();
		CreateOrUpdateDnaMatrix.docCell2DbCell(dbCell, PPodCellType.SINGLE,
				"a");

		assertEquals(dbCell.getType(), PPodCellType.SINGLE);
		assertTrue(dbCell.getLowerCase());
		assertEquals(getOnlyElement(dbCell.getElements()), PPodDnaNucleotide.A);

		CreateOrUpdateDnaMatrix.docCell2DbCell(dbCell, PPodCellType.SINGLE,
				"c");

		assertEquals(dbCell.getType(), PPodCellType.SINGLE);
		assertTrue(dbCell.getLowerCase());
		assertEquals(getOnlyElement(dbCell.getElements()), PPodDnaNucleotide.C);

		CreateOrUpdateDnaMatrix.docCell2DbCell(dbCell, PPodCellType.SINGLE,
				"g");

		assertEquals(dbCell.getType(), PPodCellType.SINGLE);
		assertTrue(dbCell.getLowerCase());
		assertEquals(getOnlyElement(dbCell.getElements()), PPodDnaNucleotide.G);

		CreateOrUpdateDnaMatrix.docCell2DbCell(dbCell, PPodCellType.SINGLE,
				"t");

		assertEquals(dbCell.getType(), PPodCellType.SINGLE);
		assertTrue(dbCell.getLowerCase());
		assertEquals(getOnlyElement(dbCell.getElements()), PPodDnaNucleotide.T);
	}

	@Test
	public void docCell2DbCellPolymorphicUpperCase() {

		final DnaCell dbCell = new DnaCell();
		CreateOrUpdateDnaMatrix.docCell2DbCell(dbCell,
				PPodCellType.POLYMORPHIC,
				"GTC");

		assertEquals(dbCell.getType(), PPodCellType.POLYMORPHIC);
		assertFalse(dbCell.getLowerCase());
	}

	@Test
	public void docCell2DbCellPolymorphicLowerCase() {

		final DnaCell dbCell = new DnaCell();
		CreateOrUpdateDnaMatrix.docCell2DbCell(dbCell,
				PPodCellType.POLYMORPHIC,
				"gtc");

		assertEquals(dbCell.getType(), PPodCellType.POLYMORPHIC);
		assertTrue(dbCell.getLowerCase());
	}

	@Test
	public void docCell2DbCellUncertain() {

		final DnaCell dbCell = new DnaCell();
		CreateOrUpdateDnaMatrix.docCell2DbCell(dbCell,
				PPodCellType.UNCERTAIN,
				"ATC");

		assertEquals(dbCell.getType(), PPodCellType.UNCERTAIN);
	}

	@Test
	public void docCell2DbCellInapplicable() {

		final DnaCell dbCell = new DnaCell();
		CreateOrUpdateDnaMatrix.docCell2DbCell(dbCell,
				PPodCellType.INAPPLICABLE,
				"-");

		assertEquals(dbCell.getType(), PPodCellType.INAPPLICABLE);
	}

}
