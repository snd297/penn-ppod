package edu.upenn.cis.ppod.modelinterfaces;

import org.hibernate.Session;

/**
 * A {@code INewPPodVersionInfo} that needs a Hibernate {@code Session}.
 * 
 * @author Sam Donnelly
 */
public interface INewPPodVersionInfoHibernate extends INewPPodVersionInfo {
	static interface IFactory {
		INewPPodVersionInfoHibernate create(Session session);
	}
}
