package edu.upenn.cis.ppod.dao.hibernate;

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.dao.IObjectWithLongIdDAO;

/**
 * @author Sam Donnelly
 */
@ImplementedBy(ObjectWithLongIdDAOHibernate.class)
public interface IObjectWithLongIdDAOHibernate extends IObjectWithLongIdDAO,
		IDAOHibernate<Object, Long> {

}
