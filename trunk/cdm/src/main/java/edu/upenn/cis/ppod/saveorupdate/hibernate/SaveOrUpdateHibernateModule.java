package edu.upenn.cis.ppod.saveorupdate.hibernate;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * @author Sam Donnelly
 *
 */
public class SaveOrUpdateHibernateModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ISaveOrUpdateStudyHibernateFactory.class).toProvider(
				FactoryProvider.newFactory(
						ISaveOrUpdateStudyHibernateFactory.class,
						SaveOrUpdateStudyHibernate.class));

	}

}
