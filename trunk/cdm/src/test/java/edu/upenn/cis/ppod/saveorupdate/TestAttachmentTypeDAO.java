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
package edu.upenn.cis.ppod.saveorupdate;

import static com.google.common.collect.Maps.newHashMap;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.upenn.cis.ppod.dao.IAttachmentTypeDAO;
import edu.upenn.cis.ppod.model.AttachmentType;

/**
 * @author Sam Donnelly
 */
public class TestAttachmentTypeDAO implements IAttachmentTypeDAO {

	private Map<String, Map<String, AttachmentType>> typesByNamespaceLabelAndTypeLabel = newHashMap();

	public void delete(final AttachmentType entity) {
		throw new UnsupportedOperationException();
	}

	public List<AttachmentType> findAll() {
		throw new UnsupportedOperationException();
	}

	public List<AttachmentType> findByExample(
			final AttachmentType exampleInstance,
			final String... excludeProperty) {
		throw new UnsupportedOperationException();
	}

	public AttachmentType get(final Long id, final boolean lock) {
		throw new UnsupportedOperationException();
	}

	public AttachmentType getAttachmentTypeByNamespaceAndType(
			final String namespaceLabel, final String typeLabel) {
		final Map<String, AttachmentType> typesByLabel = typesByNamespaceLabelAndTypeLabel
				.get(namespaceLabel);
		if (typesByLabel == null) {
			return null;
		}
		return typesByLabel.get(typeLabel);
	}

	public Serializable getIdentifier(final Object o) {
		throw new UnsupportedOperationException();
	}

	public void saveOrUpdate(final AttachmentType entity) {
		throw new UnsupportedOperationException();
	}

	public TestAttachmentTypeDAO setTypesByNamespaceLabelAndTypeLabel(
			final Map<String, Map<String, AttachmentType>> typesByNamespaceLabelAndTypeLabel) {
		this.typesByNamespaceLabelAndTypeLabel = typesByNamespaceLabelAndTypeLabel;
		return this;
	}

	public void evict(AttachmentType entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void flush() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void evictEntities(Collection<? extends AttachmentType> entities) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void initialize(AttachmentType entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String getEntityName(AttachmentType entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String getEntityName(Class<? extends AttachmentType> entityClass) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
