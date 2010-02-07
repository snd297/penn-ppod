package edu.upenn.cis.ppod.dao.hibernate;

import edu.upenn.cis.ppod.dao.IDNACharacterDAO;
import edu.upenn.cis.ppod.model.DNACharacter;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;

/**
 * @author Sam Donnelly
 */
public class DNACharacterDAOHibernate extends GenericHibernateDAO<DNACharacter, Long>
		implements IDNACharacterDAO {

}
