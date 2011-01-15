package edu.upenn.cis.ppod.dto;

import static com.google.common.collect.Sets.newHashSet;
import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

import edu.upenn.cis.ppod.TestGroupDefs;

@Test(groups = TestGroupDefs.FAST)
public class PPodStandardCellTest {
	@Test
	public void setPolymorphic() {
		final PPodStandardCell cell = new PPodStandardCell();

		final Set<Integer> states = ImmutableSet.of(0, 1, 3);

		cell.setPolymorphic(newHashSet(states));
		assertEquals(cell.getType(), PPodCellType.POLYMORPHIC);
		assertEquals(cell.getStates(), states);
	}

	@Test
	public void setUncertain() {
		final PPodStandardCell cell = new PPodStandardCell();

		final Set<Integer> states = ImmutableSet.of(0, 1, 3);

		cell.setUncertain(newHashSet(states));
		assertEquals(cell.getType(), PPodCellType.UNCERTAIN);
		assertEquals(cell.getStates(), states);
	}

}
