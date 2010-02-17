/*
 * Copyright (c) 2005, Christian Bauer <christian@hibernate.org>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the original author nor the names of contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES of MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT of
 * SUBSTITUTE GOODS OR SERVICES; LOSS of USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY of LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT of THE USE of THIS SOFTWARE, EVEN IF ADVISED of
 * THE POSSIBILITY of SUCH DAMAGE.
 */
package edu.upenn.cis.ppod.thirdparty.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;

/**
 * Basic Hibernate helper class for Hibernate config and startup.
 * <p>
 * Uses a static initializer to read startup options and initialize
 * <tt>Configuration</tt> and <tt>SessionFactory</tt>.
 * <p>
 * This class also tries to figure out if JNDI binding of the
 * <tt>SessionFactory</tt> is used, otherwise it falls back to a global static
 * variable (Singleton). If you use this helper class to obtain a
 * <tt>SessionFactory</tt> in your code, you are shielded from these deployment
 * differences.
 * <p>
 * Another advantage of this class is access to the <tt>Configuration</tt>
 * object that was used to build the current <tt>SessionFactory</tt>. You can
 * access mapping metadata programmatically with this API, and even change it
 * and rebuild the <tt>SessionFactory</tt>.
 * <p>
 * Note: This class supports annotations if you replace the line that creates a
 * Configuration object.
 * <p>
 * Note: This class supports only one data store. Support for several
 * <tt>SessionFactory</tt> instances can be easily added (through a static
 * <tt>Map</tt>, for example). You could then lookup a <tt>SessionFactory</tt>
 * by its name.
 * 
 * @author Christian Bauer
 */
public class HibernateUtil {

	private static Logger logger = org.slf4j.LoggerFactory
			.getLogger(HibernateUtil.class);

	private static Configuration configuration;

	private static SessionFactory sessionFactory;

	static {
		// Create the initial SessionFactory from the default configuration
		// files
		try {
			logger.debug("Initializing Hibernate");

			// Reads hibernate.properties, if present
			configuration = new AnnotationConfiguration();

			// Read hibernate.cfg.xml (has to be present)
			configuration.configure();

// final PPodVersionInfoInterceptor pPodVersionInfoInterceptor = new
			// PPodCoreFactory()
			// .create(PPodVersionInfoInterceptor.class);

// configuration.setInterceptor(pPodVersionInfoInterceptor);

			// Build and store (either in JNDI or static variable)
			rebuildSessionFactory(configuration);

// pPodVersionInfoInterceptor.setSessionFactory(getSessionFactory());

			logger
					.debug("Hibernate initialized, call HibernateUtil.getSessionFactory()");
		} catch (final Throwable ex) {
			// We have to catch Throwable, otherwise we will miss
			// NoClassDefFoundError and other subclasses of Error
			// Generally speaking, we only log exceptions at the point of
			// handling, but
			// we sometimes lose this exception otherwise for unknown reasons
			logger.error("Building SessionFactory failed.", ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	/**
	 * Returns the Hibernate configuration that was used to build the
	 * SessionFactory.
	 * 
	 * @return Configuration
	 */
	public static Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Returns the global SessionFactory either from a static variable or a JNDI
	 * lookup.
	 * 
	 * @return SessionFactory
	 */
	public static SessionFactory getSessionFactory() {
		final String sfName = configuration
				.getProperty(Environment.SESSION_FACTORY_NAME);
		if (sfName != null) {
			logger.debug("Looking up SessionFactory in JNDI");
			try {
				return (SessionFactory) new InitialContext().lookup(sfName);
			} catch (final NamingException ex) {
				throw new RuntimeException(ex);
			}
		} else if (sessionFactory == null) {
			rebuildSessionFactory();
		}
		return sessionFactory;
	}

	/**
	 * Rebuild the SessionFactory with the static Configuration.
	 * <p>
	 * Note that this method should only be used with static SessionFactory
	 * management, not with JNDI or any other external registry. This method
	 * also closes the old static variable SessionFactory before, if it is still
	 * open.
	 */
	public static void rebuildSessionFactory() {
		logger.debug("Using current Configuration to rebuild SessionFactory");
		rebuildSessionFactory(configuration);
	}

	/**
	 * Rebuild the SessionFactory with the given Hibernate Configuration.
	 * <p>
	 * HibernateUtil does not configure() the given Configuration object, it
	 * directly calls buildSessionFactory(). This method also closes the old
	 * static variable SessionFactory before, if it is still open.
	 * 
	 * @param cfg
	 */
	public static void rebuildSessionFactory(final Configuration cfg) {
		logger.debug("Rebuilding the SessionFactory from given Configuration");
		if (sessionFactory != null && !sessionFactory.isClosed()) {
			sessionFactory.close();
		}
		if (cfg.getProperty(Environment.SESSION_FACTORY_NAME) != null) {
			logger.debug("Managing SessionFactory in JNDI");
			cfg.buildSessionFactory();
		} else {
			logger.debug("Holding SessionFactory in static variable");
			sessionFactory = cfg.buildSessionFactory();
		}
		configuration = cfg;
	}

	/**
	 * Closes the current SessionFactory and releases all resources.
	 * <p>
	 * The only other method that can be called on HibernateUtil after this one
	 * is rebuildSessionFactory(Configuration).
	 */
	public static void shutdown() {
		logger.debug("Shutting down Hibernate");
		// Close caches and connection pools
		getSessionFactory().close();

		// Clear static variables
		sessionFactory = null;
	}

	/**
	 * Prevent inheritance and instantiation.
	 * 
	 * @throws UnsupportedOperationException always
	 */
	private HibernateUtil() {
		throw new AssertionError("Can't instantiate a HibernateUtil");
	}

}
