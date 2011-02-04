package edu.upenn.cis.ppod.model;

import static org.testng.Assert.assertEquals;

import java.util.EnumSet;
import java.util.Set;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dto.PPodCellType;
import edu.upenn.cis.ppod.dto.PPodProtein;

@Test(groups = TestGroupDefs.FAST)
public class ProteinCellTest {
	@Test
	public void setPolymorphic() {
		final ProteinCell cell = new ProteinCell();
		final Set<PPodProtein> proteins = EnumSet.allOf(PPodProtein.class);
		cell.setPolymorphic(proteins);
		assertEquals(cell.getElements().size(), proteins.size());
		assertEquals(cell.getType(), PPodCellType.POLYMORPHIC);
	}
}
