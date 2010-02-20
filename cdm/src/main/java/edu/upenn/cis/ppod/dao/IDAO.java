/*
 * Copyright (C) 2009 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * A DAO interface.
 * <p>
 * From <a
 * href="http://www.hibernate.org/328.html">http://www.hibernate.org/328.
 * html</a>.
 * 
 * @author Sam Donnelly, from <a
 *         href="http://www.hibernate.org/328.html">http://
 *         www.hibernate.org/328.html</a>.
 * 
 * @param <T> type of the transfer object.
 * @param <ID> the type of the persistence id.
 */
public interface IDAO<T, ID extends Serializable> {

	/**
	 * Retrieve all <code>T</code>s.
	 * 
	 * @return all persisted <code>T</code>s
	 */
	List<T> findAll();

	/**
	 * Do a find by example with the given example instance and properties to
	 * exclude.
	 * 
	 * @param exampleInstance the example instance
	 * @param excludeProperty properties to exclude in the find by example
	 * @return the results of the search
	 */
	List<T> findByExample(T exampleInstance, String... excludeProperty);

	/**
	 * Given a persistence id <code>id</code>, retrieve the corresponding
	 * <code>T</code>. If <code>id</code> is <code>null</code>, returns
	 * <code>null</code>.
	 * 
	 * @param id see description
	 * @param lock use an upgrade lock. Objects loaded in this lock mode are
	 *            materialized using an SQL <tt>select ... for update</tt>.
	 * @return the retrieved object, or <code>null</code> if there is no such
	 *         object or if <code>id</code> is <code>null</code>
	 */
	T get(ID id, boolean lock);

	/**
	 * Return the identifier value of the given entity as associated with this
	 * <code>IDAO</code>'s session. An exception is thrown if the given entity
	 * instance is transient or detached in relation to the session.
	 * 
	 * @param o a persistent instance
	 * @return the identifier
	 */
	Serializable getIdentifier(Object o);

	/**
	 * Make the given entity transient. That is, delete <code>entity</code>.
	 * 
	 * @param entity to be made transient
	 * 
	 * @return {@code entity}
	 */
	T delete(T entity);

	/**
	 * Save or update <code>entity</code>.
	 * 
	 * @param entity entity object
	 * @return <code>entity</code>
	 */
	T saveOrUpdate(T entity);

	T evict(final T entity);

	void evictEntities(final Collection<? extends T> entities);

	void flush();

	void initialize(T entity);

	String getEntityName(T entity);
	
	String getEntityName(Class<? extends T> entityClass);
}