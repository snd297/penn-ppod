package edu.upenn.cis.ppod.demo;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.inject.servlet.GuiceFilter;

public class DemoServer {

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		ServletContextHandler context =
				new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.setInitParameter(
				"resteasy.providers",
				"edu.upenn.cis.ppod.services.PPodExceptionMapper");
		context.addFilter(GuiceFilter.class, "/*", 0);

		context.addEventListener(new PPodGuiceResteasyBootstrapServletContextListener());

		server.setHandler(context);

		server.start();
		server.join();
	}
}
