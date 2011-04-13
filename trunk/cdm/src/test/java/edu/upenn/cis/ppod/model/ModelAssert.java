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
import static com.google.common.collect.Iterables.find;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import edu.upenn.cis.ppod.PPodOtu;
import edu.upenn.cis.ppod.PPodOtuSet;
import edu.upenn.cis.ppod.PPodStandardCell;
import edu.upenn.cis.ppod.PPodStandardCharacter;
import edu.upenn.cis.ppod.PPodStandardMatrix;
import edu.upenn.cis.ppod.PPodStandardRow;
import edu.upenn.cis.ppod.PPodStandardState;
import edu.upenn.cis.ppod.dto.ILabeled;

/**
 * For asserting that various {@code edu.upenn.cis.ppod.model} elements are the
 * equal.
 * 
 * @author Sam Donnelly
 */
public class ModelAssert {

	// public static <S extends Sequence<?>> void assertEqualsSequenceSets(
	// final SequenceSet<S> actualSeqSet,
	// final PPodDnaSequenceSet expectedSeqSet) {
	// assertEquals(actualSeqSet.getLabel(), actualSeqSet.getLabel());
	// assertEquals(
	// actualSeqSet.getSequences().size(),
	// expectedSeqSet.getSequences().size());
	//
	// for (int otuPos = 0; otuPos < actualSeqSet.getParent().getOtus().size();
	// otuPos++) {
	// assertEqualsSequences(
	// actualSeqSet
	// .getSequences().get(actualSeqSet
	// .getParent()
	// .getOtus()
	// .get(otuPos)),
	// expectedSeqSet
	// .getSequences().get(otuPos));
	//
	// }
	// }
	//
	// public static void assertEqualsSequences(
	// final Sequence<?> actualSeq,
	// final PPodDnaSequence expectedSeq) {
	// assertEquals(actualSeq.getSequence(), expectedSeq.getSequence());
	// assertEquals(actualSeq.getAccession(), expectedSeq.getAccession());
	// assertEquals(actualSeq.getDescription(), expectedSeq.getDescription());
	// assertEquals(actualSeq.getName(), expectedSeq.getName());
	// }

	public static void assertEqualsOTUSet(
			final OtuSet actualOTUSet,
			final PPodOtuSet expectedOTUSet) {
		assertEquals(actualOTUSet.getLabel(), expectedOTUSet.getLabel());
		if (expectedOTUSet.getPPodId() != null) {
			assertEquals(actualOTUSet.getPPodId(), expectedOTUSet.getPPodId());
		}
		assertEquals(actualOTUSet.getOtus().size(), expectedOTUSet
				.getOtus().size());
		for (final PPodOtu expectedOTU : expectedOTUSet.getOtus()) {
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
			final StandardState actualState,
			final PPodStandardState expectedState) {
		assertEquals(actualState.getLabel(), expectedState.getLabel());
		assertEquals(actualState.getStateNumber().intValue(),
				expectedState.getStateNumber());
	}

	public static void assertEqualsCharacters(
			final StandardCharacter actualCharacter,
			final PPodStandardCharacter expectedCharacter) {
		// assertEqualsPPodEntities(actualCharacter, expectedCharacter);
		assertEquals(actualCharacter.getLabel(), expectedCharacter.getLabel());
		assertEquals(actualCharacter.getStates().size(),
				expectedCharacter
						.getStates().size());
		for (final StandardState actualState : actualCharacter
				.getStates().values()) {

			final PPodStandardState expectedState =
					find(
							expectedCharacter.getStates(),
							compose(
									equalTo(actualState.getStateNumber()),
									PPodStandardState.getStateNumber),
									null);

			assertNotNull(expectedState);
			// assertTrue(expectedState.getParent() == expectedCharacter);
			assertEqualsStandardStates(
					actualState,
					expectedState);
		}

	}

	public static void assertEqualsStandardCells(
			final StandardCell actualCell,
			final PPodStandardCell expectedCell) {
		assertEquals(actualCell.getType(), expectedCell.getType());
		assertEquals(actualCell.getStatesSmartly().size(), expectedCell
				.getStates().size());
		for (final StandardState actualState : actualCell.getStatesSmartly()) {
			assertTrue(expectedCell.getStates().contains(
					actualState.getStateNumber()));
		}
	}

	public static void assertEqualsStandardRows(
			final StandardRow actualRow,
			final PPodStandardRow expectedRow) {
		assertEquals(actualRow.getCells().size(), expectedRow.getCells().size());

		for (int ic = 0; ic < actualRow.getCells().size(); ic++) {
			final StandardCell actualCell = actualRow.getCells().get(ic);
			final PPodStandardCell expectedCell = expectedRow.getCells()
					.get(ic);
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
			final PPodStandardMatrix expectedMatrix) {
		assertEquals(actualMatrix.getLabel(), expectedMatrix.getLabel());

		assertEquals(
				actualMatrix.getCharacters().size(),
				actualMatrix.getCharacters().size());

		for (int ic = 0; ic < actualMatrix.getCharacters().size(); ic++) {
			final StandardCharacter actualCharacter = actualMatrix
					.getCharacters().get(ic);
			final PPodStandardCharacter expectedCharacter = expectedMatrix
					.getCharacters().get(ic);
			assertSame(actualCharacter.getParent(), actualMatrix);
			assertEqualsCharacters(actualCharacter, expectedCharacter);
		}
		assertEquals(actualMatrix.getRows().size(),
				expectedMatrix.getRows().size());

		for (int io = 0; io < actualMatrix.getParent().getOtus().size(); io++) {
			final StandardRow actualRow = actualMatrix
					.getRows().get(actualMatrix.getParent().getOtus().get(io));
			final PPodStandardRow expectedRow = expectedMatrix.getRows()
					.get(io);
			assertTrue(actualRow.getParent() == actualMatrix);
			assertEqualsStandardRows(actualRow, expectedRow);
		}
	}

	// public static void assertEqualsAttachmentNamespaces(
	// final AttachmentNamespace actualAttachmentNamespace,
	// final AttachmentNamespace expectedAttachmentNamespace) {
	// assertEquals(actualAttachmentNamespace.getLabel(),
	// expectedAttachmentNamespace.getLabel());
	// }
	//
	// public static void assertEqualsAttachmentTypes(
	// final AttachmentType actualAttachmentType,
	// final AttachmentType expectedAttachmentType) {
	// assertEquals(
	// actualAttachmentType.getLabel(),
	// expectedAttachmentType.getLabel());
	// assertEqualsAttachmentNamespaces(
	// actualAttachmentType.getNamespace(),
	// expectedAttachmentType.getNamespace());
	// }
	//
	// public static void assertEqualsPPodEntities(
	// final IPPodEntity actualPPodEntity,
	// final IPPodEntity expectedPPodEntity) {
	// assertEqualsAttachmentSets(
	// actualPPodEntity.getAttachments(),
	// expectedPPodEntity.getAttachments());
	// }
	//
	// public static void assertEqualsAttachmentSets(
	// final Set<Attachment> actualAttachments,
	// final Set<Attachment> expectedAttachments) {
	// assertEquals(actualAttachments.size(), expectedAttachments.size());
	// final Set<Attachment> expectedAttachmentsCopy =
	// newHashSet(expectedAttachments);
	// for (final Attachment actualAttachment : actualAttachments) {
	// final Attachment expectedAttachment = getOnlyElement(filter(
	// expectedAttachmentsCopy,
	// new Attachment.IsOfNamespaceTypeLabelAndStringValue(
	// actualAttachment)));
	// expectedAttachmentsCopy.remove(expectedAttachment);
	// assertEqualsAttachments(actualAttachment, expectedAttachment);
	// }
	// }
	//
	// public static void assertEqualsAttachments(
	// final Attachment actualAttachment,
	// final Attachment expectedAttachment) {
	// assertEquals(actualAttachment.getLabel(), expectedAttachment.getLabel());
	// assertEquals(actualAttachment.getStringValue(), expectedAttachment
	// .getStringValue());
	// assertEquals(actualAttachment.getBytesValue(), expectedAttachment
	// .getBytesValue());
	// assertEqualsAttachmentTypes(actualAttachment.getType(),
	// expectedAttachment.getType());
	// assertEquals(actualAttachment.getAttachments().size(),
	// expectedAttachment.getAttachments().size());
	// if (expectedAttachment.getAttachments().size() > 0) {
	// throw new IllegalArgumentException(
	// "We don't support nested attchaments yet");
	// }
	// }

	/**
	 * Prevent inheritance and instantiation.
	 */
	private ModelAssert() {
		throw new AssertionError("can't instantiate a ModelAssert");
	}
}
