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
package edu.upenn.cis.ppod.model;

import static edu.upenn.cis.ppod.util.CollectionsUtil.newConcurrentHashMap;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.hibernate.EmptyInterceptor;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.type.Type;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.dao.IPPodVersionInfoDAO;
import edu.upenn.cis.ppod.dao.hibernate.HibernateDAOFactory;

/**
 * Assign a new {@link PPodVersionInfo} to any {@code PPodEntity}s that are
 * marked as being newly saved or modified and to any {@code null} members of
 * {@link CharacterStateMatrix#getColumnPPodVersionInfos()}
 * <p>
 * {@code PPodEntity}s are marked for a new {@link PPodVersionInfo} by having a
 * {@code null} {@code pPodVersionInfo} field.
 * 
 * @author Sam Donnelly
 */
public class PPodVersionInfoInterceptor extends EmptyInterceptor {

	private static final long serialVersionUID = 1L;

	private SessionFactory sessionFactory;

	private final HibernateDAOFactory.IFactory hibernateDAOFactoryFactory;

	private final Provider<PPodVersionInfo> pPodVersionInfoProvider;

	private final ConcurrentMap<Session, PPodVersionInfo> pPodVersionInfosBySession = newConcurrentHashMap();

	@Inject
	PPodVersionInfoInterceptor(
			final Provider<PPodVersionInfo> pPodVersionInfoProvider,
			final HibernateDAOFactory.IFactory hibernateDAOFactoryFactory) {
		this.pPodVersionInfoProvider = pPodVersionInfoProvider;
		this.hibernateDAOFactoryFactory = hibernateDAOFactoryFactory;
	}

	@Override
	public void afterTransactionCompletion(final Transaction tx) {
		pPodVersionInfosBySession.remove(sessionFactory.getCurrentSession());
	}

	/**
	 * In a temporary session, grab the next available pPOD version number,
	 * increment it, assign it to a {@code sessionFactory.getCurrentSession()}
	 * and {@code PPodVersionInfo} in {@link #pPodVersionInfosBySession}, and
	 * save (but not commit) it to the database.
	 */
	private void initializePPodVersionInfo() {
		if (pPodVersionInfosBySession.containsKey(sessionFactory
				.getCurrentSession())) {
			// Already been called.
			return;
		}
		// there's no alternative to the deprecated method yet - see its
		// javadoc: it says "TBD".
		@SuppressWarnings("deprecation")
		final Session tempSession = sessionFactory.openSession(sessionFactory
				.getCurrentSession().connection(), EmptyInterceptor.INSTANCE);
		final IPPodVersionInfoDAO pPodVersionInfoDAO = hibernateDAOFactoryFactory
				.create(tempSession).getPPodVersionInfoDAO();
		pPodVersionInfosBySession.put(sessionFactory.getCurrentSession(),
				pPodVersionInfoProvider.get().setPPodVersion(
						pPodVersionInfoDAO.getMaxPPodVersion() + 1).setCreated(
						new Date()));
		tempSession.save(pPodVersionInfosBySession.get(sessionFactory
				.getCurrentSession()));
		tempSession.flush();
	}

	/**
	 * Equivalent to {@code setPPodVersionInfoIfAppropriate(entity,
	 * propertyNames, currentState)}.
	 */
	@Override
	public boolean onFlushDirty(final Object entity, final Serializable id,
			final Object[] currentState, final Object[] previousState,
			final String[] propertyNames, final Type[] types) {
		return setPPodVersionInfoIfAppropriate(entity, propertyNames,
				currentState);
	}

	/**
	 * Equivalent to {@code setPPodVersionInfoIfAppropriate(entity,
	 * propertyNames, state)}.
	 */
	@Override
	public boolean onSave(final Object entity, final Serializable id,
			final Object[] state, final String[] propertyNames,
			final Type[] types) {
		return setPPodVersionInfoIfAppropriate(entity, propertyNames, state);
	}

	/**
	 * This method does two things:
	 * <ol>
	 * <li>
	 * If {@code entity} is an {@link IPPodEntity} and has {@code
	 * "pPodVersionInfo".equals(properyNames[n])} with {@code currentState[n] ==
	 * null}, then assign {@link PPodVersionInfoInterceptor#pPodVersionInfo} to
	 * {@code currentState[n]}.
	 * <li>
	 * If an {@code entity} is a {@link CharacterStateMatrix} then assign all
	 * {@link CharacterStateMatrix#getColumnPPodVersionInfos()} w/ a {@code
	 * null} value to {@link PPodVersionInfoInterceptor#pPodVersionInfo}. It
	 * does this by picking out {@code
	 * "columnPPodVersionInfos".equals(propertyNames[n])} and operating on
	 * {@code (List<PPodVersionInfo>) currentState[n]}.
	 * </ol>
	 * 
	 * @param entity being saved or updated
	 * @param propertyNames property names of the entity
	 * @param currentState the about to be saved or updated
	 * @return {@code true} if we modify {@code currentState}, {@code false}
	 *         otherwise
	 */
	private boolean setPPodVersionInfoIfAppropriate(final Object entity,
			final String[] propertyNames, final Object[] currentState) {
		boolean modified = false;
		if (entity instanceof IPPodVersioned) {
			if (!((IPPodEntity) entity).getAllowPersist()) {
				throw new IllegalArgumentException(
						"entity is marked do not persist. Entity is "
								+ entity.toString());
			}
			initializePPodVersionInfo();
			for (int i = 0; i < propertyNames.length; i++) {
				if (PPodVersionInfo.PPOD_VERSION_INFO_FIELD
						.equals(propertyNames[i])
						// Only set it if it's been modified in a way pPOD cares
						// about,
						// which is what pPodVersionInfo being null indicates
						&& (currentState[i] == null)) {
					currentState[i] = pPodVersionInfosBySession
							.get(sessionFactory.getCurrentSession());
					modified = true;
				}
			}
		}
		if (entity instanceof CharacterStateMatrix) {
			for (int i = 0; i < propertyNames.length; i++) {
				if ("columnPPodVersionInfos".equals(propertyNames[i])) {
					@SuppressWarnings("unchecked")
					final List<PPodVersionInfo> columnPPodVersionInfos = (List<PPodVersionInfo>) currentState[i];
					for (int j = 0; j < columnPPodVersionInfos.size(); j++) {
						if (columnPPodVersionInfos.get(j) == null) {
							columnPPodVersionInfos.set(j,
									pPodVersionInfosBySession
											.get(sessionFactory
													.getCurrentSession()));
							modified = true;
						}
					}
					currentState[i] = columnPPodVersionInfos;
				}
			}
		}
		return modified;
	}

	/**
	 * Set the session factory for this interceptor to use.
	 * 
	 * @param sessionFactory the session factory
	 * @return this
	 */
	public PPodVersionInfoInterceptor setSessionFactory(
			final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		return this;
	}
}
