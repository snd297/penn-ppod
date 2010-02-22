/*
 * Copyright (C) 2010 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.services;

import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.mortbay.jetty.testing.ServletTester;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST, TestGroupDefs.IN_DEVELOPMENT }, dependsOnGroups = TestGroupDefs.INIT)
public class PPodEntitiesResourceTest {

	private ServletTester tester;

	@Inject
	private IPPodEntitiesResource pPodEntitiesResource;

	@BeforeSuite
	public void beforeSuite() {
		tester = new ServletTester();
		tester.setContextPath("");
		tester
				.addServlet(
						org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.class,
						"/ppod-services");
		tester
				.setAttribute(
						"resteasy.guice.modules",
						"edu.upenn.cis.ppod.services.PPodServicesModule, "
								+ "edu.upenn.cis.ppod.util.PPodCoreModule, "
								+ "edu.upenn.cis.ppod.thirdparty.injectslf4j.InjectSlf4jModule");
		tester
				.setAttribute("resteasy.providers",
						"edu.upenn.cis.ppod.services.hibernate.HibernateCommitInterceptor");
		tester
				.addFilter(
						edu.upenn.cis.ppod.thirdparty.util.HibernateSessionPerRequestFilter.class,
						"/*", 0);
		tester
				.addEventListener(new GuiceResteasyBootstrapServletContextListener());

	}

	public void uploadProject() throws Exception {
		final String response = tester.getResponses("GET");
	}

// public void getEntitiesByHqlQuery() {
// ManagedSessionContext.bind(HibernateUtil.getSessionFactory()
// .openSession());
// final IOTUSetCentricEntities entities = pPodEntitiesResource
// .getEntitiesByHqlQuery("from CharacterStateMatrix m join fetch m.otuSet os join fetch os.otus o where o.label='Sus'");
// System.out.println(entities);
// ManagedSessionContext.unbind(HibernateUtil.getSessionFactory());
// }
}
