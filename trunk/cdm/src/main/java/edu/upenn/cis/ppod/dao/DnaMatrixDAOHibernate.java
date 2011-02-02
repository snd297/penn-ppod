package edu.upenn.cis.ppod.dao;

import org.hibernate.Session;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.model.DnaMatrix;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;

final class DnaMatrixDAOHibernate
		extends GenericHibernateDAO<DnaMatrix, Long>
		implements IDnaMatrixDAO {

	@Inject
	DnaMatrixDAOHibernate(final Session session) {
		setSession(session);
	}
}
