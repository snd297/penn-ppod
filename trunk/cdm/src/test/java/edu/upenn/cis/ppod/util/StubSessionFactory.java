/*
 * Copyright (C) 2010 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.util;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.Reference;

import org.hibernate.Cache;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;

/**
 * @author Sam Donnelly
 */
public class StubSessionFactory implements SessionFactory {

	public Reference getReference() throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public org.hibernate.classic.Session openSession()
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public org.hibernate.classic.Session openSession(Interceptor interceptor)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public org.hibernate.classic.Session openSession(Connection connection) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public org.hibernate.classic.Session openSession(Connection connection,
			Interceptor interceptor) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public org.hibernate.classic.Session getCurrentSession()
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public StatelessSession openStatelessSession() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public StatelessSession openStatelessSession(Connection connection) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public ClassMetadata getClassMetadata(Class entityClass) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public ClassMetadata getClassMetadata(String entityName) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public CollectionMetadata getCollectionMetadata(String roleName) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Map getAllClassMetadata() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Map getAllCollectionMetadata() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Statistics getStatistics() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void close() throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean isClosed() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Cache getCache() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void evict(Class persistentClass) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void evict(Class persistentClass, Serializable id)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void evictEntity(String entityName) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void evictEntity(String entityName, Serializable id)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void evictCollection(String roleName) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void evictCollection(String roleName, Serializable id)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void evictQueries(String cacheRegion) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void evictQueries() throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Set getDefinedFilterNames() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public FilterDefinition getFilterDefinition(String filterName)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean containsFetchProfileDefinition(String name) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
