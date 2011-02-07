package edu.upenn.cis.ppod.dao;

import org.hibernate.Session;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.model.ProteinMatrix;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;

class ProteinMatrixDAOHibernate extends GenericHibernateDAO<ProteinMatrix, Long>
		implements IProteinMatrixDAO {
	@Inject
	ProteinMatrixDAOHibernate(final Session session) {
		setSession(session);
	}

}
