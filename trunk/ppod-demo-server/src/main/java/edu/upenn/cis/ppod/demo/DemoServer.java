/*
 * Copyright (C) 2011 Trustees of the University of Pennsylvania
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
