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

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;
import org.hibernate.stat.SessionStatistics;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author samd
 * 
 */
public class StubSession implements Session {

	private Provider<Query> queryProvider;

	@Inject
	StubSession(final Provider<Query> queryProvider) {
		this.queryProvider = queryProvider;
	}

	public Transaction beginTransaction() throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void cancelQuery() throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void clear() {

	}

	public Connection close() throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Connection connection() throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean contains(Object object) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Criteria createCriteria(Class persistentClass) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Criteria createCriteria(String entityName) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Criteria createCriteria(Class persistentClass, String alias) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Criteria createCriteria(String entityName, String alias) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query createFilter(Object collection, String queryString)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query createQuery(String queryString) throws HibernateException {
		return queryProvider.get();
	}

	public SQLQuery createSQLQuery(String queryString)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void delete(Object object) throws HibernateException {
		return;
	}

	public void delete(String entityName, Object object)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void disableFilter(String filterName) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Connection disconnect() throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void doWork(Work work) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Filter enableFilter(String filterName) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void evict(Object object) throws HibernateException {

	}

	public void flush() throws HibernateException {

	}

	public Object get(Class clazz, Serializable id) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Object get(String entityName, Serializable id)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Object get(Class clazz, Serializable id, LockMode lockMode)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Object get(String entityName, Serializable id, LockMode lockMode)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public CacheMode getCacheMode() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public LockMode getCurrentLockMode(Object object) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Filter getEnabledFilter(String filterName) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public EntityMode getEntityMode() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String getEntityName(Object object) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public FlushMode getFlushMode() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Serializable getIdentifier(Object object) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query getNamedQuery(String queryName) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Session getSession(EntityMode entityMode) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public SessionFactory getSessionFactory() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public SessionStatistics getStatistics() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Transaction getTransaction() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean isConnected() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean isDirty() throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean isOpen() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Object load(Class theClass, Serializable id)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Object load(String entityName, Serializable id)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void load(Object object, Serializable id) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Object load(Class theClass, Serializable id, LockMode lockMode)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Object load(String entityName, Serializable id, LockMode lockMode)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void lock(Object object, LockMode lockMode)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void lock(String entityName, Object object, LockMode lockMode)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Object merge(Object object) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Object merge(String entityName, Object object)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void persist(Object object) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void persist(String entityName, Object object)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void reconnect() throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void reconnect(Connection connection) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void refresh(Object object) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void refresh(Object object, LockMode lockMode)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void replicate(Object object, ReplicationMode replicationMode)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void replicate(String entityName, Object object,
			ReplicationMode replicationMode) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Serializable save(Object object) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Serializable save(String entityName, Object object)
			throws HibernateException {
		throw new UnsupportedOperationException();
	}

	public void saveOrUpdate(Object object) throws HibernateException {

	}

	public void saveOrUpdate(String entityName, Object object)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void setCacheMode(CacheMode cacheMode) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void setFlushMode(FlushMode flushMode) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void setReadOnly(Object entity, boolean readOnly) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void update(Object object) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void update(String entityName, Object object)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean isDefaultReadOnly() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean isReadOnly(Object entityOrProxy) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void setDefaultReadOnly(boolean readOnly) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
