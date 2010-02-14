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
package edu.upenn.cis.ppod.security;

import java.io.Serializable;

import org.apache.shiro.SecurityUtils;
import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.security.PPodPermission;
import edu.upenn.cis.ppod.thirdparty.util.ChainedInterceptor;

/**
 * Hibernate interceptor that implements CRUD access control via Apache Shiro.
 * 
 * N.B., This is not a typical Interceptor implementation, don't use this as a
 * model.
 */
public final class AccessInterceptor extends ChainedInterceptor {
		

	private final Provider<PPodPermission> permissionFactory;

	@Inject
	AccessInterceptor(final Provider<PPodPermission> permissionFactory) {
		this.permissionFactory = permissionFactory;
	}

	/**
	 * Common routine that performs table- and row-level access checks for
	 * specified action and object.
	 * 
	 * @param clazz a mapped class
	 * @param action the action we seek to perform.
	 * @param id id of object to be created, updated or deleted from the
	 *            database.
	 * @throws CallbackException if the action is not permitted
	 */
	private void check(final Class clazz, final String action,
			final Serializable id) throws CallbackException {

		// We're only checking studies
		if (clazz != Study.class) {
			return;
		}

		try {
			PPodPermission permission = permissionFactory.get();
			permission.setDomain(clazz.getName());
			permission.setActions(action);
			permission.setTargets(id == null ? null : id.toString());

			if (SecurityUtils.getSubject().isPermitted(permission)) {
				return;
			}
			throw new SecurityException("You can't " + action + " this study");
		} catch (final SecurityException e) {
			throw new CallbackException(e.getMessage(), e);
		}
	}

	/**
	 * Method called before an object is delete. This method is called by
	 * "delete" actions.
	 * 
	 * @param entity object to be deleted from the database.
	 * @param id the identifier of the instance.
	 * @param state array of property values.
	 * @param propertyNames array of property names.
	 * @param types array of property types.
	 * @throws CallbackException if a problem occured.
	 */
	@Override
	public void onDelete(final Object entity, final Serializable id,
			final Object[] state, final String[] propertyNames,
			final Type[] types) throws CallbackException {

		check(entity.getClass(), "delete", id);
		super.onDelete(entity, id, state, propertyNames, types);
	}

	/**
	 * Method called when an object is detected to be dirty, during a flush.
	 * This method is called by "modify" actions.
	 * 
	 * @param entity object to be updated in the database.
	 * @param id the identifier of the instance.
	 * @param currentState array of property values.
	 * @param previousState cached array of property values.
	 * @param propertyNames array of property names.
	 * @param types array of property types.
	 * @return true if the currentState was modified in any way.
	 * @throws CallbackException if a problem occured.
	 */
	@Override
	public boolean onFlushDirty(final Object entity, final Serializable id,
			final Object[] currentState, final Object[] previousState,
			final String[] propertyNames, final Type[] types)
			throws CallbackException {

		check(entity.getClass(), "edit", id);
		return super.onFlushDirty(entity, id, currentState, previousState,
				propertyNames, types);
	}

	/**
	 * Method called just before an object is initialized. This method is called
	 * by "load" actions and queries, but there does not seem to be a way to
	 * distinguish between these cases.
	 * 
	 * @param entity uninitialized instance of the class to be loaded
	 * @param id the identifier of the new instance.
	 * @param state array of property values.
	 * @param propertyNames array of property names.
	 * @param types array of property types.
	 * @return true if the state was modified in any way.
	 * @throws CallbackException if a problem occured.
	 */
	@Override
	public boolean onLoad(final Object entity, final Serializable id,
			final Object[] state, final String[] propertyNames,
			final Type[] types) throws CallbackException {
		check(entity.getClass(), "view", id);
		return super.onLoad(entity, id, state, propertyNames, types);
	}

	/**
	 * Method called before an object is saved. This method is called by
	 * "create" actions.
	 * 
	 * @param entity object to be saved to the database.
	 * @param id the identifier of the instance.
	 * @param state array of property values.
	 * @param propertyNames array of property names.
	 * @param types array of property types.
	 * @return true if the state was modified the state in any way.
	 * @throws CallbackException if a problem occured.
	 */
	@Override
	public boolean onSave(final Object entity, final Serializable id,
			final Object[] state, final String[] propertyNames,
			final Type[] types) throws CallbackException {

		check(entity.getClass(), "create", id);
		return super.onSave(entity, id, state, propertyNames, types);
	}
}
