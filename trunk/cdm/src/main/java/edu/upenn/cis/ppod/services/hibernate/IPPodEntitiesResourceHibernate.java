package edu.upenn.cis.ppod.services.hibernate;

import org.hibernate.Session;

import edu.upenn.cis.ppod.services.IPPodEntitiesResource;

/**
 * An {@link IPPodEntitiesResource} that uses a {@link Session} for talking to
 * the database.
 * 
 * @author Sam Donnelly
 */
public interface IPPodEntitiesResourceHibernate extends IPPodEntitiesResource {
	IPPodEntitiesResourceHibernate setSession(Session session);
}
