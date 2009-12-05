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
package edu.upenn.cis.ppod.util;

import java.lang.reflect.Constructor;

import org.testng.IObjectFactory;
import org.testng.internal.ObjectFactoryImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;

/**
 * @author Sam Donnelly
 */
public abstract class GuiceObjectFactory extends AbstractModule implements
		IObjectFactory {
	private Injector injector;
	private final ObjectFactoryImpl creator = new ObjectFactoryImpl();
	

	protected GuiceObjectFactory setInjector(final Injector injector) { 
		this.injector = injector;
		return this;
	}

	public GuiceObjectFactory() {

	}

	public Object newInstance(final Constructor constructor,
			final Object... objects) {
		final Object o = creator.newInstance(constructor, objects);
		injector.injectMembers(o);
		return o;
	}
}
