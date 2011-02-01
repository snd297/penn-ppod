package edu.upenn.cis.ppod.dao;

import edu.upenn.cis.ppod.model.DnaRow;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;

public class DnaRowDAOHibernate
		extends GenericHibernateDAO<DnaRow, Long>
		implements IDnaRowDao {
	
}
