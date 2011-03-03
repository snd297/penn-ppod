package edu.upenn.cis.ppod.dao;

import org.hibernate.Session;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.model.DnaRow;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;

class DnaRowDAOHibernate
		extends GenericHibernateDAO<DnaRow, Long>
		implements IDnaRowDAO {

	@Inject
	DnaRowDAOHibernate(final Session session) {
		setSession(session);
	}
}
