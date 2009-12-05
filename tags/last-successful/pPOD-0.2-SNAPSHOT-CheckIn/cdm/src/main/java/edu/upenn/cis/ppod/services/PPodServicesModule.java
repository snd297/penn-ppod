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
package edu.upenn.cis.ppod.services;

import com.google.inject.AbstractModule;

import edu.upenn.cis.ppod.services.hibernate.PPodEntitiesResourceHibernate;
import edu.upenn.cis.ppod.services.hibernate.StudyResourceHibernate;
import edu.upenn.cis.ppod.services.ppodentity.IStudy2StudyInfo;
import edu.upenn.cis.ppod.services.ppodentity.Study2StudyInfo;

/**
 * @author Sam Donnelly
 */
public final class PPodServicesModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IPPodGreetingResource.class).to(PPodGreetingService.class);
		bind(IStudyResource.class).to(StudyResourceHibernate.class);
		bind(IStudy2StudyInfo.class).to(Study2StudyInfo.class);
		bind(IPPodEntitiesResource.class).to(
				PPodEntitiesResourceHibernate.class);
	}
}