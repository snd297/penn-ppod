package edu.upenn.cis.ppod.util;

import org.hibernate.Session;

import edu.upenn.cis.ppod.thirdparty.util.HibernateUtil;

/**
 * @author Sam Donnelly
 */
class HibernateSessionFactory implements ISessionFactory {

	public Session create() {
		return HibernateUtil.getSessionFactory().getCurrentSession();
	}

}
