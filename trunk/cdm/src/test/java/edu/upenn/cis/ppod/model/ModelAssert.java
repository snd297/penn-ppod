/*
 * Copyright (C) 2009 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model;

import static edu.upenn.cis.ppod.util.UPennCisPPodUtil.nullSafeEquals;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @author Sam Donnelly
 */
public class ModelAssert {

	public static void assertEqualsOTUSet(final OTUSet actualOTUSet,
			final OTUSet expectedOTUSet) {
		assertEquals(actualOTUSet.getLabel(), expectedOTUSet.getLabel());

		assertEquals(actualOTUSet.getOTUs().size(), expectedOTUSet.getOTUs()
				.size());
		for (final OTU expectedOTU : expectedOTUSet.getOTUs()) {
			boolean foundIt = false;
			for (final OTU actualOTU : actualOTUSet.getOTUs()) {
				if (nullSafeEquals(actualOTU.getLabel(), expectedOTU.getLabel())) {
					foundIt = true;
					break;
				}
			}
			assertTrue(foundIt, "couldn't find an expected OTU ["
					+ expectedOTU.getLabel() + "] in the actual OTU's: "
					+ actualOTUSet.getOTUs());
		}
	}

	public static void assertEqualsOTUs(final OTU actualOTU,
			final OTU expectedOTU) {
		assertEquals(actualOTU.getLabel(), expectedOTU.getLabel());
	}

	public static void assertEqualsCharacterStates(
			final CharacterState actualState, final CharacterState expectedState) {
		assertEquals(actualState.getLabel(), expectedState.getLabel());
		assertEquals(actualState.getStateNumber(), expectedState
				.getStateNumber());
	}

	public static void assertEqualsCharacters(final Character actualCharacter,
			final Character expectedCharacter) {
		assertEquals(actualCharacter.getLabel(), expectedCharacter.getLabel());
		assertEquals(actualCharacter.getStates().size(), expectedCharacter
				.getStates().size());
		for (final Entry<Integer, CharacterState> actualStateNumberToState : actualCharacter
				.getStates().entrySet()) {
			final CharacterState actualState = actualStateNumberToState
					.getValue();
			final CharacterState expectedState = expectedCharacter.getStates()
					.get(actualStateNumberToState.getKey());
			assertNotNull(expectedState);
			assertTrue(expectedState.getCharacter() == expectedCharacter);
			assertEqualsCharacterStates(actualStateNumberToState.getValue(),
					expectedState);
		}
	}

	public static void assertEqualsCharacterStateCells(
			final CharacterStateCell actualCell,
			final CharacterStateCell expectedCell) {
		assertEquals(actualCell.getStates().size(), expectedCell.getStates()
				.size());
		for (final Iterator<CharacterState> actualStateItr = actualCell
				.getStates().iterator(), expectedStateItr = expectedCell
				.getStates().iterator(); actualStateItr.hasNext()
				&& expectedStateItr.hasNext();) {
			assertEqualsCharacterStates(actualStateItr.next(), expectedStateItr
					.next());
		}
	}

	public static void assertEqualsCharacterStateRows(
			final CharacterStateRow actualRow,
			final CharacterStateRow expectedRow) {
		assertEquals(actualRow.getCells().size(), expectedRow.getCells().size());
		for (final Iterator<CharacterStateCell> actualCellItr = actualRow
				.getCells().iterator(), expectedCellItr = expectedRow
				.getCells().iterator(); actualCellItr.hasNext()
				&& expectedCellItr.hasNext();) {
			final CharacterStateCell actualCell = actualCellItr.next(), expectedCell = expectedCellItr
					.next();
			assertTrue(actualCell.getRow() == actualRow);
			assertEqualsCharacterStateCells(actualCell, expectedCell);
		}
	}

	/**
	 * pPOD ID's are not checked.
	 * 
	 * @param actualMatrix
	 * @param expectedMatrix
	 */
	public static void assertEqualsCharacterStateMatrices(
			final CharacterStateMatrix actualMatrix,
			final CharacterStateMatrix expectedMatrix) {
		assertEquals(actualMatrix.getLabel(), expectedMatrix.getLabel());
		assertEquals(actualMatrix.getDescription(), expectedMatrix
				.getDescription());

		assertEqualsOTUSet(actualMatrix.getOTUSet(), expectedMatrix.getOTUSet());
		assertEquals(actualMatrix.getOTUs().size(), expectedMatrix.getOTUs()
				.size());

		// assertEqualsOTUSet verifies that both OTUSet's contain the same
		// OTU's. Now we confirm
		// that the matrix's OTU's are in the correct order
		for (final Iterator<OTU> actualOTUItr = actualMatrix.getOTUs()
				.iterator(), expectedOTUItr = expectedMatrix.getOTUs()
				.iterator(); actualOTUItr.hasNext() && expectedOTUItr.hasNext();) {
			assertEqualsOTUs(actualOTUItr.next(), expectedOTUItr.next());
		}

		// Let's make sure that actualMatrix.getOTUIdx() is what it's
		// supposed to be.
		// We use actualMatrix.getOTUs() to check as opposed to looking at
		// expectedMatrix sine that seems to make the most sense
		assertEquals(actualMatrix.getOTUIdx().size(), actualMatrix.getOTUs()
				.size());
		for (final Entry<OTU, Integer> actualIdxByOTU : actualMatrix
				.getOTUIdx().entrySet()) {
			assertTrue(actualIdxByOTU.getKey() == actualMatrix.getOTUs().get(
					actualIdxByOTU.getValue()));
		}
		assertEquals(actualMatrix.getCharacters().size(), actualMatrix
				.getCharacters().size());
		for (final Iterator<Character> actualCharacterItr = actualMatrix
				.getCharacters().iterator(), expectedCharacterItr = expectedMatrix
				.getCharacters().iterator(); actualCharacterItr.hasNext()
				&& expectedCharacterItr.hasNext();) {
			final Character actualCharacter = actualCharacterItr.next();
			final Character expectedCharacter = expectedCharacterItr.next();
			assertTrue(actualCharacter.getMatrices().contains(actualMatrix));
			assertTrue(expectedCharacter.getMatrices().contains(expectedMatrix));
			assertEqualsCharacters(actualCharacter, expectedCharacter);
		}

		// Let's make sure that actualMatrix.getCharacterIdx() is what it's
		// supposed to be.
		// We use actualMatrix.getCharacters() to check as oppose to looking at
		// expectedMatrix sine that seems to make the most sense
		assertEquals(actualMatrix.getCharacterIdx().size(), actualMatrix
				.getCharacters().size());

		for (final Entry<Character, Integer> actualIdxByCharacter : actualMatrix
				.getCharacterIdx().entrySet()) {
			assertTrue(actualIdxByCharacter.getKey() == actualMatrix
					.getCharacters().get(actualIdxByCharacter.getValue()));
		}

		assertEquals(actualMatrix.getRows().size(), expectedMatrix.getRows()
				.size());

		for (final Iterator<CharacterStateRow> actualRowItr = actualMatrix
				.getRows().iterator(), expectedRowItr = expectedMatrix
				.getRows().iterator(); actualRowItr.hasNext()
				&& expectedRowItr.hasNext();) {
			final CharacterStateRow actualRow = actualRowItr.next(), expectedRow = expectedRowItr
					.next();
			assertTrue(actualRow.getMatrix() == actualMatrix);
			assertEqualsCharacterStateRows(actualRow, expectedRow);
		}
	}

	public static void assertEqualsAttachmentNamespaces(
			final AttachmentNamespace actualAttachmentNamespace,
			final AttachmentNamespace expectedAttachmentNamespace) {
		assertEquals(actualAttachmentNamespace.getLabel(),
				expectedAttachmentNamespace.getLabel());
	}

	public static void assertEqualsAttachmentTypes(
			final AttachmentType actualAttachmentType,
			final AttachmentType expectedAttachmentType) {
		assertEquals(actualAttachmentType.getLabel(), expectedAttachmentType
				.getLabel());
		assertEqualsAttachmentNamespaces(actualAttachmentType.getNamespace(),
				expectedAttachmentType.getNamespace());
	}

	public static void assertEqualsAttachments(
			final Attachment actualAttachment,
			final Attachment expectedAttachment) {
		assertEquals(actualAttachment.getLabel(), expectedAttachment.getLabel());
		assertEquals(actualAttachment.getStringValue(), expectedAttachment
				.getStringValue());
		assertEquals(actualAttachment.getBytesValue(), expectedAttachment
				.getBytesValue());
		assertEqualsAttachmentTypes(actualAttachment.getType(),
				expectedAttachment.getType());
		assertEquals(actualAttachment.getAttachments().size(),
				expectedAttachment.getAttachments().size());
		for (final Attachment attachmentAttachment : expectedAttachment
				.getAttachments()) {
			throw new IllegalArgumentException("We don't support nested attchaments yet");
		}
	}

	/**
	 * Prevent inheritance and instantiation.
	 */
	private ModelAssert() {
		throw new AssertionError("can't instantiate a ModelAssert");
	}
}
