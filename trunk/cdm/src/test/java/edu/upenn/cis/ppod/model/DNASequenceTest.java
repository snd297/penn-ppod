/*
 * Copyright (C) 2010 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * {@code DNASequenceSet} tests.
 * 
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST)
public class DNASequenceTest {

	private final List<java.lang.Character> alpabet = ImmutableList
			.of(
					'A', 'a',
					'B', 'b',
					'C', 'c',
					'D', 'd',
					'E', 'e',
					'F', 'f',
					'G', 'g',
					'H', 'h',
					'I', 'i',
					'J', 'j',
					'K', 'k',
					'L', 'l',
					'M', 'm',
					'N', 'n',
					'O', 'o',
					'P', 'p',
					'Q', 'q',
					'R', 'r',
					'S', 's',
					'T', 't',
					'U', 'u',
					'V', 'v',
					'W', 'w',
					'X', 'x',
					'Y', 'y',
					'Z', 'z'
					);

	/**
	 * All of these characters are legal, upper or lower case.
	 */
	private final List<java.lang.Character> legalDNACharacters = ImmutableList
			.of(
					'A', 'a',
					'C', 'c',
					'G', 'g',
					'T', 't',
					'R',
					'Y',
					'K',
					'M',
					'S',
					'W',
					'B',
					'D',
					'H',
					'V',
					'N',
					'-'
					);

	/**
	 * Make sure that all legal character return true for {@code isLegal(...)}.
	 */
	@Test
	public void testIsLegal() {
		final Sequence<?> sequence = new DNASequence();

		for (final java.lang.Character character : legalDNACharacters) {
			assertTrue(sequence.isLegal(character));
		}
	}

	/**
	 * Make sure that all illegal character return false for
	 * {@code isLegal(...)}.
	 */
	@Test
	public void testIsLegalShouldReturnFalse() {
		final Sequence<?> sequence = new DNASequence();

		for (final java.lang.Character illegalCharacter : alpabet) {
			if (legalDNACharacters.contains(illegalCharacter)) {
				// it's legal don't see if it's false
			} else {
				assertFalse(sequence.isLegal(illegalCharacter),
						"illegalChar: " + illegalCharacter);
			}
		}
	}
}
