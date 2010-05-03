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

import edu.upenn.cis.ppod.dao.hibernate.DAOHibernateModule;
import edu.upenn.cis.ppod.model.CategoricalState;
import edu.upenn.cis.ppod.model.DNA;
import edu.upenn.cis.ppod.model.ModelModule;
import edu.upenn.cis.ppod.saveorupdate.IMergeAttachments;
import edu.upenn.cis.ppod.saveorupdate.IMergeOTUSets;
import edu.upenn.cis.ppod.saveorupdate.IMergeTreeSets;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateCategoricalMatrix;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateStudy;
import edu.upenn.cis.ppod.saveorupdate.SaveOrUpdateModule;
import edu.upenn.cis.ppod.services.StringPair;
import edu.upenn.cis.ppod.util.IPair;
import edu.upenn.cis.ppod.util.ISetPPodVersionInfoVisitor;
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
		bind(ISaveOrUpdateCategoricalMatrix.IFactory.class).toProvider(
				FactoryProvider.newFactory(ISaveOrUpdateCategoricalMatrix.IFactory.class,
						ISaveOrUpdateCategoricalMatrix.class));
		bind(IMergeAttachments.IFactory.class).toProvider(
				FactoryProvider.newFactory(IMergeAttachments.IFactory.class,
						IMergeAttachments.class));

		bind(CategoricalState.IFactory.class).toProvider(
				FactoryProvider.newFactory(CategoricalState.IFactory.class,
						CategoricalState.class));
		bind(DNA.IFactory.class).toProvider(
				FactoryProvider.newFactory(DNA.IFactory.class,
						DNA.class));

		bind(StringPair.IFactory.class).toProvider(
				FactoryProvider.newFactory(
						StringPair.IFactory.class,
						StringPair.class));

		bind(ISetPPodVersionInfoVisitor.IFactory.class).toProvider(
				FactoryProvider.newFactory(
						ISetPPodVersionInfoVisitor.IFactory.class,
						ISetPPodVersionInfoVisitor.class));

		bind(ISaveOrUpdateStudy.IFactory.class).toProvider(
				FactoryProvider.newFactory(
						ISaveOrUpdateStudy.IFactory.class,
						ISaveOrUpdateStudy.class));

		install(new ModelModule());
		install(new DAOHibernateModule());
		install(new SaveOrUpdateModule());

	}
}
