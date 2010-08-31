/*
 * Copyright (C) 2009 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.services.hibernate;

import javax.ws.rs.ext.Provider;

import org.hibernate.Session;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.upenn.cis.ppod.persistence.SessionFactoryProvider;

/**
 * Attempt to commit the current transaction <em>before</em> the servlet
 * response is committed. Because we want the client to be notified if the
 * commit fails. Which doesn't happen if we commit in
 * {@link HibernateSessionPerRequestFilter}.
 * 
 * @author Sam Donnelly
 */
@Provider
@ServerInterceptor
public class HibernateCommitInterceptor implements PostProcessInterceptor {

	private static Logger logger = LoggerFactory
			.getLogger(HibernateCommitInterceptor.class);

	public void postProcess(final ServerResponse response) {
		final Session currentSession = SessionFactoryProvider
				.getSessionFactory().getCurrentSession();
		logger.info("committing transaction...");
		currentSession.getTransaction().commit();
		logger.info("committed");

		return;
	}
}
