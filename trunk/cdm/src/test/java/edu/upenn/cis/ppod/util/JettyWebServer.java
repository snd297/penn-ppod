package edu.upenn.cis.ppod.util;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sam Donnelly
 */
public class JettyWebServer implements IService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private Server server;

	public void start() throws Exception {
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
		logger.info("starting up jetty...");
		server.start();
		logger.info("...started");
	}

	public void stop() throws Exception {
		logger.info("stopping jetty...");
		server.stop();
		logger.info("...stopped");
	}
}
