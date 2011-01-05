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

import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.newHashSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;
import java.util.Set;

import edu.upenn.cis.ppod.imodel.IAttachment;
import edu.upenn.cis.ppod.imodel.IAttachmentNamespace;
import edu.upenn.cis.ppod.imodel.IAttachmentType;
import edu.upenn.cis.ppod.imodel.ILabeled;
import edu.upenn.cis.ppod.imodel.IPPodEntity;

/**
 * For asserting that various {@code edu.upenn.cis.ppod.model} elements are the
 * equal.
 * 
 * @author Sam Donnelly
 */
public class ModelAssert {

	public static <S extends Sequence<?>> void assertEqualsSequenceSets(
			final SequenceSet<S> actualSeqSet,
			final SequenceSet<S> expectedSeqSet) {
		assertEquals(actualSeqSet.getLabel(), actualSeqSet.getLabel());
		assertEquals(
				actualSeqSet.getSequences().size(),
				expectedSeqSet.getSequences().size());

		assertEquals(
				actualSeqSet.getParent().getOtus().size(),
				expectedSeqSet.getParent().getOtus().size());

		for (int otuPos = 0; otuPos < actualSeqSet.getParent().getOtus().size(); otuPos++) {
			assertEqualsSequences(
					actualSeqSet
							.getSequence(actualSeqSet
									.getParent()
									.getOtus()
									.get(otuPos)),
									expectedSeqSet
											.getSequence(expectedSeqSet
													.getParent()
													.getOtus()
													.get(otuPos)));
		}
	}

	public static void assertEqualsSequences(
			final Sequence<?> actualSeq,
			final Sequence<?> expectedSeq) {
		assertEquals(actualSeq.getSequence(), expectedSeq.getSequence());
		assertEquals(actualSeq.getAccession(), expectedSeq.getAccession());
		assertEquals(actualSeq.getDescription(), expectedSeq.getDescription());
		assertEquals(actualSeq.getName(), expectedSeq.getName());
	}

	public static void assertEqualsOTUSet(
			final OtuSet actualOTUSet,
			final OtuSet expectedOTUSet) {
		assertEquals(actualOTUSet.getLabel(), expectedOTUSet.getLabel());
		if (expectedOTUSet.getPPodId() != null) {
			assertEquals(actualOTUSet.getPPodId(), expectedOTUSet.getPPodId());
		}
		assertEquals(actualOTUSet.getOtus().size(), expectedOTUSet
				.getOtus().size());
		for (final Otu expectedOTU : expectedOTUSet.getOtus()) {
			final Otu foundOTU =
					find(
							actualOTUSet.getOtus(),
							compose(
									equalTo(
									expectedOTU.getLabel()),
									ILabeled.getLabel),
									null);

			assertNotNull(foundOTU, "couldn't find an expected OTU ["
								+ expectedOTU.getLabel()
								+ "] in the actual OTU's: "
								+ actualOTUSet);
		}
	}

	public static void assertEqualsOTUs(
			final Otu actualOTU,
			final Otu expectedOTU) {
		assertEquals(actualOTU.getLabel(), expectedOTU.getLabel());
	}

	public static void assertEqualsStandardStates(
			final StandardState actualState, final StandardState expectedState) {
		assertEquals(actualState.getLabel(), expectedState.getLabel());
		assertEquals(actualState.getStateNumber(),
				expectedState.getStateNumber());
	}

	public static void assertEqualsCharacters(
			final StandardCharacter actualCharacter,
			final StandardCharacter expectedCharacter) {
		assertEqualsPPodEntities(actualCharacter, expectedCharacter);
		assertEquals(actualCharacter.getLabel(), expectedCharacter.getLabel());
		assertEquals(actualCharacter.getStates().size(),
				expectedCharacter
						.getStates().size());
		for (final StandardState actualState : actualCharacter
				.getStates()) {

			final StandardState expectedState =
					find(
							expectedCharacter.getStates(),
							compose(
									equalTo(actualState.getStateNumber()),
									StandardState.getStateNumber),
									null);

			assertNotNull(expectedState);
			assertTrue(expectedState.getParent() == expectedCharacter);
			assertEqualsStandardStates(
					actualState,
					expectedState);
		}

	}

	public static void assertEqualsStandardCells(
			final StandardCell actualCell,
			final StandardCell expectedCell) {
		assertEquals(actualCell.getType(), expectedCell.getType());
		assertEquals(actualCell.getElements().size(), expectedCell
				.getElements().size());
		for (final StandardState actualState : actualCell.getElements()) {
			final StandardState expectedState =
					find(expectedCell.getElements(),
							compose(
									equalTo(
										actualState.getStateNumber()),
									StandardState.getStateNumber),
									null);
			assertEqualsStandardStates(
					actualState,
					expectedState);
		}
	}

	public static void assertEqualsStandardRows(
			final StandardRow actualRow,
			final StandardRow expectedRow) {
		assertEquals(actualRow.getCells().size(), expectedRow.getCells().size());

		for (final Iterator<StandardCell> actualCellItr = actualRow
				.getCells()
				.iterator(), expectedCellItr = expectedRow.getCells()
				.iterator(); actualCellItr
				.hasNext()
																		&& expectedCellItr
																				.hasNext();) {
			final StandardCell actualCell = actualCellItr.next(), expectedCell = expectedCellItr
					.next();
			assertTrue(actualCell.getParent() == actualRow);
			assertEqualsStandardCells(actualCell, expectedCell);
		}
	}

	/**
	 * pPOD ID's are not checked.
	 * 
	 * @param actualMatrix
	 * @param expectedMatrix
	 */
	public static void assertEqualsStandardMatrices(
			final StandardMatrix actualMatrix,
			final StandardMatrix expectedMatrix) {
		assertEquals(actualMatrix.getLabel(), expectedMatrix.getLabel());
		assertEquals(
				actualMatrix.getDescription(),
				expectedMatrix.getDescription());

		assertEqualsOTUSet(actualMatrix.getParent(), expectedMatrix.getParent());

		assertEquals(
				actualMatrix.getCharacters().size(),
				actualMatrix.getCharacters().size());
		for (final Iterator<StandardCharacter> actualCharacterItr = actualMatrix
				.getCharacters().iterator(), expectedCharacterItr = expectedMatrix
				.getCharacters().iterator(); actualCharacterItr
				.hasNext()
												&& expectedCharacterItr
														.hasNext();) {
			final StandardCharacter actualCharacter = actualCharacterItr
					.next();
			final StandardCharacter expectedCharacter =
					expectedCharacterItr.next();
			assertSame(actualCharacter.getParent(), actualMatrix);
			assertSame(expectedCharacter.getParent(), expectedMatrix);
			assertEqualsCharacters(actualCharacter, expectedCharacter);
		}
		assertEquals(actualMatrix.getRows().size(),
				expectedMatrix.getRows().size());

		for (final Iterator<Otu> actualOTUIterator = actualMatrix.getParent()
				.getOtus()
				.iterator(), expectedOTUIterator = expectedMatrix.getParent()
				.getOtus()
				.iterator(); actualOTUIterator.hasNext()
								&& expectedOTUIterator.hasNext();) {
			final StandardRow actualRow = actualMatrix
					.getRows().get(actualOTUIterator.next()), expectedRow = expectedMatrix
					.getRows().get(expectedOTUIterator.next());
			assertTrue(actualRow.getParent() == actualMatrix);
			assertEqualsStandardRows(actualRow, expectedRow);
		}
	}

	public static void assertEqualsAttachmentNamespaces(
			final IAttachmentNamespace actualAttachmentNamespace,
			final IAttachmentNamespace expectedAttachmentNamespace) {
		assertEquals(actualAttachmentNamespace.getLabel(),
				expectedAttachmentNamespace.getLabel());
	}

	public static void assertEqualsAttachmentTypes(
			final IAttachmentType actualAttachmentType,
			final IAttachmentType expectedAttachmentType) {
		assertEquals(
				actualAttachmentType.getLabel(),
				expectedAttachmentType.getLabel());
		assertEqualsAttachmentNamespaces(
				actualAttachmentType.getNamespace(),
				expectedAttachmentType.getNamespace());
	}

	public static void assertEqualsPPodEntities(
			final IPPodEntity actualPPodEntity,
			final IPPodEntity expectedPPodEntity) {
		assertEqualsAttachmentSets(
				actualPPodEntity.getAttachments(),
				expectedPPodEntity.getAttachments());
	}

	public static void assertEqualsAttachmentSets(
			final Set<IAttachment> actualAttachments,
			final Set<IAttachment> expectedAttachments) {
		assertEquals(actualAttachments.size(), expectedAttachments.size());
		final Set<IAttachment> expectedAttachmentsCopy = newHashSet(expectedAttachments);
		for (final IAttachment actualAttachment : actualAttachments) {
			final IAttachment expectedAttachment = getOnlyElement(filter(
							expectedAttachmentsCopy,
							new IAttachment.IsOfNamespaceTypeLabelAndStringValue(
									actualAttachment)));
			expectedAttachmentsCopy.remove(expectedAttachment);
			assertEqualsAttachments(actualAttachment, expectedAttachment);
		}
	}

	public static void assertEqualsAttachments(
			final IAttachment actualAttachment,
			final IAttachment expectedAttachment) {
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
