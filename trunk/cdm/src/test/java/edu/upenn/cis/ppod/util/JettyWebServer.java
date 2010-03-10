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
package edu.upenn.cis.ppod.util;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Sam Donnelly
 */
public class JettyWebServer implements IService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private final Server server;

	@Inject
	JettyWebServer(final Server server) {
		this.server = server;
	}

	public void start() throws Exception {
		String jetty_home = System.getProperty("jetty.home", "..");

		final Connector connector = new SelectChannelConnector();
		connector.setPort(8082);
		connector.setHost("127.0.0.1");
		server.addConnector(connector);

		final WebAppContext wac = new WebAppContext();
		wac.setContextPath("/ppod-services");
		wac.setWar("../ppod/services/src/main/webapp"); // this is path to .war
		// OR TO
		// expanded,
		// existing webapp; WILL FIND web.xml and
		// parse it
		server.setHandler(wac);
		server.setStopAtShutdown(true);
		logger.info("starting up jetty...");
		server.start();
		logger.info("...started");
	}

	public void stop() throws Exception {
		// Nothing to do here: stopping will happen because of server.setStopAtShutdown(true)
	}


}
