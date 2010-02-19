package edu.upenn.cis.ppod.util;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * @author Sam Donnelly
 */
public class JettyService implements IService {

	private Server server;

	public void start() throws Exception {
		server = new Server();
		Connector connector = new SelectChannelConnector();
		connector.setPort(8080);
		connector.setHost("127.0.0.1");
		server.addConnector(connector);

		WebAppContext wac = new WebAppContext();
		wac.setContextPath("/");
		wac.setWar("./web"); // this is path to .war OR TO expanded, existing
		// webapp; WILL FIND web.xml and parse it
		server.setHandler(wac);
		server.setStopAtShutdown(true);

		server.start();
	}

	public void stop() throws Exception {
		server.stop();
	}
}
