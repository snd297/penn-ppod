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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.google.inject.AbstractModule;

import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.model.VersionInfo;

/**
 * @author Sam Donnelly
 * 
 */
public class TestPPodUtilModule extends AbstractModule {

	@Override
	protected void configure() {
		final INewVersionInfo newVersionInfo = mock(INewVersionInfo.class);
		bind(INewVersionInfo.class).toInstance(newVersionInfo);

		final VersionInfo versionInfo = mock(VersionInfo.class);
		when(newVersionInfo.getNewVersionInfo()).thenReturn(versionInfo);

		bind(Session.class).to(StubSession.class);
		bind(Query.class).to(StubQuery.class);
		bind(SessionFactory.class).to(StubSessionFactory.class);
	}
}
