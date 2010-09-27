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
package edu.upenn.cis.ppod.createorupdate;

import static com.google.common.collect.Maps.newHashMap;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import edu.upenn.cis.ppod.dao.IAttachmentNamespaceDAO;
import edu.upenn.cis.ppod.imodel.IAttachmentNamespace;

/**
 * @author Sam Donnelly
 */
public class TestAttachmentNamespaceDAO implements IAttachmentNamespaceDAO {

	private final Map<String, IAttachmentNamespace> namespacesByLabel = newHashMap();

	public TestAttachmentNamespaceDAO setNamespacesByLabel(
			final Map<String, ? extends IAttachmentNamespace> namespacesByLabel) {
		namespacesByLabel.clear();
		this.namespacesByLabel.putAll(namespacesByLabel);
		return this;
	}

	public void makeTransient(final IAttachmentNamespace entity) {
		throw new UnsupportedOperationException();
	}

	public List<IAttachmentNamespace> findAll() {
		throw new UnsupportedOperationException();
	}

	public List<IAttachmentNamespace> findByExample(
			final IAttachmentNamespace exampleInstance,
			final String... excludeProperty) {
		throw new UnsupportedOperationException();
	}

	public IAttachmentNamespace findById(final Long id, final boolean lock) {
		throw new UnsupportedOperationException();
	}

	public Serializable getIdentifier(final Object o) {
		throw new UnsupportedOperationException();
	}

	public IAttachmentNamespace getNamespaceByLabel(final String label) {
		return namespacesByLabel.get(label);
	}

	public void makePersistent(final IAttachmentNamespace entity) {
		return;
	}

	public void evict(IAttachmentNamespace entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void flush() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void evictEntities(
			final Iterable<? extends IAttachmentNamespace> entities) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void initialize(IAttachmentNamespace entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String getEntityName(IAttachmentNamespace entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String getEntityName(
			Class<? extends IAttachmentNamespace> entityClass) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
