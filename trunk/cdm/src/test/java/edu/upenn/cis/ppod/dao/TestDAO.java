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
package edu.upenn.cis.ppod.dao;

import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.util.List;

import edu.upenn.cis.ppod.thirdparty.dao.IDAO;

public class TestDAO<T, ID extends Serializable> implements IDAO<T, ID> {

	private final List<T> madePersistent = newArrayList();

	public void evict(final T entity) {}

	public void evictEntities(final Iterable<? extends T> entities) {}

	public List<T> findAll() {
		return null;
	}

	public List<T> findByExample(final T exampleInstance,
			final String... excludeProperty) {
		return null;
	}

	public void flush() {}

	public T findById(final ID id, final boolean lock) {
		return null;
	}

	public String getEntityName(final Class<? extends T> entityClass) {
		return null;
	}

	public String getEntityName(final T entity) {
		return null;
	}

	public Serializable getIdentifier(final Object o) {
		return null;
	}

	public List<T> getMadePersistent() {
		return madePersistent;
	}

	public void initialize(final T entity) {

	}

	public void makePersistent(final T entity) {
		madePersistent.add(entity);
	}

	public void makeTransient(final T entity) {}

}
