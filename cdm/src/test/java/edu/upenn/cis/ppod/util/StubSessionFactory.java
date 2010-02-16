package edu.upenn.cis.ppod.util;

import org.hibernate.Query;

import org.hibernate.Session;

import com.google.inject.Provider;

/**
 * @author Sam Donnelly
 */
public class StubSessionFactory implements ISessionFactory {

	private Provider<Query> stubQueryProvider;

	StubSessionFactory(final Provider<Query> stubyQueryProvider) {
		this.stubQueryProvider = stubQueryProvider;
	}

	public Session create() {
		return new StubSession(stubQueryProvider);
	}
}
