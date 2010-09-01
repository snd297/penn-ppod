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
import com.google.inject.assistedinject.FactoryProvider;
import com.google.inject.servlet.RequestScoped;

import edu.upenn.cis.ppod.imodel.IDNACell;
import edu.upenn.cis.ppod.imodel.IDNAMatrix;
import edu.upenn.cis.ppod.imodel.IDNARow;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.IStandardCell;
import edu.upenn.cis.ppod.imodel.IStandardCharacter;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStandardRow;
import edu.upenn.cis.ppod.imodel.IStandardState;
import edu.upenn.cis.ppod.imodel.IStudy;
import edu.upenn.cis.ppod.imodel.ITree;
import edu.upenn.cis.ppod.imodel.ITreeSet;
import edu.upenn.cis.ppod.imodel.IUUPPodEntity;

/**
 * @author Sam Donnelly
 * 
 */
public class ModelModule extends AbstractModule {

	private static void preparePPodEntity(final IUUPPodEntity pPodEntity,
			final INewVersionInfo newVersionInfo) {
		pPodEntity.setVersionInfo(newVersionInfo.getNewVersionInfo());
		pPodEntity.setPPodId();
	}

	@Override
	protected void configure() {

		bind(IDNARow.class).to(DNARow.class);
		bind(IDNACell.class).to(DNACell.class);

		bind(IStandardState.IFactory.class).toProvider(
				FactoryProvider.newFactory(IStandardState.IFactory.class,
						StandardState.class));

		bind(IStandardRow.class).to(StandardRow.class);
		bind(IStandardCell.class).to(StandardCell.class);

		bind(INewVersionInfo.class)
				.to(NewVersionInfoDB.class)
				.in(RequestScoped.class);
		bind(Attachment.IIsOfNamespace.IFactory.class)
				.toProvider(
						FactoryProvider.newFactory(
								Attachment.IIsOfNamespace.IFactory.class,
								Attachment.IIsOfNamespace.class));
	}

	@Provides
	IDNAMatrix provideDNAMatrix(final INewVersionInfo newVersionInfo,
			final DNARows rows) {
		final IDNAMatrix matrix = new DNAMatrix(rows);
		preparePPodEntity(matrix, newVersionInfo);
		matrix.setColumnVersionInfos(
				newVersionInfo.getNewVersionInfo());
		return matrix;
	}

	@Provides
	IOTU provideOTU(final INewVersionInfo newVersionInfo) {
		final IOTU otu = new OTU();
		preparePPodEntity(otu, newVersionInfo);
		return otu;
	}

	@Provides
	IOTUSet provideOTUSet(final INewVersionInfo newVersionInfo) {
		final IOTUSet otuSet = new OTUSet();
		preparePPodEntity(otuSet, newVersionInfo);
		return otuSet;
	}

	@Provides
	IStandardCharacter provideStandardCharacter(
			final INewVersionInfo newVersionInfo) {
		final IStandardCharacter character = new StandardCharacter();
		preparePPodEntity(character, newVersionInfo);
		return character;
	}

	@Provides
	IStandardMatrix provideStandardMatrix(final INewVersionInfo newVersionInfo,
			final StandardRows rows) {
		final IStandardMatrix matrix = new StandardMatrix(rows);
		preparePPodEntity(matrix, newVersionInfo);
		matrix.setColumnVersionInfos(
				newVersionInfo.getNewVersionInfo());
		return matrix;
	}

	@Provides
	IStudy provideStudy(final INewVersionInfo newVersionInfo) {
		final IStudy study = new Study();
		preparePPodEntity(study, newVersionInfo);
		return study;
	}

	@Provides
	ITree provideTree(final INewVersionInfo newVersionInfo) {
		final ITree tree = new Tree();
		preparePPodEntity(tree, newVersionInfo);
		return tree;
	}

	@Provides
	ITreeSet provideTreeSet(final INewVersionInfo newVersionInfo) {
		final ITreeSet treeSet = new TreeSet();
		preparePPodEntity(treeSet, newVersionInfo);
		return treeSet;
	}

}
