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
import edu.upenn.cis.ppod.imodel.IStudy;
import edu.upenn.cis.ppod.imodel.IUUPPodEntity;

/**
 * @author Sam Donnelly
 * 
 */
public class ModelModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(IDNARow.class).to(DNARow.class);
		bind(IDNACell.class).to(DNACell.class);

		bind(INewVersionInfo.class)
				.to(NewVersionInfoDB.class)
				.in(RequestScoped.class);
		bind(Attachment.IIsOfNamespace.IFactory.class)
				.toProvider(
						FactoryProvider.newFactory(
								Attachment.IIsOfNamespace.IFactory.class,
								Attachment.IIsOfNamespace.class));
	}

	private static void preparePPodEntity(final IUUPPodEntity pPodEntity,
			final INewVersionInfo newVersionInfo) {
		pPodEntity.setVersionInfo(newVersionInfo.getNewVersionInfo());
		pPodEntity.setPPodId();
	}

	@Provides
	IStudy provideStudy(final INewVersionInfo newVersionInfo) {
		final IStudy study = new Study();
		preparePPodEntity(study, newVersionInfo);
		return study;
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
	IDNAMatrix provideDNAMatrix(final INewVersionInfo newVersionInfo,
			final DNARows rows) {
		final IDNAMatrix matrix = new DNAMatrix(rows);
		preparePPodEntity(matrix, newVersionInfo);
		matrix.setColumnVersionInfos(
				newVersionInfo.getNewVersionInfo());
		return matrix;
	}

}
