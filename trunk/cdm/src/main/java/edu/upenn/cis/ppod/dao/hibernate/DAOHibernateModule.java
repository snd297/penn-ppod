package edu.upenn.cis.ppod.dao.hibernate;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * @author Sam Donnelly
 * 
 */
public class DAOHibernateModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(IAttachmentNamespaceDAOHibernateFactory.class).toProvider(
				FactoryProvider.newFactory(
						IAttachmentNamespaceDAOHibernateFactory.class,
						AttachmentNamespaceDAOHibernate.class));

		bind(IAttachmentTypeDAOHibernateFactory.class).toProvider(
				FactoryProvider.newFactory(
						IAttachmentTypeDAOHibernateFactory.class,
						AttachmentTypeDAOHibernate.class));

	}

}
