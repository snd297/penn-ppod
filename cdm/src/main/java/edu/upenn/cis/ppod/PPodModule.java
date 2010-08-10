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
package edu.upenn.cis.ppod;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;

import edu.upenn.cis.ppod.createorupdate.CreateOrUpdateModule;
import edu.upenn.cis.ppod.createorupdate.ICreateOrUpdateDNAMatrix;
import edu.upenn.cis.ppod.createorupdate.ICreateOrUpdateStandardMatrix;
import edu.upenn.cis.ppod.createorupdate.ICreateOrUpdateStudy;
import edu.upenn.cis.ppod.createorupdate.IMergeAttachments;
import edu.upenn.cis.ppod.createorupdate.IMergeOTUSets;
import edu.upenn.cis.ppod.createorupdate.IMergeTreeSets;
import edu.upenn.cis.ppod.dao.hibernate.DAOHibernateModule;
import edu.upenn.cis.ppod.imodel.IAttachment;
import edu.upenn.cis.ppod.imodel.IDNACell;
import edu.upenn.cis.ppod.imodel.IDNAMatrix;
import edu.upenn.cis.ppod.imodel.IDNARow;
import edu.upenn.cis.ppod.imodel.IDNASequenceSet;
import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.IStandardCell;
import edu.upenn.cis.ppod.imodel.IStandardCharacter;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStandardRow;
import edu.upenn.cis.ppod.imodel.IStudy;
import edu.upenn.cis.ppod.imodel.ITree;
import edu.upenn.cis.ppod.imodel.ITreeSet;
import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.DNACell;
import edu.upenn.cis.ppod.model.DNAMatrix;
import edu.upenn.cis.ppod.model.DNARow;
import edu.upenn.cis.ppod.model.DNASequenceSet;
import edu.upenn.cis.ppod.model.ModelModule;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.StandardRow;
import edu.upenn.cis.ppod.model.StandardState;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.services.StringPair;
import edu.upenn.cis.ppod.util.IPair;
import edu.upenn.cis.ppod.util.ISetVersionInfoVisitor;
import edu.upenn.cis.ppod.util.Pair;

import edu.upenn.cis.ppod.util.ISetVersionInfoVisitor;
import edu.upenn.cis.ppod.util.Pair;

/**
 * pPOD CDM guice configuration.
 * 
 * @author Sam Donnelly
 */
public final class PPodModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(IPair.IFactory.class).to(Pair.Factory.class);

		bind(IMergeOTUSets.IFactory.class).toProvider(
				FactoryProvider.newFactory(IMergeOTUSets.IFactory.class,
						IMergeOTUSets.class));
		bind(IMergeTreeSets.IFactory.class).toProvider(
				FactoryProvider.newFactory(IMergeTreeSets.IFactory.class,
						IMergeTreeSets.class));
		bind(ICreateOrUpdateStandardMatrix.IFactory.class).toProvider(
				FactoryProvider.newFactory(
						ICreateOrUpdateStandardMatrix.IFactory.class,
						ICreateOrUpdateStandardMatrix.class));
		bind(ICreateOrUpdateDNAMatrix.IFactory.class)
				.toProvider(
						FactoryProvider.newFactory(
								ICreateOrUpdateDNAMatrix.IFactory.class,
								ICreateOrUpdateDNAMatrix.class));

		bind(IMergeAttachments.IFactory.class).toProvider(
				FactoryProvider.newFactory(IMergeAttachments.IFactory.class,
						IMergeAttachments.class));

		bind(StandardState.IFactory.class).toProvider(
				FactoryProvider.newFactory(StandardState.IFactory.class,
						StandardState.class));

		bind(StringPair.IFactory.class).toProvider(
				FactoryProvider.newFactory(
						StringPair.IFactory.class,
						StringPair.class));

		bind(ISetVersionInfoVisitor.IFactory.class).toProvider(
				FactoryProvider.newFactory(
						ISetVersionInfoVisitor.IFactory.class,
						ISetVersionInfoVisitor.class));

		bind(ICreateOrUpdateStudy.IFactory.class).toProvider(
				FactoryProvider.newFactory(
						ICreateOrUpdateStudy.IFactory.class,
						ICreateOrUpdateStudy.class));

		bind(IStudy.class).to(Study.class);

		bind(IOTUSet.class).to(OTUSet.class);
		bind(IOTU.class).to(OTU.class);

		bind(IStandardMatrix.class).to(StandardMatrix.class);
		bind(IStandardCharacter.class).to(StandardCharacter.class);
		bind(IStandardRow.class).to(StandardRow.class);
		bind(IStandardCell.class).to(StandardCell.class);

		bind(IDNAMatrix.class).to(DNAMatrix.class);
		bind(IDNARow.class).to(DNARow.class);
		bind(IDNACell.class).to(DNACell.class);

		bind(IDNASequenceSet.class).to(DNASequenceSet.class);

		bind(ITreeSet.class).to(TreeSet.class);
		bind(ITree.class).to(Tree.class);

		bind(IAttachment.class).to(Attachment.class);

		install(new ModelModule());
		install(new DAOHibernateModule());
		install(new CreateOrUpdateModule());

	}
}
