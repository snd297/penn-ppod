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
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model;

import edu.upenn.cis.ppod.services.hibernate.PPodEntitiesResourceHibernate;

/**
 * @author Sam Donnelly
 */
public interface IPPodEntity extends IPersistentObject, IAttachee,
		IPPodVersioned {

	/**
	 * Indicate that this object should not be persisted and changes to the pPOD
	 * version numbers should not be propagated.
	 * <p>
	 * {@link PPodVersionInfoInterceptor} checks this flag before it does any
	 * write operations. Beyond that, this flag should not be taken as a
	 * guarantee that an object will not be written. Note that if {@code
	 * PPodVersionInterceptor} is not configured in a session, it will,
	 * obviously, not check this flag.
	 * <p>
	 * This flag was invented so that we can remove matrices and tree sets from
	 * an {@link OTUSet} in
	 * {@link PPodEntitiesResourceHibernate#getEntitiesByHqlQuery(String)}
	 * before we return the data to the client. It is a less than ideal
	 * solution.
	 * 
	 * @see PPodVersionInfoInterceptor
	 * 
	 * @return this {@code pPodEntity}
	 */
	PersistentObject unsetAllowPersistAndResetPPodVersionInfo();



	boolean getAllowResetPPodVersionInfo();

    IPPodEntity setAllowResetPPodVersionInfo(boolean allowResetPPodVersionInfo);

}
