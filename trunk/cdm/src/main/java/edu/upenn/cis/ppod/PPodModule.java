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
import com.google.inject.servlet.RequestScoped;

import edu.upenn.cis.ppod.createorupdate.CreateOrUpdateModule;
import edu.upenn.cis.ppod.dao.hibernate.DAOHibernateModule;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.model.ModelModule;
import edu.upenn.cis.ppod.model.NewVersionInfoDB;
import edu.upenn.cis.ppod.persistence.PersistenceModule;
import edu.upenn.cis.ppod.services.StringPair;
import edu.upenn.cis.ppod.util.IPair;
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

		bind(StringPair.IFactory.class).toProvider(
				FactoryProvider.newFactory(
						StringPair.IFactory.class,
						StringPair.class));

		bind(INewVersionInfo.class).to(NewVersionInfoDB.class).in(
				RequestScoped.class);

		install(new ModelModule());
		install(new DAOHibernateModule());
		install(new CreateOrUpdateModule());
		install(new PersistenceModule());
	}
}
