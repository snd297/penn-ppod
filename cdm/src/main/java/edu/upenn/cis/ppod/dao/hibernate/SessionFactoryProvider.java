package edu.upenn.cis.ppod.dao.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.ImprovedNamingStrategy;

import com.google.inject.Provider;

public class SessionFactoryProvider implements Provider<SessionFactory> {

	private static SessionFactory sf;

	/**
	 * Don't use this if not necessary. This is a compromise to get the session
	 * factory into
	 * {@link edu.upenn.cis.ppod.services.hibernate.HibernateCommitInterceptor}.
	 * 
	 * @return the singleton session factory
	 */
	public static SessionFactory getSessionFactory() {
		return sf;
	}

	SessionFactoryProvider() {}

	public SessionFactory get() {
		final Configuration cfg = new AnnotationConfiguration();
		cfg.setNamingStrategy(new ImprovedNamingStrategy());

		// Read hibernate.cfg.xml (has to be present)
		cfg.configure();
		final SessionFactory sf = cfg.buildSessionFactory();
		SessionFactoryProvider.sf = sf;
		return sf;
	}

}
