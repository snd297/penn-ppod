package edu.upenn.cis.ppod.dao;

import org.hibernate.Session;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.model.CurrentVersion;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;

public class CurrentVersionDAOHibernate
		extends GenericHibernateDAO<CurrentVersion, Long>
		implements ICurrentVersionDAO {
	@Inject
	CurrentVersionDAOHibernate(final Session session) {
		setSession(session);
	}
}
