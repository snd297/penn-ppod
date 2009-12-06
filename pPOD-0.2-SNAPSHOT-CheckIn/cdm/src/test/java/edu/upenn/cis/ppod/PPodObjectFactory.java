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

import com.google.inject.Guice;
import com.google.inject.assistedinject.FactoryProvider;
import com.google.inject.util.Modules;

import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateMatrix;
import edu.upenn.cis.ppod.saveorupdate.SaveOrUpdateMatrix;
import edu.upenn.cis.ppod.saveorupdate.TestSaveOrUpdateAttachment;
import edu.upenn.cis.ppod.saveorupdate.hibernate.ISaveOrUpdateAttachmentHibernateFactory;
import edu.upenn.cis.ppod.services.IPPodEntitiesResource;
import edu.upenn.cis.ppod.services.hibernate.PPodEntitiesResourceHibernate;
import edu.upenn.cis.ppod.util.GuiceObjectFactory;
import edu.upenn.cis.ppod.util.PPodCoreModule;

/**
 * @author Sam Donnelly
 */
public class PPodObjectFactory extends GuiceObjectFactory {

	PPodObjectFactory() {
		setInjector(Guice.createInjector(Modules.override(new PPodCoreModule())
				.with(this)));
	}

	@Override
	protected void configure() {
		bind(ISaveOrUpdateMatrix.IFactory.class).toProvider(
				FactoryProvider.newFactory(ISaveOrUpdateMatrix.IFactory.class,
						SaveOrUpdateMatrix.class));
		bind(ISaveOrUpdateAttachmentHibernateFactory.class).toProvider(
				FactoryProvider.newFactory(
						ISaveOrUpdateAttachmentHibernateFactory.class,
						TestSaveOrUpdateAttachment.class));

		bind(IPPodEntitiesResource.class).to(
				PPodEntitiesResourceHibernate.class);
	}
}
