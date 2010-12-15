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

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import edu.upenn.cis.ppod.imodel.IAttachment;
import edu.upenn.cis.ppod.imodel.IDNACell;
import edu.upenn.cis.ppod.imodel.IDNAMatrix;
import edu.upenn.cis.ppod.imodel.IDNARow;
import edu.upenn.cis.ppod.imodel.IDNASequence;
import edu.upenn.cis.ppod.imodel.IDNASequenceSet;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.IPPodEntity;
import edu.upenn.cis.ppod.imodel.IStandardCell;
import edu.upenn.cis.ppod.imodel.IStandardCharacter;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStandardRow;
import edu.upenn.cis.ppod.imodel.IStudy;
import edu.upenn.cis.ppod.imodel.ITree;
import edu.upenn.cis.ppod.imodel.ITreeSet;
import edu.upenn.cis.ppod.imodel.IUUPPodEntity;

/**
 * @author Sam Donnelly
 */
public class ModelFactory extends AbstractModule {

	private static void preparePPodEntity(final IPPodEntity pPodEntity,
			final INewVersionInfo newVersionInfo) {
		pPodEntity.setVersionInfo(newVersionInfo.getNewVersionInfo());
	}

	private static void prepareUUPPodEntity(final IUUPPodEntity pPodEntity,
			final INewVersionInfo newVersionInfo) {
		preparePPodEntity(pPodEntity, newVersionInfo);
		pPodEntity.setPPodId();
	}

	@Override
	protected void configure() {}

	public IAttachment newAttachment(final INewVersionInfo newVersionInfo) {
		final IAttachment attachment = new Attachment();
		prepareUUPPodEntity(attachment, newVersionInfo);
		return attachment;
	}

	public IDNACell provideDNACell(final INewVersionInfo newVersionInfo) {
		final IDNACell cell = new DNACell();
		preparePPodEntity(cell, newVersionInfo);
		return cell;
	}

	public IDNAMatrix provideDNAMatrix(final INewVersionInfo newVersionInfo) {
		final IDNAMatrix matrix = new DNAMatrix();
		prepareUUPPodEntity(matrix, newVersionInfo);
		matrix.setColumnVersionInfos(
				newVersionInfo.getNewVersionInfo());
		return matrix;
	}

	public IDNARow provideDNARow(final INewVersionInfo newVersionInfo) {
		final IDNARow row = new DNARow();
		preparePPodEntity(row, newVersionInfo);
		return row;
	}

	public IDNASequence provideDNASequence(
			final INewVersionInfo newVersionInfo) {
		final IDNASequence sequence = new DNASequence();
		preparePPodEntity(sequence, newVersionInfo);
		return sequence;
	}

	public IDNASequenceSet provideDNASequenceSet(
			final INewVersionInfo newVersionInfo) {
		final IDNASequenceSet sequenceSet = new DNASequenceSet();
		prepareUUPPodEntity(sequenceSet, newVersionInfo);
		return sequenceSet;
	}

	public IOTU provideOTU(final INewVersionInfo newVersionInfo) {
		final IOTU otu = new OTU();
		prepareUUPPodEntity(otu, newVersionInfo);
		return otu;
	}

	public IOTUSet provideOTUSet(final INewVersionInfo newVersionInfo) {
		final IOTUSet otuSet = new OTUSet();
		prepareUUPPodEntity(otuSet, newVersionInfo);
		return otuSet;
	}

	public IStandardCell provideStandardCell(
			final INewVersionInfo newVersionInfo) {
		final IStandardCell cell = new StandardCell();
		preparePPodEntity(cell, newVersionInfo);
		return cell;
	}

	public IStandardCharacter provideStandardCharacter(
			final INewVersionInfo newVersionInfo) {
		final IStandardCharacter character = new StandardCharacter();
		prepareUUPPodEntity(character, newVersionInfo);
		return character;
	}

	public IStandardMatrix provideStandardMatrix(
			final INewVersionInfo newVersionInfo) {
		final IStandardMatrix matrix = new StandardMatrix();
		prepareUUPPodEntity(matrix, newVersionInfo);
		matrix.setColumnVersionInfos(
				newVersionInfo.getNewVersionInfo());
		return matrix;
	}

	@Provides
	IStandardRow provideStandardRow(final INewVersionInfo newVersionInfo) {
		final IStandardRow row = new StandardRow();
		preparePPodEntity(row, newVersionInfo);
		return row;
	}

	@Provides
	IStudy provideStudy(final INewVersionInfo newVersionInfo) {
		final IStudy study = new Study();
		prepareUUPPodEntity(study, newVersionInfo);
		return study;
	}

	@Provides
	ITree provideTree(final INewVersionInfo newVersionInfo) {
		final ITree tree = new Tree();
		prepareUUPPodEntity(tree, newVersionInfo);
		return tree;
	}

	@Provides
	ITreeSet provideTreeSet(final INewVersionInfo newVersionInfo) {
		final ITreeSet treeSet = new TreeSet();
		prepareUUPPodEntity(treeSet, newVersionInfo);
		return treeSet;
	}

}
