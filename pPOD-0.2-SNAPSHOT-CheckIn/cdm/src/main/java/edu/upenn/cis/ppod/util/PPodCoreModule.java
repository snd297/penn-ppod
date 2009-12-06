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

import edu.upenn.cis.ppod.dao.hibernate.HibernateDAOFactory;
import edu.upenn.cis.ppod.dao.hibernate.StudyDAOHibernate;
import edu.upenn.cis.ppod.model.CharacterState;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateMatrix;
import edu.upenn.cis.ppod.saveorupdate.SaveOrUpdateMatrix;
import edu.upenn.cis.ppod.saveorupdate.hibernate.ISaveOrUpdateAttachmentHibernateFactory;
import edu.upenn.cis.ppod.saveorupdate.hibernate.ISaveOrUpdateOTUSetHibernateFactory;
import edu.upenn.cis.ppod.saveorupdate.hibernate.ISaveOrUpdateStudyHibernateFactory;
import edu.upenn.cis.ppod.saveorupdate.hibernate.ISaveOrUpdateTreeSetHibernateFactory;
import edu.upenn.cis.ppod.saveorupdate.hibernate.SaveOrUpdateAttachmentHibernate;
import edu.upenn.cis.ppod.saveorupdate.hibernate.SaveOrUpdateOTUSetHibernate;
import edu.upenn.cis.ppod.saveorupdate.hibernate.SaveOrUpdateStudyHibernate;
import edu.upenn.cis.ppod.saveorupdate.hibernate.SaveOrUpdateTreeSetHibernate;
import edu.upenn.cis.ppod.security.ISimpleAuthenticationInfoFactory;
import edu.upenn.cis.ppod.security.SimpleAuthenticationInfoFactory;

public final class PPodCoreModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(HibernateDAOFactory.IFactory.class).toProvider(
				FactoryProvider.newFactory(HibernateDAOFactory.IFactory.class,
						HibernateDAOFactory.class));
		bind(StudyDAOHibernate.class);

		bind(IPair.IFactory.class).to(Pair.Factory.class);

		bind(ISimpleAuthenticationInfoFactory.class).to(
				SimpleAuthenticationInfoFactory.class);

		bind(ISaveOrUpdateStudyHibernateFactory.class).toProvider(
				FactoryProvider.newFactory(
						ISaveOrUpdateStudyHibernateFactory.class,
						SaveOrUpdateStudyHibernate.class));
		bind(ISaveOrUpdateOTUSetHibernateFactory.class).toProvider(
				FactoryProvider.newFactory(
						ISaveOrUpdateOTUSetHibernateFactory.class,
						SaveOrUpdateOTUSetHibernate.class));
		bind(ISaveOrUpdateMatrix.IFactory.class).toProvider(
				FactoryProvider.newFactory(ISaveOrUpdateMatrix.IFactory.class,
						SaveOrUpdateMatrix.class));
		bind(ISaveOrUpdateTreeSetHibernateFactory.class).toProvider(
				FactoryProvider.newFactory(
						ISaveOrUpdateTreeSetHibernateFactory.class,
						SaveOrUpdateTreeSetHibernate.class));

		bind(ISaveOrUpdateAttachmentHibernateFactory.class).toProvider(
				FactoryProvider.newFactory(
						ISaveOrUpdateAttachmentHibernateFactory.class,
						SaveOrUpdateAttachmentHibernate.class));

		bind(CharacterState.IFactory.class).toProvider(
				FactoryProvider.newFactory(CharacterState.IFactory.class,
						CharacterState.class));
	}
}
