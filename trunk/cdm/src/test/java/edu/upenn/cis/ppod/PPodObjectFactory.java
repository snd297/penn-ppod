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

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.inject.Guice;
import com.google.inject.assistedinject.FactoryProvider;
import com.google.inject.util.Modules;

import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateMatrixFactory;
import edu.upenn.cis.ppod.saveorupdate.SaveOrUpdateCharacterStateMatrix;
import edu.upenn.cis.ppod.saveorupdate.TestMergeAttachment;
import edu.upenn.cis.ppod.services.IPPodEntitiesResource;
import edu.upenn.cis.ppod.services.hibernate.PPodEntitiesResourceHibernate;
import edu.upenn.cis.ppod.thirdparty.injectslf4j.InjectSlf4jModule;
import edu.upenn.cis.ppod.util.GuiceObjectFactory;
import edu.upenn.cis.ppod.util.PPodCoreModule;
import edu.upenn.cis.ppod.util.StubNewVersionInfo;
import edu.upenn.cis.ppod.util.StubQuery;
import edu.upenn.cis.ppod.util.StubSession;

/**
 * @author Sam Donnelly
 */
public class PPodObjectFactory extends GuiceObjectFactory {

	PPodObjectFactory() {
		setInjector(Guice.createInjector(Modules.override(new PPodCoreModule())
				.with(this), new InjectSlf4jModule()));
	}

	@Override
	protected void configure() {
		bind(ISaveOrUpdateMatrixFactory.class).toProvider(
				FactoryProvider.newFactory(ISaveOrUpdateMatrixFactory.class,
						SaveOrUpdateCharacterStateMatrix.class));
		bind(TestMergeAttachment.class);

		bind(IPPodEntitiesResource.class).to(
				PPodEntitiesResourceHibernate.class);
// final org.hibernate.classic.Session session = HibernateUtil
// .getSessionFactory().openSession();
// ManagedSessionContext.bind(session);
// bind(Session.class).toInstance(
// HibernateUtil.getSessionFactory().getCurrentSession());
		bind(Session.class).to(StubSession.class);
		bind(Query.class).to(StubQuery.class);
		bind(INewPPodVersionInfo.class).to(StubNewVersionInfo.class);
	}
}
