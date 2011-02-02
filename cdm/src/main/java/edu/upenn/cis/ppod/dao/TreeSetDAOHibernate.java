package edu.upenn.cis.ppod.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import org.hibernate.Session;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;

final class TreeSetDAOHibernate
		extends GenericHibernateDAO<TreeSet, Long>
		implements ITreeSetDAO {

	@Inject
	TreeSetDAOHibernate(final Session session) {
		checkNotNull(session);
		setSession(session);
	}
}
