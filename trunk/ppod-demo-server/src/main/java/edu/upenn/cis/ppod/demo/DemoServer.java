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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EventListener;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import com.google.inject.Guice;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;

import edu.upenn.cis.ppod.persistence.PersistenceModule;
import edu.upenn.cis.ppod.services.PPodServicesHibernateModule;
import edu.upenn.cis.ppod.thirdparty.util.PPodGuiceResteasyBootstrap;

public class DemoServer {

	private static Server server;

	public static void main(String[] args) throws Exception {
		server = new Server(Integer.valueOf(args[0]));

		ServletContextHandler context =
				new ServletContextHandler(
						server,
						"/",
						ServletContextHandler.SESSIONS);
		context.setResourceBase("/");
		context.setInitParameter(
				"resteasy.providers",
				"edu.upenn.cis.ppod.services.PPodExceptionMapper");
		context.addFilter(GuiceFilter.class, "/*", 0);
		context.addServlet(DefaultServlet.class, "/");
		EventListener resteasyBootstrapListener =
				new PPodGuiceResteasyBootstrap(
						Guice.createInjector(
								new PersistenceModule(),
								new PPodServicesHibernateModule(),
								new ServletModule() {
									@Override
									protected void configureServlets() {
										bind(HttpServletDispatcher.class)
												.in(Singleton.class);
										serve("/*")
												.with(HttpServletDispatcher.class);
									}
								}
								)
				);

		context.addEventListener(resteasyBootstrapListener);

		Thread monitor = new MonitorThread(Integer.valueOf(args[0]) + 1);

		monitor.start();
		server.start();
		server.join();
	}

	private static class MonitorThread extends Thread {
		private ServerSocket socket;

		public MonitorThread(int port) {
			setDaemon(true);
			setName("StopMonitor");
			try {
				socket = new ServerSocket(port, 1,
						InetAddress.getByName("127.0.0.1"));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void run() {
			System.out.println("*** running jetty 'stop' thread");
			Socket accept;
			try {
				accept = socket.accept();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(accept.getInputStream()));
				reader.readLine();
				System.out.println("*** stopping jetty embedded server");
				server.stop();
				accept.close();
				socket.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
