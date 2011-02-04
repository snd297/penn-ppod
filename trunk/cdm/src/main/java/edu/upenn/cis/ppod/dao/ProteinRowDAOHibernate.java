package edu.upenn.cis.ppod.dao;

import edu.upenn.cis.ppod.model.ProteinRow;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;

class ProteinRowDAOHibernate
		extends GenericHibernateDAO<ProteinRow, Long>
		implements IProteinRowDAO {}
