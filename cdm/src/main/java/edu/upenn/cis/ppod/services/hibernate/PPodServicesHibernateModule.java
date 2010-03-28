package edu.upenn.cis.ppod.services.hibernate;

import com.google.inject.AbstractModule;

import edu.upenn.cis.ppod.services.IPPodGreetingResource;
import edu.upenn.cis.ppod.services.IStudyResource;
import edu.upenn.cis.ppod.services.PPodGreetingService;

/**
 * @author Sam Donnelly
 * 
 */
public class PPodServicesHibernateModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IPPodGreetingResource.class).to(PPodGreetingService.class);
		bind(IStudyResource.class).to(StudyResourceHibernate.class);
	}
}
