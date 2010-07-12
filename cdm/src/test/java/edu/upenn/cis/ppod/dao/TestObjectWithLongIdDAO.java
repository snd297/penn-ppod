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
package edu.upenn.cis.ppod.dao;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static edu.upenn.cis.ppod.util.CollectionsUtil.nullFillAndSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import edu.upenn.cis.ppod.dao.hibernate.IObjectWithLongIdDAOHibernate;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.StandardRow;

/**
 * A {@code IObjectWithLongIdDAO} that stores the entities that operations were
 * called on. So you can, for example, get a list of all of the entities that
 * {@code IObjectWithLongIdDAOHibernate.delete(...)} was called on.
 * <p>
 * This is only implemented for {@code delete(...)} so far.
 * 
 * @author Sam Donnelly
 */
public class TestObjectWithLongIdDAO implements IObjectWithLongIdDAOHibernate {

	private List<Object> deletedEntities = newArrayList();

	public List<Object> getTransientEntities() {
		return deletedEntities;
	}

	/**
	 * Hangs onto all entities that this method was called with and they can be
	 * retrieved, in order, with {@link #getDeletedEntities()}.
	 * 
	 * @param entity recorded for later retrieval
	 */
	public void makeTransient(final Object entity) {
		deletedEntities.add(entity);
	}

	/**
	 * Does nothing.
	 * 
	 * @param entity ignored
	 */
	public void evict(final Object entity) {
		return;
	}

	/**
	 * Does nothing.
	 * 
	 * @param entities ignored
	 */
	public void evictEntities(final Iterable<? extends Object> entities) {}

	public List<Object> findAll() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public List<Object> findByExample(final Object exampleInstance,
			final String... excludeProperty) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	/**
	 * Does nothing.
	 */
	public void flush() {}

	public Object get(final Long id, final boolean lock) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String getEntityName(final Class<? extends Object> entityClass) {
		return entityClass.getName();
	}

	public String getEntityName(final Object entity) {
		return entity.getClass().getName();
	}

	public Serializable getIdentifier(final Object o) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void initialize(final Object entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	private Map<StandardRow, List<StandardCell>> rowsToCells = newHashMap();

	/**
	 * Does nothing.
	 * 
	 * @entity ignored
	 */
	public void makePersistent(final Object entity) {
		if (entity instanceof StandardCell) {
			final StandardCell cell = (StandardCell) entity;
			final StandardRow row = cell.getParent();
			if (rowsToCells.containsKey(cell.getParent())) {

			} else {
				rowsToCells.put(row, new ArrayList<StandardCell>());
			}
			nullFillAndSet(rowsToCells.get(row), row.getCellPosition(cell),
						cell);
		} else {

		}
	}

	public Map<StandardRow, List<StandardCell>> getRowsToCells() {
		return rowsToCells;
	}

	/**
	 * Does nothing and returns.
	 * 
	 * @param s ignored
	 * 
	 * @return this
	 */
	public TestObjectWithLongIdDAO setSession(final Session s) {
		return this;
	}

}
