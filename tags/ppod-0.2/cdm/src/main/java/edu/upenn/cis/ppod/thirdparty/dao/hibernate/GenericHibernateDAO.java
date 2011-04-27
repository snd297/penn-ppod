/*
 * Copyright (c) 2005, Christian Bauer <christian@hibernate.org>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the original author nor the names of contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES of MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT of
 * SUBSTITUTE GOODS OR SERVICES; LOSS of USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY of LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT of THE USE of THIS SOFTWARE, EVEN IF ADVISED of
 * THE POSSIBILITY of SUCH DAMAGE.
 */
package edu.upenn.cis.ppod.thirdparty.dao.hibernate;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;

import edu.upenn.cis.ppod.thirdparty.dao.IDAO;

/**
 * From http://www.hibernate.org/328.html.
 * 
 * @param <T> transfer object type
 * @param <ID> the type of the Hibernate PHYLO_CHAR_ID_COLUMN - usually
 *            {@link Long}
 */
public abstract class GenericHibernateDAO<T, ID extends Serializable>
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
	public final void clear() {
		getSession().clear();
	}

	public final List<T> findAll() {
		return findByCriteria();
	}

	public final List<T> findByExample(final T exampleInstance,
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

	public T findById(final ID id, final boolean lock) {
		checkNotNull(id);

		T entity;
		if (lock) {
			@SuppressWarnings("unchecked")
			final T suppressUncheckedWarningEntity =
					(T) getSession()
							.get(
									getPersistentClass(),
									id,
									new LockOptions(LockMode.PESSIMISTIC_WRITE));
			entity = suppressUncheckedWarningEntity;
		} else {
			@SuppressWarnings("unchecked")
			final T suppressUncheckedWarningEntity = (T) getSession().get(
					getPersistentClass(), id);
			entity = suppressUncheckedWarningEntity;
		}
		return entity;
	}

	public final Serializable getIdentifier(final Object o) {
		return getSession().getIdentifier(o);
	}

	/**
	 * Get the {@link Class} of the entity that this {@link IDAO} manages.
	 * 
	 * @return the {@link Class} of the entity that this {@link IDAO} manages
	 */
	public final Class<T> getPersistentClass() {
		return persistentClass;
	}

	public final void evict(final T entity) {
		getSession().evict(entity);
	}

	public final void evictEntities(final Iterable<? extends T> entities) {
		for (final T entity : entities) {
			evict(entity);
		}
	}

	public final void makeTransient(final T entity) {
		checkNotNull(entity);
		getSession().delete(entity);
	}

	public final void makePersistent(final T entity) {
		checkNotNull(entity);
		getSession().saveOrUpdate(entity);
	}

	/** {@inheritDoc} */
	public final void setSession(final Session session) {
		checkNotNull(session);
		this.session = session;
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
	protected final List<T> findByCriteria(final Criterion... criterion) {
		final Criteria crit = getSession().createCriteria(getPersistentClass());
		for (final Criterion c : criterion) {
			crit.add(c);
		}

		@SuppressWarnings("unchecked")
		final List<T> suppressUncheckedWarningTs = crit.list();
		return suppressUncheckedWarningTs;
	}

	/**
	 * Get this <code>IDAO</code>'s session.
	 * 
	 * @return see description
	 */
	protected final Session getSession() {
		if (session == null) {
			throw new IllegalStateException("no session set for dao");
			// setSession(HibernateUtil.getSessionFactory().getCurrentSession());
		}
		return session;
	}

	public final void initialize(final T entity) {
		checkNotNull(entity);
		Hibernate.initialize(entity);
	}

	public final String getEntityName(T entity) {
		return getSession().getEntityName(entity);
	}

}
