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
package edu.upenn.cis.ppod.model;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;
import com.google.inject.servlet.RequestScoped;

import edu.upenn.cis.ppod.imodel.INewVersionInfo;

/**
 * @author Sam Donnelly
 * 
 */
public class ModelModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(INewVersionInfo.class)
				.to(NewVersionInfoDB.class)
				.in(RequestScoped.class);
		bind(Attachment.IIsOfNamespace.IFactory.class)
				.toProvider(
						FactoryProvider.newFactory(
								Attachment.IIsOfNamespace.IFactory.class,
								Attachment.IIsOfNamespace.class));
	}
}
