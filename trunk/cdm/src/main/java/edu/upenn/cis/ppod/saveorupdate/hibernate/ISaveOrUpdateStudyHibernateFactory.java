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
package edu.upenn.cis.ppod.saveorupdate.hibernate;

import org.hibernate.Session;

import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateStudy;

/**
 * Create {@link Study}s that depend on a {@link Session}.
 * 
 * @author Sam Donnelly
 */
public interface ISaveOrUpdateStudyHibernateFactory {

	/**
	 * Create {@link Study}s that depend on a {@link Session}.
	 * 
	 * @param session dependency
	 * @param newPPodVersionInfo
	 * @return a new {@link Study}
	 */
	ISaveOrUpdateStudy create(Session session,
			INewPPodVersionInfo newPPodVersionInfo);
}
