package edu.upenn.cis.ppod.dao;

import org.hibernate.Session;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.model.CurrentVersion;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;

public class CurrentVersionDAO
		extends GenericHibernateDAO<CurrentVersion, Long>
		implements ICurrentVersionDAO {
	@Inject
	CurrentVersionDAO(final Session session) {
		setSession(session);
	}
}
