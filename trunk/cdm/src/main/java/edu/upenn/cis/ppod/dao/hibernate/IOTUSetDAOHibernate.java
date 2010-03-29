package edu.upenn.cis.ppod.dao.hibernate;

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.dao.IOTUSetDAO;
import edu.upenn.cis.ppod.dao.hibernate.HibernateDAOFactory.OTUSetDAOHibernate;
import edu.upenn.cis.ppod.model.OTUSet;

/**
 * @author Sam Donnelly
 */
@ImplementedBy(OTUSetDAOHibernate.class)
public interface IOTUSetDAOHibernate extends IOTUSetDAO,
IDAOHibernate<OTUSet, Long> {

}
