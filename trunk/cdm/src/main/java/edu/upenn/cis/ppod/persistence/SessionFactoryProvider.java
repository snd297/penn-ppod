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
package edu.upenn.cis.ppod.persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;

final class SessionFactoryProvider implements Provider<SessionFactory> {

	private static Logger logger = LoggerFactory
			.getLogger(SessionFactoryProvider.class);

	public SessionFactory get() {
		logger.debug("building session factory...");
		final Configuration cfg = new Configuration();
		cfg.setNamingStrategy(new ImprovedNamingStrategy());

		// Read hibernate.cfg.xml (has to be present)
		cfg.configure();
		final SessionFactory sf = cfg.buildSessionFactory();
		logger.debug("...done");
		return sf;
	}

}
