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

/**
 * @author Sam Donnelly
 */
public class ModelFactory {

	public static Attachment newAttachment(final VersionInfo versionInfo) {
		final Attachment attachment = new Attachment();
		preparePPodEntity(attachment, versionInfo);
		return attachment;
	}

	public static DnaCell newDNACell(final VersionInfo versionInfo) {
		final DnaCell cell = new DnaCell();
		preparePPodEntity(cell, versionInfo);
		return cell;
	}

	public static DnaMatrix newDNAMatrix(
			final VersionInfo versionInfo) {
		final DnaMatrix matrix = new DnaMatrix();
		preparePPodEntity(matrix, versionInfo);
		matrix.setColumnVersionInfos(versionInfo);
		return matrix;
	}

	public static DnaRow newDnaRow(final VersionInfo versionInfo) {
		final DnaRow row = new DnaRow();
		preparePPodEntity(row, versionInfo);
		return row;
	}

	public static DnaSequence newDNASequence(
			final VersionInfo versionInfo) {
		final DnaSequence sequence = new DnaSequence();
		preparePPodEntity(sequence, versionInfo);
		return sequence;
	}

	public static DnaSequenceSet newDNASequenceSet(
			final VersionInfo versionInfo) {
		final DnaSequenceSet sequenceSet = new DnaSequenceSet();
		preparePPodEntity(sequenceSet, versionInfo);
		return sequenceSet;
	}

	public static Otu newOTU(final VersionInfo versionInfo) {
		final Otu otu = new Otu();
		preparePPodEntity(otu, versionInfo);
		return otu;
	}

	public static OtuSet newOTUSet(final VersionInfo versionInfo) {
		final OtuSet otuSet = new OtuSet();
		preparePPodEntity(otuSet, versionInfo);
		return otuSet;
	}

	public static StandardCell newStandardCell(
			final VersionInfo versionInfo) {
		final StandardCell cell = new StandardCell();
		preparePPodEntity(cell, versionInfo);
		return cell;
	}

	public static StandardCharacter newStandardCharacter(
			final VersionInfo versionInfo) {
		final StandardCharacter character = new StandardCharacter();
		preparePPodEntity(character, versionInfo);
		return character;
	}

	public static StandardMatrix newStandardMatrix(
			final VersionInfo versionInfo) {
		final StandardMatrix matrix = new StandardMatrix();
		preparePPodEntity(matrix, versionInfo);
		matrix.setColumnVersionInfos(versionInfo);
		return matrix;
	}

	public static StandardRow newStandardRow(
			final VersionInfo versionInfo) {
		final StandardRow row = new StandardRow();
		preparePPodEntity(row, versionInfo);
		return row;
	}

	public static Study newStudy(final VersionInfo versionInfo) {
		final Study study = new Study();
		preparePPodEntity(study, versionInfo);
		return study;
	}

	public static Tree newTree(final VersionInfo versionInfo) {
		final Tree tree = new Tree();
		preparePPodEntity(tree, versionInfo);
		return tree;
	}

	public static TreeSet newTreeSet(final VersionInfo versionInfo) {
		final TreeSet treeSet = new TreeSet();
		preparePPodEntity(treeSet, versionInfo);
		return treeSet;
	}

	private static void preparePPodEntity(final IPPodEntity pPodEntity,
			final VersionInfo versionInfo) {
		pPodEntity.setVersionInfo(versionInfo);
	}

}
