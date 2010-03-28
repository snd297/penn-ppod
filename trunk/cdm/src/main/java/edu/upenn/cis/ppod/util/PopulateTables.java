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

import java.util.Iterator;

import org.hibernate.classic.Session;
import org.hibernate.context.ManagedSessionContext;

import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.upenn.cis.ppod.model.CharacterState;
import edu.upenn.cis.ppod.model.DNACharacter;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfoHibernate;
import edu.upenn.cis.ppod.thirdparty.util.HibernateUtil;

/**
 * @author Sam Donnelly
 * 
 */
public class PopulateTables {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Throwable {
		Session session = null;
		try {
			final Injector injector = Guice
					.createInjector(new PPodCoreModule());
			final DNACharacter dnaCharacter = injector
					.getInstance(DNACharacter.class);

			dnaCharacter.setPPodId();

			session = HibernateUtil.getSessionFactory().openSession();
			ManagedSessionContext.bind(session);
			session.beginTransaction();

			final INewPPodVersionInfo newPPodVersionInfo = injector
					.getInstance(INewPPodVersionInfoHibernate.IFactory.class)
					.create(session);
			dnaCharacter.setPPodVersionInfo(newPPodVersionInfo
					.getNewPPodVersionInfo());

			for (final Iterator<CharacterState> statesItr = dnaCharacter
					.getStatesIterator(); statesItr.hasNext();) {
				final CharacterState state = statesItr.next();
				state.setPPodVersionInfo(newPPodVersionInfo
						.getNewPPodVersionInfo());
			}

			session.save(dnaCharacter);

			session.getTransaction().commit();

		} catch (Throwable t) {
			if (session != null) {
				if (session.getTransaction().isActive()) {
					try {
						session.getTransaction().rollback();
					} catch (Throwable rbEx) {
						System.err.println("rollback exception: " + t);
					}
				}
			}
			throw t;
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (Throwable t) {
					System.err.println("exception while closing session: " + t);
				}
				ManagedSessionContext.unbind(HibernateUtil.getSessionFactory());
			}
		}
	}
}
