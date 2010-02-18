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

import edu.upenn.cis.ppod.dao.hibernate.AttachmentNamespaceDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.AttachmentTypeDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.HibernateDAOFactory;
import edu.upenn.cis.ppod.dao.hibernate.IAttachmentNamespaceDAOHibernateFactory;
import edu.upenn.cis.ppod.dao.hibernate.IAttachmentTypeDAOHibernateFactory;
import edu.upenn.cis.ppod.model.CharacterState;
import edu.upenn.cis.ppod.model.CharacterStateMatrixFactory;
import edu.upenn.cis.ppod.model.DNAState;
import edu.upenn.cis.ppod.model.NewPPodVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.ICharacterStateMatrixFactory;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.saveorupdate.IMergeAttachment;
import edu.upenn.cis.ppod.saveorupdate.IMergeOTUSetFactory;
import edu.upenn.cis.ppod.saveorupdate.IMergeTreeSetFactory;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateMatrixFactory;
import edu.upenn.cis.ppod.saveorupdate.MergeAttachment;
import edu.upenn.cis.ppod.saveorupdate.MergeOTUSet;
import edu.upenn.cis.ppod.saveorupdate.MergeTreeSet;
import edu.upenn.cis.ppod.saveorupdate.SaveOrUpdateCharacterStateMatrix;
import edu.upenn.cis.ppod.saveorupdate.hibernate.ISaveOrUpdateStudyHibernateFactory;
import edu.upenn.cis.ppod.saveorupdate.hibernate.SaveOrUpdateStudyHibernate;
import edu.upenn.cis.ppod.security.ISimpleAuthenticationInfoFactory;
import edu.upenn.cis.ppod.security.SimpleAuthenticationInfoFactory;

public final class PPodCoreModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(HibernateDAOFactory.IFactory.class).toProvider(
				FactoryProvider.newFactory(HibernateDAOFactory.IFactory.class,
						HibernateDAOFactory.class));

		bind(IAttachmentNamespaceDAOHibernateFactory.class).toProvider(
				FactoryProvider.newFactory(
						IAttachmentNamespaceDAOHibernateFactory.class,
						AttachmentNamespaceDAOHibernate.class));
		bind(IAttachmentTypeDAOHibernateFactory.class).toProvider(
				FactoryProvider.newFactory(
						IAttachmentTypeDAOHibernateFactory.class,
						AttachmentTypeDAOHibernate.class));

		bind(IPair.IFactory.class).to(Pair.Factory.class);

		bind(ISimpleAuthenticationInfoFactory.class).to(
				SimpleAuthenticationInfoFactory.class);

		bind(ISaveOrUpdateStudyHibernateFactory.class).toProvider(
				FactoryProvider.newFactory(
						ISaveOrUpdateStudyHibernateFactory.class,
						SaveOrUpdateStudyHibernate.class));
		bind(IMergeOTUSetFactory.class).toProvider(
				FactoryProvider.newFactory(IMergeOTUSetFactory.class,
						MergeOTUSet.class));
		bind(IMergeTreeSetFactory.class).toProvider(
				FactoryProvider.newFactory(IMergeTreeSetFactory.class,
						MergeTreeSet.class));
		bind(ISaveOrUpdateMatrixFactory.class).toProvider(
				FactoryProvider.newFactory(
						ISaveOrUpdateMatrixFactory.class,
						SaveOrUpdateCharacterStateMatrix.class));

		bind(IMergeAttachment.IFactory.class).toProvider(
				FactoryProvider.newFactory(IMergeAttachment.IFactory.class,
						MergeAttachment.class));

		bind(CharacterState.IFactory.class).toProvider(
				FactoryProvider.newFactory(CharacterState.IFactory.class,
						CharacterState.class));
		bind(DNAState.IFactory.class).toProvider(
				FactoryProvider.newFactory(DNAState.IFactory.class,
						DNAState.class));

		bind(ICharacterStateMatrixFactory.class).to(
				CharacterStateMatrixFactory.class);

		bind(SetPPodVersionInfoVisitor.IFactory.class).toProvider(
				FactoryProvider.newFactory(
						SetPPodVersionInfoVisitor.IFactory.class,
						SetPPodVersionInfoVisitor.class));

		bind(INewPPodVersionInfo.class).to(NewPPodVersionInfo.class);
	}

}
