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
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;

import edu.upenn.cis.ppod.dao.hibernate.AttachmentNamespaceDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.AttachmentTypeDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.HibernateDAOFactory;
import edu.upenn.cis.ppod.dao.hibernate.IAttachmentNamespaceDAOHibernateFactory;
import edu.upenn.cis.ppod.dao.hibernate.IAttachmentTypeDAOHibernateFactory;
import edu.upenn.cis.ppod.model.CharacterState;
import edu.upenn.cis.ppod.model.DNASequence;
import edu.upenn.cis.ppod.model.DNASequenceSet;
import edu.upenn.cis.ppod.model.DNAState;
import edu.upenn.cis.ppod.model.NewPPodVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.saveorupdate.IMergeAttachments;
import edu.upenn.cis.ppod.saveorupdate.IMergeMolecularSequenceSets;
import edu.upenn.cis.ppod.saveorupdate.IMergeOTUSetFactory;
import edu.upenn.cis.ppod.saveorupdate.IMergeTreeSetsFactory;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateMatrixFactory;
import edu.upenn.cis.ppod.saveorupdate.MergeAttachments;
import edu.upenn.cis.ppod.saveorupdate.MergeMolecularSequenceSets;
import edu.upenn.cis.ppod.saveorupdate.MergeOTUSets;
import edu.upenn.cis.ppod.saveorupdate.MergeTreeSets;
import edu.upenn.cis.ppod.saveorupdate.SaveOrUpdateCharacterStateMatrix;
import edu.upenn.cis.ppod.saveorupdate.hibernate.ISaveOrUpdateStudyHibernateFactory;
import edu.upenn.cis.ppod.saveorupdate.hibernate.SaveOrUpdateStudiesHibernate;
import edu.upenn.cis.ppod.security.ISimpleAuthenticationInfoFactory;
import edu.upenn.cis.ppod.security.SimpleAuthenticationInfoFactory;

public final class PPodCoreModule extends AbstractModule {

	private final static class MergeMolecularSequenceSetsTypeLiteral
			extends
			TypeLiteral<MergeMolecularSequenceSets<DNASequenceSet, DNASequence>> {}

	private final static class IMergeMolecularSequenceSetsIFactoryTypeLiteral
			extends
			TypeLiteral<IMergeMolecularSequenceSets.IFactory<DNASequenceSet, DNASequence>> {}

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
						SaveOrUpdateStudiesHibernate.class));
		bind(IMergeOTUSetFactory.class).toProvider(
				FactoryProvider.newFactory(IMergeOTUSetFactory.class,
						MergeOTUSets.class));
		bind(IMergeTreeSetsFactory.class).toProvider(
				FactoryProvider.newFactory(IMergeTreeSetsFactory.class,
						MergeTreeSets.class));
		bind(ISaveOrUpdateMatrixFactory.class).toProvider(
				FactoryProvider.newFactory(ISaveOrUpdateMatrixFactory.class,
						SaveOrUpdateCharacterStateMatrix.class));

		final TypeLiteral<IMergeMolecularSequenceSets.IFactory<DNASequenceSet, DNASequence>> mergeDNASequencesFactoryTypeLiteral = new IMergeMolecularSequenceSetsIFactoryTypeLiteral();

		final TypeLiteral<MergeMolecularSequenceSets<DNASequenceSet, DNASequence>> mergeDNASequenceSetTypeLiteral = new MergeMolecularSequenceSetsTypeLiteral();

		bind(mergeDNASequencesFactoryTypeLiteral).toProvider(
				FactoryProvider.newFactory(mergeDNASequencesFactoryTypeLiteral,
						mergeDNASequenceSetTypeLiteral));

		bind(IMergeAttachments.IFactory.class).toProvider(
				FactoryProvider.newFactory(IMergeAttachments.IFactory.class,
						MergeAttachments.class));

		bind(CharacterState.IFactory.class).toProvider(
				FactoryProvider.newFactory(CharacterState.IFactory.class,
						CharacterState.class));
		bind(DNAState.IFactory.class).toProvider(
				FactoryProvider.newFactory(DNAState.IFactory.class,
						DNAState.class));

		bind(SetPPodVersionInfoVisitor.IFactory.class).toProvider(
				FactoryProvider.newFactory(
						SetPPodVersionInfoVisitor.IFactory.class,
						SetPPodVersionInfoVisitor.class));

		bind(INewPPodVersionInfo.IFactory.class).toProvider(
				FactoryProvider.newFactory(INewPPodVersionInfo.IFactory.class,
						NewPPodVersionInfo.class));

	}
}
