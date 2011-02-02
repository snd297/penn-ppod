package edu.upenn.cis.ppod.dao;

import org.hibernate.Session;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.model.DnaSequenceSet;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;

final class DnaSequenceSetDAOHibernate
		extends GenericHibernateDAO<DnaSequenceSet, Long>
		implements IDnaSequenceSetDAO {
	@Inject
	DnaSequenceSetDAOHibernate(final Session session) {
		setSession(session);
	}
}
