package edu.upenn.cis.ppod.dao.hibernate;

import org.hibernate.Session;

import edu.upenn.cis.ppod.dao.IAttachmentNamespaceDAO;

public interface IAttachmentNamespaceDAOHibernateFactory {
	IAttachmentNamespaceDAO create(Session session);
}