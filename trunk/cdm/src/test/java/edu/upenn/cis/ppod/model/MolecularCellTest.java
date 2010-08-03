package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * {@link MolecularCell} testing.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST, TestGroupDefs.SINGLE })
public class MolecularCellTest {

	public void setPolymorphicElements() {
		final MolecularCell<DNANucleotide, ?> cell = new DNACell();

		cell.unsetInNeedOfNewVersion();

		final Set<DNANucleotide> nucleotides =
				ImmutableSet.of(DNANucleotide.A, DNANucleotide.T);

		final MolecularCell<?, ?> returnedCell =
				cell.setPolymorphicElements(
						nucleotides,
						false);
		assertTrue(cell.isInNeedOfNewVersion());
		assertSame(returnedCell, cell);
		assertEquals(cell.getElements(), nucleotides);
		assertFalse(cell.isLowerCase());

		cell.unsetInNeedOfNewVersion();

		final MolecularCell<?, ?> returnedCell1 =
				cell.setPolymorphicElements(
						nucleotides,
						false);
		assertFalse(cell.isInNeedOfNewVersion());
		assertSame(returnedCell1, cell);
		assertEquals(cell.getElements(), nucleotides);
		assertFalse(cell.isLowerCase());

		cell.unsetInNeedOfNewVersion();

		final Set<DNANucleotide> nucleotides2 =
				ImmutableSet.of(DNANucleotide.T, DNANucleotide.G);

		final MolecularCell<?, ?> returnedCell2 =
				cell.setPolymorphicElements(
						nucleotides2,
						false);
		assertTrue(cell.isInNeedOfNewVersion());
		assertSame(returnedCell2, cell);
		assertEquals(cell.getElements(), nucleotides2);
		assertFalse(cell.isLowerCase());

		cell.unsetInNeedOfNewVersion();

		final MolecularCell<?, ?> returnedCell3 =
				cell.setPolymorphicElements(
						nucleotides2,
						true);
		assertTrue(cell.isInNeedOfNewVersion());
		assertSame(returnedCell3, cell);
		assertEquals(cell.getElements(), nucleotides2);
		assertTrue(cell.isLowerCase());
	}

	@Test
	public void setSingleElement() {
		final MolecularCell<DNANucleotide, ?> cell = new DNACell();

		cell.unsetInNeedOfNewVersion();
		final MolecularCell<?, ?> returnedCell =
				cell.setSingleElement(DNANucleotide.C, false);
		assertTrue(cell.isInNeedOfNewVersion());
		assertSame(returnedCell, cell);
		assertSame(getOnlyElement(cell.getElements()), DNANucleotide.C);

		cell.unsetInNeedOfNewVersion();
		final MolecularCell<?, ?> returnedCell1 =
				cell.setSingleElement(DNANucleotide.C, false);
		assertFalse(cell.isInNeedOfNewVersion());
		assertSame(returnedCell1, cell);
		assertSame(getOnlyElement(cell.getElements()), DNANucleotide.C);

		cell.unsetInNeedOfNewVersion();
		final MolecularCell<?, ?> returnedCell2 =
				cell.setSingleElement(DNANucleotide.C, true);
		assertTrue(cell.isInNeedOfNewVersion());
		assertSame(returnedCell2, cell);
		assertSame(getOnlyElement(cell.getElements()), DNANucleotide.C);

		cell.unsetInNeedOfNewVersion();
		final MolecularCell<?, ?> returnedCell3 =
				cell.setSingleElement(DNANucleotide.A, true);
		assertTrue(cell.isInNeedOfNewVersion());
		assertSame(returnedCell3, cell);
		assertSame(getOnlyElement(cell.getElements()), DNANucleotide.A);
	}
}
