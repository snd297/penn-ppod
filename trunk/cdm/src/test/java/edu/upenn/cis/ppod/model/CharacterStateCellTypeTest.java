package edu.upenn.cis.ppod.model;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST)
public class CharacterStateCellTypeTest {
	public void of() {
		final CharacterStateCell.Type single = CharacterStateCell.Type
				.valueOf("SINGLE");
		assertEquals(single, CharacterStateCell.Type.SINGLE);

		final CharacterStateCell.Type unassigned = CharacterStateCell.Type
				.valueOf("UNASSIGNED");
		assertEquals(unassigned, CharacterStateCell.Type.UNASSIGNED);

		final CharacterStateCell.Type inapplicable = CharacterStateCell.Type
				.valueOf("INAPPLICABLE");
		assertEquals(inapplicable, CharacterStateCell.Type.INAPPLICABLE);

		final CharacterStateCell.Type uncertain = CharacterStateCell.Type
				.valueOf("UNCERTAIN");
		assertEquals(uncertain, CharacterStateCell.Type.UNCERTAIN);

		final CharacterStateCell.Type polymorphic = CharacterStateCell.Type
				.valueOf("POLYMORPHIC");
		assertEquals(polymorphic, CharacterStateCell.Type.POLYMORPHIC);

	}
}
