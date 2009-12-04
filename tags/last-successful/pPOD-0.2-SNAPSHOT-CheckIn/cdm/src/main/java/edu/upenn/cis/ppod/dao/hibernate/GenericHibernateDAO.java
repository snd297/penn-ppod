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
package edu.upenn.cis.ppod.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;

import edu.upenn.cis.ppod.dao.IDAO;

/**
 * From http://www.hibernate.org/328.html.
 * 
 * @author Sam Donnelly
 * @param <T> transfer object type
 * @param <ID> the type of the Hibernate PHYLO_CHAR_ID_COLUMN - usually
 *            {@link Long}
 */
public class GenericHibernateDAO<T, ID extends Serializable>
		implements IDAO<T, ID> {

	/** No arg constructor. */
	public GenericHibernateDAO() {
		Class<?> curClass = getClass();

		// We're looking for the first {@code ParametrizedType} in the class
		// hierarchy.
		while (true) {
			if (curClass.getGenericSuperclass() instanceof ParameterizedType) {
				// tempPersistentClass is there just to use SuppressWarnings
				// with as little scope as possible.
				@SuppressWarnings("unchecked")
				final Class<T> tempPersistentClass = (Class<T>) ((ParameterizedType) curClass
						.getGenericSuperclass()).getActualTypeArguments()[0];
				persistentClass = tempPersistentClass;
				break;
			} else {
				curClass = curClass.getSuperclass();
			}
		}
	}

	/** DAO transfer object type. */
	private Class<T> persistentClass;

	/** Hang on to a session for this DAO. */
	private Session session;

	/**
	 * Call {@link Session#clear()} on this <code>GenericHibernateDAO</code>'s
	 * session.
	 */
	public void clear() {
		getSession().clear();
	}

	public List<T> findAll() {
		return findByCriteria();
	}

	public List<T> findByExample(final T exampleInstance,
			final String... excludeProperty) {
		final Criteria crit = getSession().createCriteria(getPersistentClass());
		final Example example = Example.create(exampleInstance);
		for (final String exclude : excludeProperty) {
			example.excludeProperty(exclude);
		}
		crit.add(example);

		@SuppressWarnings("unchecked")
		final List<T> suppressUncheckedWarningTs = crit.list();
		return suppressUncheckedWarningTs;
	}

	/**
	 * Call {@link Session#flush()} on this <code>GenericHibernateDAO</code>'s
	 * session.
	 */
	public void flush() {
		getSession().flush();
	}

	public T get(final ID id, final boolean lock) {
		if (id == null) {
			return null;
		}
		T entity;
		if (lock) {
			@SuppressWarnings("unchecked")
			final T suppressUncheckedWarningEntity = (T) getSession().get(
					getPersistentClass(), id, LockMode.UPGRADE);
			entity = suppressUncheckedWarningEntity;
		} else {
			@SuppressWarnings("unchecked")
			final T suppressUncheckedWarningEntity = (T) getSession().get(
					getPersistentClass(), id);
			entity = suppressUncheckedWarningEntity;
		}
		return entity;
	}

	public Serializable getIdentifier(final Object o) {
		return getSession().getIdentifier(o);
	}

	/**
	 * Get the {@link Class} of the entity that this {@link IDAO} manages.
	 * 
	 * @return the {@link Class} of the entity that this {@link IDAO} manages
	 */
	public Class<T> getPersistentClass() {
		return persistentClass;
	}

	public T delete(final T entity) {
		getSession().delete(entity);
		return entity;
	}

	public T saveOrUpdate(final T entity) {
		getSession().saveOrUpdate(entity);
		return entity;
	}

	/**
	 * Setter.
	 * 
	 * @param s a {@code Session}, can be {@code null}
	 * @return this
	 */
	public IDAO<T, ID> setSession(final Session s) {
		this.session = s;
		return this;
	}

	/**
	 * Use this inside subclasses as a convenience method.
	 * <p>
	 * By criteria, find {@code T}s.
	 * 
	 * @param criterion 1 or more criteria for the search
	 * 
	 * @return persisted {@code T}s that match {@code criterion}
	 */
	protected List<T> findByCriteria(final Criterion... criterion) {
		final Criteria crit = getSession().createCriteria(getPersistentClass());
		for (final Criterion c : criterion) {
			crit.add(c);
		}

		@SuppressWarnings("unchecked")
		final List<T> suppressUncheckedWarningTs = crit.list();
		return suppressUncheckedWarningTs;
	}

	/**
	 * Get this <code>IDAO</code>'s session. Initializes the session
	 * <code>HibernateUtil.getSessionFactory().getCurrentSession()</code> if
	 * necessary.
	 * 
	 * @return see description
	 */
	protected Session getSession() {
		if (session == null) {
			throw new IllegalStateException(
					"Session has not been set on DAO before usage");
		}
		return session;
	}

}
