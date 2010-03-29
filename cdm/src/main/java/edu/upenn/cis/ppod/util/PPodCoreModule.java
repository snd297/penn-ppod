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
package edu.upenn.cis.ppod.util;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;

import edu.upenn.cis.ppod.dao.hibernate.DAOHibernateModule;
import edu.upenn.cis.ppod.dao.hibernate.HibernateDAOFactory;
import edu.upenn.cis.ppod.model.CharacterState;
import edu.upenn.cis.ppod.model.DNAState;
import edu.upenn.cis.ppod.model.ModelModule;
import edu.upenn.cis.ppod.saveorupdate.IMergeAttachments;
import edu.upenn.cis.ppod.saveorupdate.IMergeOTUSetFactory;
import edu.upenn.cis.ppod.saveorupdate.IMergeOTUSets;
import edu.upenn.cis.ppod.saveorupdate.IMergeTreeSets;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateMatrix;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateMatrixFactory;
import edu.upenn.cis.ppod.saveorupdate.SaveOrUpdateModule;
import edu.upenn.cis.ppod.saveorupdate.hibernate.SaveOrUpdateHibernateModule;
import edu.upenn.cis.ppod.security.ISimpleAuthenticationInfoFactory;
import edu.upenn.cis.ppod.security.SimpleAuthenticationInfoFactory;
import edu.upenn.cis.ppod.services.StringPair;

public final class PPodCoreModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(HibernateDAOFactory.IFactory.class).toProvider(
				FactoryProvider.newFactory(HibernateDAOFactory.IFactory.class,
						HibernateDAOFactory.class));

		bind(IPair.IFactory.class).to(Pair.Factory.class);

		bind(ISimpleAuthenticationInfoFactory.class).to(
				SimpleAuthenticationInfoFactory.class);

		bind(IMergeOTUSetFactory.class).toProvider(
				FactoryProvider.newFactory(IMergeOTUSetFactory.class,
						IMergeOTUSets.class));
		bind(IMergeTreeSets.IFactory.class).toProvider(
				FactoryProvider.newFactory(IMergeTreeSets.IFactory.class,
						IMergeTreeSets.class));
		bind(ISaveOrUpdateMatrixFactory.class).toProvider(
				FactoryProvider.newFactory(ISaveOrUpdateMatrixFactory.class,
						ISaveOrUpdateMatrix.class));
		bind(IMergeAttachments.IFactory.class).toProvider(
				FactoryProvider.newFactory(IMergeAttachments.IFactory.class,
						IMergeAttachments.class));

		bind(CharacterState.IFactory.class).toProvider(
				FactoryProvider.newFactory(CharacterState.IFactory.class,
						CharacterState.class));
		bind(DNAState.IFactory.class).toProvider(
				FactoryProvider.newFactory(DNAState.IFactory.class,
						DNAState.class));

		bind(StringPair.IFactory.class).toProvider(
				FactoryProvider.newFactory(
						StringPair.IFactory.class,
						StringPair.class));

		bind(ISetPPodVersionInfoVisitor.IFactory.class).toProvider(
				FactoryProvider.newFactory(
						ISetPPodVersionInfoVisitor.IFactory.class,
						ISetPPodVersionInfoVisitor.class));

		install(new ModelModule());
		install(new SaveOrUpdateHibernateModule());
		install(new DAOHibernateModule());
		install(new SaveOrUpdateModule());

	}
}
