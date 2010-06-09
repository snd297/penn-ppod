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

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST, TestGroupDefs.IN_DEVELOPMENT }, dependsOnGroups = TestGroupDefs.INIT)
public class PPodEntitiesResourceTest {

	// private ServletTester tester;
	private Server server;

// @Inject
// private IPPodEntitiesResource pPodEntitiesResource;

	//@BeforeSuite
	public void beforeSuite() throws Exception {
		String jetty_home = System.getProperty("jetty.home", "..");
		System.out.println("jetty_home: " + jetty_home);
		System.out.println("properties: " + System.getProperties());
		server = new Server();
		Connector connector = new SelectChannelConnector();
		connector.setPort(8080);
		connector.setHost("127.0.0.1");
		server.addConnector(connector);

		final WebAppContext wac = new WebAppContext();
		wac.setContextPath("/ppod-services");
		wac.setWar("../services/src/main/webapp"); // this is path to .war OR TO
		// expanded,
		// existing webapp; WILL FIND web.xml and
		// parse it
		server.setHandler(wac);
		server.setStopAtShutdown(true);

		server.start();

		while (true) {
			Thread.sleep(1000);
		}
// server = new Server(8080);
// server.setContextPath("");
// tester
// .addServlet(
// org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.class,
// "/ppod-services");
// tester
// .setAttribute(
// "resteasy.guice.modules",
// "edu.upenn.cis.ppod.services.PPodServicesModule, "
// + "edu.upenn.cis.ppod.util.PPodCoreModule, "
// + "edu.upenn.cis.ppod.thirdparty.injectslf4j.InjectSlf4jModule");
// tester
// .setAttribute("resteasy.providers",
// "edu.upenn.cis.ppod.services.hibernate.HibernateCommitInterceptor");
// tester
// .addFilter(
// edu.upenn.cis.ppod.thirdparty.util.HibernateSessionPerRequestFilter.class,
// "/*", 0);
// tester
// .addEventListener(new GuiceResteasyBootstrapServletContextListener());

// final TJWSEmbeddedJaxrsServer tjws = new TJWSEmbeddedJaxrsServer();
// tjws.setPort(8081);
//
// tjws.getDeployment().getActualResourceClasses().add(
// IPPodEntitiesResource.class);
// System.out.println("started the server......................");
// tjws.start();
	}

	public void uploadProject() throws Exception {

	// final String response = tester.getResponses("GET");
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
