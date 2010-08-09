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
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;
import java.util.Set;

import edu.upenn.cis.ppod.imodel.IAttachment;
import edu.upenn.cis.ppod.imodel.ILabeled;
import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.IPPodEntity;
import edu.upenn.cis.ppod.imodel.ISequenceSet;
import edu.upenn.cis.ppod.imodel.IStandardCharacter;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStandardRow;

/**
 * For asserting that various {@code edu.upenn.cis.ppod.model} elements are the
 * equal.
 * 
 * @author Sam Donnelly
 */
public class ModelAssert {

	public static <S extends Sequence<?>> void assertEqualsSequenceSets(
			final ISequenceSet<S> actualSeqSet,
			final ISequenceSet<S> expectedSeqSet) {
		assertEquals(actualSeqSet.getLabel(), actualSeqSet.getLabel());
		assertEquals(
				actualSeqSet.getSequences().size(),
				expectedSeqSet.getSequences().size());

		assertEquals(
				actualSeqSet.getParent().getOTUs().size(),
				expectedSeqSet.getParent().getOTUs().size());

		for (int otuPos = 0; otuPos < actualSeqSet.getParent().getOTUs().size(); otuPos++) {
			assertEqualsSequences(
					actualSeqSet
							.getSequence(actualSeqSet
									.getParent()
									.getOTUs()
									.get(otuPos)),
									expectedSeqSet
											.getSequence(expectedSeqSet
													.getParent()
													.getOTUs()
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
			final IOTUSet actualOTUSet,
			final IOTUSet expectedOTUSet) {
		assertEquals(actualOTUSet.getLabel(), expectedOTUSet.getLabel());
		if (expectedOTUSet.getPPodId() != null) {
			assertEquals(actualOTUSet.getPPodId(), expectedOTUSet.getPPodId());
		}
		assertEquals(actualOTUSet.getOTUs().size(), expectedOTUSet
				.getOTUs().size());
		for (final IOTU expectedOTU : expectedOTUSet.getOTUs()) {
			final IOTU foundOTU =
					findIf(
							actualOTUSet.getOTUs(),
							compose(
									equalTo(
									expectedOTU.getLabel()),
									ILabeled.getLabel));

			assertNotNull(foundOTU, "couldn't find an expected OTU ["
								+ expectedOTU.getLabel()
								+ "] in the actual OTU's: "
								+ actualOTUSet);
		}
	}

	public static void assertEqualsOTUs(
			final IOTU actualOTU,
			final IOTU expectedOTU) {
		assertEquals(actualOTU.getLabel(), expectedOTU.getLabel());
	}

	public static void assertEqualsStandardStates(
			final StandardState actualState, final StandardState expectedState) {
		assertEquals(actualState.getLabel(), expectedState.getLabel());
		assertEquals(actualState.getStateNumber(),
				expectedState.getStateNumber());
	}

	public static void assertEqualsCharacters(
			final IStandardCharacter actualCharacter,
			final IStandardCharacter expectedCharacter) {
		assertEqualsPPodEntities(actualCharacter, expectedCharacter);
		assertEquals(actualCharacter.getLabel(), expectedCharacter.getLabel());
		assertEquals(actualCharacter.getStates().size(),
				expectedCharacter
						.getStates().size());
		for (final StandardState actualState : actualCharacter
				.getStates()) {

			final StandardState expectedState =
					findIf(
							expectedCharacter.getStates(),
							compose(
									equalTo(actualState.getStateNumber()),
									StandardState.getStateNumber));

			assertNotNull(expectedState);
			assertTrue(expectedState.getParent() == expectedCharacter);
			assertEqualsStandardStates(
					actualState,
					expectedState);
		}

	}

	public static void assertEqualsStandardCells(
			final IStandardCell actualCell,
			final IStandardCell expectedCell) {
		assertEquals(actualCell.getType(), expectedCell.getType());
		assertEquals(actualCell.getElements().size(), expectedCell
				.getElements().size());
		for (final StandardState actualState : actualCell.getElements()) {
			final StandardState expectedState =
					findIf(
							expectedCell.getElements(),
							compose(
									equalTo(
										actualState.getStateNumber()),
									StandardState.getStateNumber));
			assertEqualsStandardStates(
					actualState,
					expectedState);
		}
	}

	public static void assertEqualsStandardRows(
			final IStandardRow actualRow,
			final IStandardRow expectedRow) {
		assertEquals(actualRow.getCells().size(), expectedRow.getCells().size());

		for (final Iterator<IStandardCell> actualCellItr = actualRow
				.getCells()
				.iterator(), expectedCellItr = expectedRow.getCells()
				.iterator(); actualCellItr
				.hasNext()
																		&& expectedCellItr
																				.hasNext();) {
			final IStandardCell actualCell = actualCellItr.next(), expectedCell = expectedCellItr
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
			final IStandardMatrix actualMatrix,
			final IStandardMatrix expectedMatrix) {
		assertEquals(actualMatrix.getLabel(), expectedMatrix.getLabel());
		assertEquals(
				actualMatrix.getDescription(),
				expectedMatrix.getDescription());

		assertEqualsOTUSet(actualMatrix.getParent(), expectedMatrix.getParent());

		assertEquals(
				actualMatrix.getCharacters().size(),
				actualMatrix.getCharacters().size());
		for (final Iterator<IStandardCharacter> actualCharacterItr = actualMatrix
				.getCharacters().iterator(), expectedCharacterItr = expectedMatrix
				.getCharacters().iterator(); actualCharacterItr
				.hasNext()
												&& expectedCharacterItr
														.hasNext();) {
			final IStandardCharacter actualCharacter = actualCharacterItr
					.next();
			final IStandardCharacter expectedCharacter =
					expectedCharacterItr.next();
			assertSame(actualCharacter.getParent(), actualMatrix);
			assertSame(expectedCharacter.getParent(), expectedMatrix);
			assertEqualsCharacters(actualCharacter, expectedCharacter);
		}
		assertEquals(actualMatrix.getRows().size(),
				expectedMatrix.getRows().size());

		for (final Iterator<IOTU> actualOTUIterator = actualMatrix.getParent()
				.getOTUs()
				.iterator(), expectedOTUIterator = expectedMatrix.getParent()
				.getOTUs()
				.iterator(); actualOTUIterator.hasNext()
								&& expectedOTUIterator.hasNext();) {
			final IStandardRow actualRow = actualMatrix
					.getRows().get(actualOTUIterator.next()), expectedRow = expectedMatrix
					.getRows().get(expectedOTUIterator.next());
			assertTrue(actualRow.getParent() == actualMatrix);
			assertEqualsStandardRows(actualRow, expectedRow);
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
