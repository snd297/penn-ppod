/*******************************************************************************
 * Copyright 2010 Trustees of the University of Pennsylvania
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package edu.upenn.cis.ppod.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class SessionFactoryContextListener implements
		ServletContextListener {

	private final SessionFactory sessionFactory;

	private Logger logger = LoggerFactory
			.getLogger(SessionFactoryContextListener.class);

	@Inject
	SessionFactoryContextListener(final SessionFactory sessionFactory) {
		logger.debug("starting up session factory...");
		this.sessionFactory = sessionFactory;
		logger.debug("done");
	}

	public void contextDestroyed(final ServletContextEvent arg0) {
		logger.debug("shutting down hibernate...");
		sessionFactory.close();
		logger.debug("done");
	}

	public void contextInitialized(final ServletContextEvent arg0) {
		// Note the session factory will actually be started when this context
		// listener is created.
	}
}
