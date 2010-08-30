package edu.upenn.cis.ppod.services;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import javax.ws.rs.core.Application;

import edu.upenn.cis.ppod.services.hibernate.StudyResourceHibernate;

public class PPodApplication extends Application {
	private Set<Object> singletons = newHashSet();
	private Set<Class<?>> classes = newHashSet();

	public PPodApplication() {
		classes.add(StudyResourceHibernate.class);
	}

	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
