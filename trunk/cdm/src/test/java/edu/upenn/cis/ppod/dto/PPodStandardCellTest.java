package edu.upenn.cis.ppod.dto;

import static com.google.common.collect.Sets.newHashSet;
import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

import edu.upenn.cis.ppod.PPodCellType;
import edu.upenn.cis.ppod.PPodStandardCell;
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

	@Test
	public void setSingle() {
		final PPodStandardCell cell = new PPodStandardCell();

		final Integer stateNumber = Integer.valueOf(2);

		cell.setSingle(stateNumber);
		assertEquals(cell.getType(), PPodCellType.SINGLE);
		assertEquals(cell.getStates(), newHashSet(stateNumber));
	}

	@Test
	public void setInapplicable() {
		final PPodStandardCell cell = new PPodStandardCell();
		cell.setInapplicable();
		assertEquals(cell.getType(), PPodCellType.INAPPLICABLE);
		assertEquals(cell.getStates(), PPodStandardCell.EMPTY_STATES);
	}

	@Test
	public void setUnassigned() {
		final PPodStandardCell cell = new PPodStandardCell();
		cell.setUnassigned();
		assertEquals(cell.getType(), PPodCellType.UNASSIGNED);
		assertEquals(cell.getStates(), PPodStandardCell.EMPTY_STATES);
	}

	@Test
	public void constructorPolymorphic() {
		final Set<Integer> states = ImmutableSet.of(0, 1, 3);

		final PPodStandardCell cell = new PPodStandardCell(
				PPodCellType.POLYMORPHIC, states);

		assertEquals(cell.getType(), PPodCellType.POLYMORPHIC);
		assertEquals(cell.getStates(), states);
	}

	@Test
	public void constructorUncertain() {
		final Set<Integer> states = ImmutableSet.of(0, 1, 3);

		final PPodStandardCell cell = new PPodStandardCell(
				PPodCellType.UNCERTAIN, states);

		assertEquals(cell.getType(), PPodCellType.UNCERTAIN);
		assertEquals(cell.getStates(), states);
	}

	@Test
	public void constructorSingle() {
		final Set<Integer> states = ImmutableSet.of(0);

		final PPodStandardCell cell = new PPodStandardCell(
				PPodCellType.SINGLE, states);

		assertEquals(cell.getType(), PPodCellType.SINGLE);
		assertEquals(cell.getStates(), states);
	}

	@Test
	public void constructorInapplicable() {

		final PPodStandardCell cell = new PPodStandardCell(
				PPodCellType.INAPPLICABLE, PPodStandardCell.EMPTY_STATES);

		assertEquals(cell.getType(), PPodCellType.INAPPLICABLE);
		assertEquals(cell.getStates(), PPodStandardCell.EMPTY_STATES);
	}

	@Test
	public void constructorUnassigned() {

		final PPodStandardCell cell = new PPodStandardCell(
				PPodCellType.UNASSIGNED, PPodStandardCell.EMPTY_STATES);

		assertEquals(cell.getType(), PPodCellType.UNASSIGNED);
		assertEquals(cell.getStates(), PPodStandardCell.EMPTY_STATES);
	}

}
