package edu.upenn.cis.ppod.dao;

import org.hibernate.Session;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.model.ProteinRow;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;

class ProteinRowDAOHibernate
		extends GenericHibernateDAO<ProteinRow, Long>
		implements IProteinRowDAO {

	@Inject
	ProteinRowDAOHibernate(final Session session) {
		setSession(session);
	}

}
