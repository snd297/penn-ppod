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
