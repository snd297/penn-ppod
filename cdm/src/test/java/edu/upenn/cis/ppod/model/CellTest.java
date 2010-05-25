package edu.upenn.cis.ppod.model;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.EnumSet;
import java.util.Set;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

@Test(groups = TestGroupDefs.FAST)
public class CellTest {

	@Inject
	private Provider<DNACell> dnaCellProvider;

	@Test(expectedExceptions = IllegalStateException.class)
	public void getElementsWhenNoTypeSet() {
		final Cell<?> cell = dnaCellProvider.get();
		cell.getElements();
	}

	@Test
	public void getElementsXml() {
		final DNACell cell = dnaCellProvider.get();
		cell.beforeUnmarshal(null, null);
		final Set<DNANucleotide> cellElementsXml = cell.getElementsXml();
		assertNotNull(cellElementsXml);
		assertEquals(cellElementsXml.size(), 0);

		cell.afterUnmarshal();

		cell.setUnassigned();
		assertNull(cell.getElementsXml());

		cell.setSingleElement(DNANucleotide.A);
		assertNull(cell.getElementsXml());

		cell.setInapplicable();
		assertNull(cell.getElementsXml());

		final Set<DNANucleotide> nucleotides = EnumSet.of(DNANucleotide.A,
				DNANucleotide.G);
		cell.setPolymorphicElements(nucleotides);
		assertEquals((Object) cell.getElementsXml(), (Object) nucleotides);

		cell.setUncertainElements(nucleotides);
		assertEquals((Object) cell.getElementsXml(), (Object) nucleotides);

	}

	@Test
	public void afterUnmarshalUnassigned() {
		final DNACell cell = dnaCellProvider.get();
		cell.setUnassigned();
		cell.afterUnmarshal();
		assertFalse(cell.getBeingUnmarshalled());
		assertNull(cell.getElementsRaw());
	}

	@Test
	public void afterUnmarshalSingle() {
		final DNACell cell = dnaCellProvider.get();
		cell.setSingleElement(DNANucleotide.G);
		cell.afterUnmarshal();
		assertFalse(cell.getBeingUnmarshalled());
		assertNull(cell.getElementsRaw());
	}

	@Test
	public void afterUnmarshalPolymorphic() {
		final DNACell cell = dnaCellProvider.get();
		Set<DNANucleotide> nucleotides =
				EnumSet.of(DNANucleotide.G, DNANucleotide.T);
		cell.setPolymorphicElements(nucleotides);
		cell.afterUnmarshal();
		assertFalse(cell.getBeingUnmarshalled());
		assertEquals((Object) cell.getElementsRaw(),
				(Object) nucleotides);
	}

	@Test
	public void afterUnmarshalUncertain() {
		final DNACell cell = dnaCellProvider.get();
		Set<DNANucleotide> nucleotides =
				EnumSet.of(DNANucleotide.G, DNANucleotide.T);
		cell.setUncertainElements(nucleotides);
		cell.afterUnmarshal();
		assertFalse(cell.getBeingUnmarshalled());
		assertEquals((Object) cell.getElementsRaw(),
				(Object) nucleotides);
	}

	@Test
	public void afterUnmarshalInapplicable() {
		final DNACell cell = dnaCellProvider.get();
		cell.setInapplicable();
		cell.afterUnmarshal();
		assertFalse(cell.getBeingUnmarshalled());
		assertNull(cell.getElementsRaw());
	}
}
