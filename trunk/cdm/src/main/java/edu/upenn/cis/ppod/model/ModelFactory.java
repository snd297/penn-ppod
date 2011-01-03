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

import edu.upenn.cis.ppod.imodel.IPPodEntity;
import edu.upenn.cis.ppod.imodel.ITree;
import edu.upenn.cis.ppod.imodel.ITreeSet;
import edu.upenn.cis.ppod.imodel.IUUPPodEntity;
import edu.upenn.cis.ppod.imodel.IVersionInfo;

/**
 * @author Sam Donnelly
 */
public class ModelFactory {

	public static Attachment newAttachment(final IVersionInfo versionInfo) {
		final Attachment attachment = new Attachment();
		prepareUUPPodEntity(attachment, versionInfo);
		return attachment;
	}

	public static DNACell newDNACell(final IVersionInfo versionInfo) {
		final DNACell cell = new DNACell();
		preparePPodEntity(cell, versionInfo);
		return cell;
	}

	public static DNAMatrix newDNAMatrix(
			final IVersionInfo versionInfo) {
		final DNAMatrix matrix = new DNAMatrix();
		prepareUUPPodEntity(matrix, versionInfo);
		matrix.setColumnVersionInfos(versionInfo);
		return matrix;
	}

	public static DNARow newDNARow(final IVersionInfo versionInfo) {
		final DNARow row = new DNARow();
		preparePPodEntity(row, versionInfo);
		return row;
	}

	public static DNASequence newDNASequence(
			final IVersionInfo versionInfo) {
		final DNASequence sequence = new DNASequence();
		preparePPodEntity(sequence, versionInfo);
		return sequence;
	}

	public static DNASequenceSet newDNASequenceSet(
			final IVersionInfo versionInfo) {
		final DNASequenceSet sequenceSet = new DNASequenceSet();
		prepareUUPPodEntity(sequenceSet, versionInfo);
		return sequenceSet;
	}

	public static Otu newOTU(final IVersionInfo versionInfo) {
		final Otu otu = new Otu();
		prepareUUPPodEntity(otu, versionInfo);
		return otu;
	}

	public static OtuSet newOTUSet(final IVersionInfo versionInfo) {
		final OtuSet otuSet = new OtuSet();
		prepareUUPPodEntity(otuSet, versionInfo);
		return otuSet;
	}

	public static StandardCell newStandardCell(
			final IVersionInfo versionInfo) {
		final StandardCell cell = new StandardCell();
		preparePPodEntity(cell, versionInfo);
		return cell;
	}

	public static StandardCharacter newStandardCharacter(
			final IVersionInfo versionInfo) {
		final StandardCharacter character = new StandardCharacter();
		prepareUUPPodEntity(character, versionInfo);
		return character;
	}

	public static StandardMatrix newStandardMatrix(
			final IVersionInfo versionInfo) {
		final StandardMatrix matrix = new StandardMatrix();
		prepareUUPPodEntity(matrix, versionInfo);
		matrix.setColumnVersionInfos(versionInfo);
		return matrix;
	}

	public static StandardRow newStandardRow(
			final IVersionInfo versionInfo) {
		final StandardRow row = new StandardRow();
		preparePPodEntity(row, versionInfo);
		return row;
	}

	public static Study newStudy(final IVersionInfo versionInfo) {
		final Study study = new Study();
		prepareUUPPodEntity(study, versionInfo);
		return study;
	}

	public static ITree newTree(final IVersionInfo versionInfo) {
		final ITree tree = new Tree();
		prepareUUPPodEntity(tree, versionInfo);
		return tree;
	}

	public static ITreeSet newTreeSet(final IVersionInfo versionInfo) {
		final ITreeSet treeSet = new TreeSet();
		prepareUUPPodEntity(treeSet, versionInfo);
		return treeSet;
	}

	private static void preparePPodEntity(final IPPodEntity pPodEntity,
			final IVersionInfo versionInfo) {
		pPodEntity.setVersionInfo(versionInfo);
	}

	private static void prepareUUPPodEntity(final IUUPPodEntity pPodEntity,
			final IVersionInfo versionInfo) {
		preparePPodEntity(pPodEntity, versionInfo);
		pPodEntity.setPPodId();
	}

}
