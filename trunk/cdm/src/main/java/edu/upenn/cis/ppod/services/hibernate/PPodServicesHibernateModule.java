package edu.upenn.cis.ppod.services.hibernate;

import com.google.inject.AbstractModule;

import edu.upenn.cis.ppod.services.IStudyResource;

/**
 * @author Sam Donnelly
 * 
 */
public class PPodServicesHibernateModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IStudyResource.class).to(StudyResourceHibernate.class);
	}
}
