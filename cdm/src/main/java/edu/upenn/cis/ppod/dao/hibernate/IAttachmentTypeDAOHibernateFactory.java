package edu.upenn.cis.ppod.dao.hibernate;

import org.hibernate.Session;

import edu.upenn.cis.ppod.dao.IAttachmentTypeDAO;

/**
 * @author Sam Donnelly
 * 
 */
public interface IAttachmentTypeDAOHibernateFactory {
	IAttachmentTypeDAO create(Session session);
}
