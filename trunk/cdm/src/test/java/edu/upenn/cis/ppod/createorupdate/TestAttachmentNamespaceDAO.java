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
import edu.upenn.cis.ppod.model.AttachmentNamespace;

/**
 * @author Sam Donnelly
 */
public class TestAttachmentNamespaceDAO implements IAttachmentNamespaceDAO {

	private final Map<String, AttachmentNamespace> namespacesByLabel = newHashMap();

	public TestAttachmentNamespaceDAO setNamespacesByLabel(
			final Map<String, AttachmentNamespace> namespacesByLabel) {
		namespacesByLabel.clear();
		this.namespacesByLabel.putAll(namespacesByLabel);
		return this;
	}

	public void makeTransient(final AttachmentNamespace entity) {
		throw new UnsupportedOperationException();
	}

	public List<AttachmentNamespace> findAll() {
		throw new UnsupportedOperationException();
	}

	public List<AttachmentNamespace> findByExample(
			final AttachmentNamespace exampleInstance,
			final String... excludeProperty) {
		throw new UnsupportedOperationException();
	}

	public AttachmentNamespace get(final Long id, final boolean lock) {
		throw new UnsupportedOperationException();
	}

	public Serializable getIdentifier(final Object o) {
		throw new UnsupportedOperationException();
	}

	public AttachmentNamespace getNamespaceByLabel(final String label) {
		return namespacesByLabel.get(label);
	}

	public void makePersistent(final AttachmentNamespace entity) {
		return;
	}

	public void evict(AttachmentNamespace entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void flush() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void evictEntities(
			final Iterable<? extends AttachmentNamespace> entities) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void initialize(AttachmentNamespace entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String getEntityName(AttachmentNamespace entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String getEntityName(Class<? extends AttachmentNamespace> entityClass) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
