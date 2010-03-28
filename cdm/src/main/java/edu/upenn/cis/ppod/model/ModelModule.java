package edu.upenn.cis.ppod.model;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;

import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfoHibernate;

/**
 * @author Sam Donnelly
 * 
 */
public class ModelModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(INewPPodVersionInfoHibernate.IFactory.class).toProvider(
				FactoryProvider.newFactory(
						INewPPodVersionInfoHibernate.IFactory.class,
						NewPPodVersionInfoHibernate.class));

	}

}
