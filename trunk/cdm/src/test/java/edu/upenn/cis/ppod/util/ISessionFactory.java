package edu.upenn.cis.ppod.util;

import org.hibernate.Session;

/**
 * @author Sam Donnelly
 */
public interface ISessionFactory {
	Session create();
}
