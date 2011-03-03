package edu.upenn.cis.ppod.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dto.PPodCellType;

@Test(groups = TestGroupDefs.FAST)
public class PPodSequenceTokenizerTest {

	@Test
	public void test() {
		final PPodSequenceTokenizer seqTokenizer = new PPodSequenceTokenizer(
				"AC{TY}W(FJ)");
		final PPodSequenceTokenizer.Token firstToken = seqTokenizer.nextToken();
		assertEquals(firstToken.cellType, PPodCellType.SINGLE);
		assertEquals(firstToken.sequence, "A");
		assertTrue(seqTokenizer.hasMoreTokens());

		final PPodSequenceTokenizer.Token secondToken = seqTokenizer
				.nextToken();
		assertEquals(secondToken.cellType, PPodCellType.SINGLE);
		assertEquals(secondToken.sequence, "C");
		assertTrue(seqTokenizer.hasMoreTokens());

		final PPodSequenceTokenizer.Token thirdToken = seqTokenizer.nextToken();
		assertEquals(thirdToken.cellType, PPodCellType.UNCERTAIN);
		assertEquals(thirdToken.sequence, "TY");
		assertTrue(seqTokenizer.hasMoreTokens());

		final PPodSequenceTokenizer.Token fourthToken = seqTokenizer
				.nextToken();
		assertEquals(fourthToken.cellType, PPodCellType.SINGLE);
		assertEquals(fourthToken.sequence, "W");
		assertTrue(seqTokenizer.hasMoreTokens());

		final PPodSequenceTokenizer.Token fifthToken = seqTokenizer
				.nextToken();
		assertEquals(fifthToken.cellType, PPodCellType.POLYMORPHIC);
		assertEquals(fifthToken.sequence, "FJ");

		assertFalse(seqTokenizer.hasMoreTokens());

	}

	@Test(expectedExceptions = NoSuchElementException.class)
	public void nextTokenNoMoreTokens() {
		final PPodSequenceTokenizer seqTokenizer = new PPodSequenceTokenizer(
				"AC{TY}W(FJ)");
		seqTokenizer.nextToken();
		seqTokenizer.nextToken();
		seqTokenizer.nextToken();
		seqTokenizer.nextToken();
		seqTokenizer.nextToken();

		// should throw
		seqTokenizer.nextToken();
	}
}
