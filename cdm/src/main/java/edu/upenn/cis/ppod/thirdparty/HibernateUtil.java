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
package edu.upenn.cis.ppod.thirdparty;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.upenn.cis.ppod.model.PPodVersionInfoInterceptor;
import edu.upenn.cis.ppod.util.PPodCoreFactory;

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

	/**
	 * Prevent inheritance and instantiation.
	 * 
	 * @throws UnsupportedOperationException always
	 */
	private HibernateUtil() {
		throw new AssertionError("Can't instantiate a HibernateUtil");
	}

	private static Logger logger = LoggerFactory.getLogger(HibernateUtil.class);

	/**
	 * Configuration used for building and rebuilding
	 * <code>sessionFactory</code>.
	 */
	private static Configuration config;

	/** The session factory that we're wrapping. */
	private static SessionFactory sessionFactory;

	static {
		try {
			// Reads hibernate.properties, if present and
			// Build and store (either in JNDI or static variable)
			rebuildSessionFactory(configure(new AnnotationConfiguration()));
		} catch (Throwable ex) {
			// We have to catch Throwable, otherwise we will miss
			// NoClassDefFoundError and other subclasses of Error
			logger.error("Couldn't load HibernateUtil.", ex); // We break the
			// rule and log this here because we're
			// losing the
			// exception
			// otherwise in the Mesquite module - but don't know why
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static Configuration configure(AnnotationConfiguration config) {
		// Create the initial SessionFactory from the default config
		// files
		logger.debug("(Re)Initializing Hibernate");

		// Read hibernate.cfg.xml (has to be present)
		config.configure().setInterceptor(
				new PPodCoreFactory().create(PPodVersionInfoInterceptor.class));

		logger
				.debug("Hibernate initialized, call HibernateUtil.getSessionFactory()");
		return config;
	}

	public static void addResource(String resource) {
		rebuildSessionFactory(configure(new AnnotationConfiguration()
				.addResource(resource)));
	}

	/**
	 * Equivalent to {@code getCurrentSession().beginTransaction()}.
	 * 
	 * @return {@code getCurrentSession().beginTransaction()}
	 */
	public static Transaction beginTransactionInCurrentSession() {
		return getCurrentSession().beginTransaction();
	}

	/**
	 * Closes the current {@code SessionFactory} and releases all resources.
	 * <p>
	 * The only other method that can be called on HibernateUtil after this one
	 * is rebuildSessionFactory(Configuration).
	 */
	public static void closeSessionFactory() {
		logger.debug("Shutting down Hibernate");

		// Close caches and connection pools
		getSessionFactory().close();

		// Clear static variables
		sessionFactory = null;
	}

	/**
	 * Equivalent to {@code getCurrentSession().getTransaction().commit()}.
	 */
	public static void commitTransactionInCurrentSession() {
		getCurrentSession().getTransaction().commit();
	}

	/**
	 * Returns the Hibernate config that was used to build the SessionFactory.
	 * 
	 * @return Configuration
	 */
	public static Configuration getConfiguration() {
		return config;
	}

	/**
	 * Equivalent to {@code getSessionFactory().getCurrentSession()}.
	 * 
	 * @return {@code getSessionFactory().getCurrentSession()}
	 */
	public static Session getCurrentSession() {
		return getSessionFactory().getCurrentSession();
	}

	/**
	 * Returns the global {@code SessionFactory} either from a static variable
	 * or a JNDI lookup.
	 * 
	 * @return the global {@code SessionFactory}
	 * @throws IllegalStateException if a JNDI lookup is done and fails
	 */
	public static SessionFactory getSessionFactory() {
		final String sfName = config
				.getProperty(Environment.SESSION_FACTORY_NAME);
		if (sfName != null) {
			logger.debug("Looking up SessionFactory in JNDI");
			try {
				return (SessionFactory) new InitialContext().lookup(sfName);
			} catch (final NamingException ex) {
				throw new IllegalStateException(ex);
			}
		} else if (sessionFactory == null) {
			rebuildSessionFactory();
		}
		return sessionFactory;
	}

	/**
	 * Rebuild the SessionFactory with the static {@code Configuration}.
	 * <p>
	 * Note that this method should only be used with static SessionFactory
	 * management, not with JNDI or any other external registry. This method
	 * also closes the old static variable SessionFactory before, if it is still
	 * open.
	 */
	public static void rebuildSessionFactory() {
		logger.debug("Using current Configuration to rebuild SessionFactory");
		rebuildSessionFactory(config);
	}

	/**
	 * Rebuild the SessionFactory with the given Hibernate
	 * {@link AnnotationConfiguration}.
	 * <p>
	 * {@code rebuildSessionFactory(...)} does not {@code configure()} the given
	 * {@code config}, it directly calls {@code buildSessionFactory()}. This
	 * method also closes the old static variable SessionFactory before, if it
	 * is still open.
	 * 
	 * @param config the Hibernate {@link AnnotationConfiguration}.
	 */
	public static void rebuildSessionFactory(final Configuration config) {
		logger.debug("Rebuilding the SessionFactory from given Configuration");
		if ((sessionFactory != null) && !sessionFactory.isClosed()) {
			sessionFactory.close();
		}
		if (config.getProperty(Environment.SESSION_FACTORY_NAME) != null) {
			logger.debug("Managing SessionFactory in JNDI");
			config.buildSessionFactory();
		} else {
			logger.debug("Holding SessionFactory in static variable");
			sessionFactory = config.buildSessionFactory();
		}
		HibernateUtil.config = config;
		logger
				.debug("url: {}", getConfiguration().getProperty(
						Environment.URL));

	}

}
