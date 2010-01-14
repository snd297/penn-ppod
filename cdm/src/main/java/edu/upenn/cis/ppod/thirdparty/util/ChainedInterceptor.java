/*
 * From "https://www.hibernate.org/92.html" w/ modification suggested by the comments. 
 */
package edu.upenn.cis.ppod.thirdparty.util;

import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;

/**
 * Implementation of the Hibernate {@link Interceptor} interface that allows the
 * chaining of several different instances of the same interface.
 * <p>
 * From <a
 * href="https://www.hibernate.org/92.html">https://www.hibernate.org/92.
 * html</a> w/ modification suggested by the comments.
 * 
 * @author Laurent RIEU
 * @see Interceptor
 */
public class ChainedInterceptor extends EmptyInterceptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Interceptors to be chained
	private Interceptor[] interceptors = new Interceptor[0];

	/**
	 * Constructor
	 */
	public ChainedInterceptor() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param An array of interceptors
	 */
	public ChainedInterceptor(final Interceptor... interceptors) {
		super();
		this.interceptors = interceptors;
	}

	@Override
	public int[] findDirty(final Object entity, final Serializable id,
			final Object[] currentState, final Object[] previousState,
			final String[] propertyNames, final Type[] types) {
		int[] result = null;
		for (final Interceptor interceptor : interceptors) {
			result = interceptor.findDirty(entity, id, currentState,
					previousState, propertyNames, types);
			if (result != null) {
				/*
				 * If any interceptor has returned something not null, stop the
				 * chain
				 */
				break;
			}
		}
		return result;
	}

	/**
	 * Returns an array containing the instances of the <code>Interceptor</code>
	 * interface that are chained within this interceptor.
	 * 
	 * @return An array of interceptor
	 */
	public Interceptor[] getInterceptors() {
		return interceptors;
	}

	@Override
	public Object instantiate(final String entityName,
			final EntityMode entityMode, final Serializable id)
			throws CallbackException {
		Object result = null;
		for (final Interceptor interceptor : interceptors) {
			result = interceptor.instantiate(entityName, entityMode, id);
			if (result != null) {
				/*
				 * If any interceptor has returned something not null, stop the
				 * chain
				 */
				break;
			}
		}
		return result;
	}

	@Override
	public Boolean isTransient(final Object entity) {
		Boolean result = null;
		for (final Interceptor interceptor : interceptors) {
			result = interceptor.isTransient(entity);
			if (result != null) {
				// If any interceptor has returned either true or false, stop
				// the chain
				break;
			}
		}
		return result;
	}

	@Override
	public void onDelete(final Object entity, final Serializable id,
			final Object[] state, final String[] propertyNames,
			final Type[] types) throws CallbackException {
		for (final Interceptor interceptor : interceptors) {
			interceptor.onDelete(entity, id, state, propertyNames, types);
		}
	}

	@Override
	public boolean onFlushDirty(final Object entity, final Serializable id,
			final Object[] currentState, final Object[] previousState,
			final String[] propertyNames, final Type[] types)
			throws CallbackException {
		boolean result = false;
		for (final Interceptor interceptor : interceptors) {
			if (interceptor.onFlushDirty(entity, id, currentState,
					previousState, propertyNames, types)) {
				/*
				 * Returns true if one interceptor in the chain has modified the
				 * object current state
				 */
				result = true;
			}
		}
		return result;
	}

	@Override
	public boolean onLoad(final Object entity, final Serializable id,
			final Object[] state, final String[] propertyNames,
			final Type[] types) throws CallbackException {
		boolean result = false;
		for (final Interceptor interceptor : interceptors) {
			if (interceptor.onLoad(entity, id, state, propertyNames, types)) {
				/*
				 * Returns true if one interceptor in the chain has modified the
				 * object state
				 */
				result = true;
			}
		}
		return result;
	}

	@Override
	public boolean onSave(final Object entity, final Serializable id,
			final Object[] state, final String[] propertyNames,
			final Type[] types) throws CallbackException {
		boolean result = false;
		for (final Interceptor interceptor : interceptors) {
			if (interceptor.onSave(entity, id, state, propertyNames, types)) {
				/*
				 * Returns true if one interceptor in the chain has modified the
				 * object state
				 */
				result = true;
			}
		}
		return result;
	}

	@Override
	public void postFlush(final Iterator entities) throws CallbackException {
		final List entityList = newArrayList(entities);
		for (final Interceptor interceptor : interceptors) {
			interceptor.postFlush(entityList.iterator());
		}
	}

	@Override
	public void preFlush(final Iterator entities) throws CallbackException {
		final List entityList = newArrayList(entities);
		for (final Interceptor interceptor : interceptors) {
			interceptor.preFlush(entityList.iterator());
		}
	}

	/**
	 * Sets the instances of the <code>Interceptor</code> interface that are
	 * chained within this interceptor.
	 * 
	 * @param interceptors
	 */
	public ChainedInterceptor setInterceptors(final Interceptor... interceptors) {
		this.interceptors = interceptors;
		return this;
	}

}
