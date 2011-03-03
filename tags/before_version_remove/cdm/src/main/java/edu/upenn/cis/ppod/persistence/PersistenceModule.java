package edu.upenn.cis.ppod.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;


public class PersistenceModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(SessionFactory.class)
				.toProvider(SessionFactoryProvider.class)
				.asEagerSingleton();
	}

	@Provides
	Session provideSession(final SessionFactory sf) {
		return sf.getCurrentSession();
	}

}
