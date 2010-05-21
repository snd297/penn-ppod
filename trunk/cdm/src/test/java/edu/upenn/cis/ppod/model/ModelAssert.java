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

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.newHashSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * For asserting that various {@code edu.upenn.cis.ppod.model} elements are the
 * equal.
 * 
 * @author Sam Donnelly
 */
public class ModelAssert {

	public static void assertEqualsOTUSet(final OTUSet actualOTUSet,
			final OTUSet expectedOTUSet) {
		assertEquals(actualOTUSet.getLabel(), expectedOTUSet.getLabel());
		if (expectedOTUSet.getPPodId() != null) {
			assertEquals(actualOTUSet.getPPodId(), expectedOTUSet.getPPodId());
		}
		assertEquals(actualOTUSet.getOTUs().size(), expectedOTUSet
				.getOTUs().size());
		for (final OTU expectedOTU : expectedOTUSet.getOTUs()) {
			boolean foundIt = false;
			for (final OTU actualOTU : actualOTUSet.getOTUs()) {
				if (equal(actualOTU.getLabel(), expectedOTU.getLabel())) {
					foundIt = true;
					break;
				}
			}
			assertTrue(foundIt, "couldn't find an expected OTU ["
								+ expectedOTU.getLabel()
								+ "] in the actual OTU's: "
								+ actualOTUSet);
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
		assertEqualsPPodEntities(actualCharacter, expectedCharacter);
		assertEquals(actualCharacter.getLabel(), expectedCharacter.getLabel());
		assertEquals(actualCharacter.getStatesModifiable().size(),
				expectedCharacter
						.getStatesModifiable().size());
		for (final Entry<Integer, CharacterState> actualStateNumberToState : actualCharacter
				.getStatesModifiable().entrySet()) {
			final CharacterState actualState = actualStateNumberToState
					.getValue();
			final CharacterState expectedState = expectedCharacter
					.getStatesModifiable()
					.get(actualStateNumberToState.getKey());

			assertNotNull(expectedState);
			assertTrue(expectedState.getCharacter() == expectedCharacter);
			assertEqualsCharacterStates(actualState,
					expectedState);
		}

	}

	public static void assertEqualsCharacterStateCells(
			final CharacterStateCell actualCell,
			final CharacterStateCell expectedCell) {
		assertEquals(actualCell.getElements().size(), expectedCell
				.getElements().size());
		for (final Iterator<CharacterState> actualStateItr = actualCell
				.getElements().iterator(), expectedStateItr = expectedCell
				.getElements().iterator(); actualStateItr.hasNext()
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
				.iterator(), expectedCellItr = expectedRow.iterator(); actualCellItr
				.hasNext()
																		&& expectedCellItr
																				.hasNext();) {
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

		assertEquals(actualMatrix.getCharactersModifiable().size(),
				actualMatrix
						.getCharactersModifiable().size());
		for (final Iterator<Character> actualCharacterItr = actualMatrix
				.getCharactersModifiable().iterator(), expectedCharacterItr = expectedMatrix
				.getCharactersModifiable().iterator(); actualCharacterItr
				.hasNext()
												&& expectedCharacterItr
														.hasNext();) {
			final Character actualCharacter = actualCharacterItr.next();
			final Character expectedCharacter = expectedCharacterItr.next();
			assertSame(actualCharacter.getMatrix(), actualMatrix);
			assertSame(expectedCharacter.getMatrix(), expectedMatrix);
			assertEqualsCharacters(actualCharacter, expectedCharacter);
		}

		// Let's make sure that actualMatrix.getCharacterIdx() is what it's
		// supposed to be.
		// We use actualMatrix.getCharacters() to check as oppose to looking at
		// expectedMatrix sine that seems to make the most sense
		if (actualMatrix.getClass().equals(CharacterStateMatrix.class)) {
			final Map<Character, Integer> actualMatrixCharactersToPositions = actualMatrix
					.getCharactersToPositions();
			assertEquals(
					actualMatrixCharactersToPositions.size(),
					actualMatrix.getColumnVersionInfos().size());
			for (final Entry<Character, Integer> actualIdxByCharacter : actualMatrixCharactersToPositions
					.entrySet()) {
				assertTrue(actualIdxByCharacter.getKey() == actualMatrix
						.getCharactersModifiable().get(
								actualIdxByCharacter.getValue()));
			}
		}

		assertEquals(actualMatrix.getRows().size(), expectedMatrix
				.getRows().size());

		for (final Iterator<OTU> actualOTUIterator = actualMatrix.getOTUSet()
				.getOTUs()
				.iterator(), expectedOTUIterator = expectedMatrix.getOTUSet()
				.getOTUs()
				.iterator(); actualOTUIterator.hasNext()
								&& expectedOTUIterator.hasNext();) {
			final CharacterStateRow actualRow = actualMatrix
					.getRow(actualOTUIterator.next()), expectedRow = expectedMatrix
					.getRow(expectedOTUIterator.next());
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

	public static void assertEqualsPPodEntities(
			final PPodEntity actualPPodEntity,
			final PPodEntity expectedPPodEntity) {
		assertEqualsAttachmentSets(
				actualPPodEntity.getAttachments(),
				expectedPPodEntity.getAttachments());
	}

	public static void assertEqualsAttachmentSets(
			final Set<Attachment> actualAttachments,
			final Set<Attachment> expectedAttachments) {
		assertEquals(actualAttachments.size(), expectedAttachments.size());
		final Set<Attachment> expectedAttachmentsCopy = newHashSet(expectedAttachments);
		for (final Attachment actualAttachment : actualAttachments) {
			final Attachment expectedAttachment = getOnlyElement(filter(
							expectedAttachmentsCopy,
							new Attachment.IsOfNamespaceTypeLabelAndStringValue(
									actualAttachment)));
			expectedAttachmentsCopy.remove(expectedAttachment);
			assertEqualsAttachments(actualAttachment, expectedAttachment);
		}
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
		if (expectedAttachment.getAttachments().size() > 0) {
			throw new IllegalArgumentException(
					"We don't support nested attchaments yet");
		}
	}

	/**
	 * Prevent inheritance and instantiation.
	 */
	private ModelAssert() {
		throw new AssertionError("can't instantiate a ModelAssert");
	}
}
