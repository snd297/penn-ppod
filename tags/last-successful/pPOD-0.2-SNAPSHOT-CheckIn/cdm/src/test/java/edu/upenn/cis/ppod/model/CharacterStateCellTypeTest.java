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
				.of("SINGLE");
		assertEquals(single, CharacterStateCell.Type.SINGLE);

		final CharacterStateCell.Type unassigned = CharacterStateCell.Type
				.of("UNASSIGNED");
		assertEquals(unassigned, CharacterStateCell.Type.UNASSIGNED);

		final CharacterStateCell.Type inapplicable = CharacterStateCell.Type
				.of("INAPPLICABLE");
		assertEquals(inapplicable, CharacterStateCell.Type.INAPPLICABLE);

		final CharacterStateCell.Type uncertain = CharacterStateCell.Type
				.of("UNCERTAIN");
		assertEquals(uncertain, CharacterStateCell.Type.UNCERTAIN);

		final CharacterStateCell.Type polymorphic = CharacterStateCell.Type
				.of("POLYMORPHIC");
		assertEquals(polymorphic, CharacterStateCell.Type.POLYMORPHIC);

	}
}
