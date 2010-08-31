package edu.upenn.cis.ppod.persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.ImprovedNamingStrategy;

import com.google.inject.Provider;

public class SessionFactoryProvider implements Provider<SessionFactory> {

	SessionFactoryProvider() {}

	public SessionFactory get() {
		final Configuration cfg = new AnnotationConfiguration();
		cfg.setNamingStrategy(new ImprovedNamingStrategy());

		// Read hibernate.cfg.xml (has to be present)
		cfg.configure();
		final SessionFactory sf = cfg.buildSessionFactory();
		return sf;
	}

}
