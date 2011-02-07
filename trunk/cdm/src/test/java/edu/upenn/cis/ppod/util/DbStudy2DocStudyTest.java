package edu.upenn.cis.ppod.util;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.EnumSet;
import java.util.Set;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dto.PPodProtein;
import edu.upenn.cis.ppod.dto.PPodProteinRow;
import edu.upenn.cis.ppod.model.ProteinCell;

@Test(groups = TestGroupDefs.FAST)
public class DbStudy2DocStudyTest {
	@Test
	public void dbProteinCell2SequenceSingle() {
		final ProteinCell cell = new ProteinCell();
		cell.setSingle(PPodProtein.A);

		final StringBuilder sb = new StringBuilder();

		DbStudy2DocStudy.dbProteinCell2Sequence(cell, sb);

		assertEquals(sb.length(), 1);

		assertEquals(
				sb.charAt(0),
				getOnlyElement(cell.getElements()).toString().charAt(0));
	}

	@Test
	public void dbProteinCell2SequencePolymorphic() {
		final ProteinCell cell = new ProteinCell();

		final Set<PPodProtein> proteins = EnumSet.allOf(PPodProtein.class);

		cell.setPolymorphic(proteins);

		final StringBuilder sb = new StringBuilder();

		DbStudy2DocStudy.dbProteinCell2Sequence(cell, sb);

		assertEquals(sb.length(), proteins.size() + 2);

		assertEquals(sb.charAt(0), '(');
		assertEquals(sb.charAt(sb.length() - 1), ')');

		final String s = sb.toString();

		for (final Character legalChar : PPodProteinRow.LEGAL_CHARS) {
			if (legalChar != '?' && legalChar != '-'
					&& legalChar != '(' && legalChar != ')'
					&& legalChar != '{' && legalChar != '}') {
				assertTrue(s.indexOf(legalChar) != -1);
				assertTrue(s.indexOf(legalChar) != 0);
				assertTrue(s.indexOf(legalChar) != s.length() - 1);
			}
		}
	}

	@Test
	public void dbProteinCell2SequenceUncertain() {
		final ProteinCell cell = new ProteinCell();

		final Set<PPodProtein> proteins = EnumSet.allOf(PPodProtein.class);

		cell.setUncertain(proteins);

		final StringBuilder sb = new StringBuilder();

		DbStudy2DocStudy.dbProteinCell2Sequence(cell, sb);

		assertEquals(sb.length(), proteins.size() + 2);

		assertEquals(sb.charAt(0), '{');
		assertEquals(sb.charAt(sb.length() - 1), '}');

		final String s = sb.toString();

		for (final Character legalChar : PPodProteinRow.LEGAL_CHARS) {
			if (legalChar != '?' && legalChar != '-'
					&& legalChar != '(' && legalChar != ')'
					&& legalChar != '{' && legalChar != '}') {
				assertTrue(s.indexOf(legalChar) != -1);
				assertTrue(s.indexOf(legalChar) != 0);
				assertTrue(s.indexOf(legalChar) != s.length() - 1);
			}
		}
	}

	@Test
	public void dbProteinCell2SequenceInapplicable() {
		final ProteinCell cell = new ProteinCell();
		cell.setInapplicable();

		final StringBuilder sb = new StringBuilder();

		DbStudy2DocStudy.dbProteinCell2Sequence(cell, sb);

		assertEquals(sb.length(), 1);

		assertEquals(sb.charAt(0), '-');
	}

	@Test
	public void dbProteinCell2SequenceUnassigned() {
		final ProteinCell cell = new ProteinCell();
		cell.setUnassigned();

		final StringBuilder sb = new StringBuilder();

		DbStudy2DocStudy.dbProteinCell2Sequence(cell, sb);

		assertEquals(sb.length(), 1);

		assertEquals(sb.charAt(0), '?');
	}
}
