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
import com.google.inject.util.Modules;

import edu.upenn.cis.ppod.services.hibernate.PPodServicesHibernateModule;
import edu.upenn.cis.ppod.thirdparty.injectslf4j.InjectSlf4jModule;
import edu.upenn.cis.ppod.util.GuiceObjectFactory;

/**
 * @author Sam Donnelly
 */
public class PPodObjectFactory extends GuiceObjectFactory {

	private static final long serialVersionUID = 1L;

	PPodObjectFactory() {
		setInjector(Guice.createInjector(Modules.override(new PPodModule(),
				new PPodServicesHibernateModule()).with(new TestModule()),
				new InjectSlf4jModule()));
	}

	@Override
	protected void configure() {}
}
