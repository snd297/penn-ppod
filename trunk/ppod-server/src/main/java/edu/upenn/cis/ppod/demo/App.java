package edu.upenn.cis.ppod.demo;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.inject.servlet.GuiceFilter;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		ServletContextHandler context =
				new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		// context.addLifeCycleListener(new
		// PPodGuiceResteasyBootstrapServletContextListener());
		context.addFilter(GuiceFilter.class, "/*", 0);

		server.setHandler(context);

		server.start();
		server.join();
	}
}
