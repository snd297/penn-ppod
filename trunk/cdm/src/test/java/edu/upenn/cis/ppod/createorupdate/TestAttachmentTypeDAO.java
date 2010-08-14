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

import edu.upenn.cis.ppod.dao.IAttachmentTypeDAO;
import edu.upenn.cis.ppod.model.IAttachmentType;

/**
 * @author Sam Donnelly
 */
public class TestAttachmentTypeDAO implements IAttachmentTypeDAO {

	private Map<String, Map<String, IAttachmentType>> typesByNamespaceLabelAndTypeLabel = newHashMap();

	public void makeTransient(final IAttachmentType entity) {
		throw new UnsupportedOperationException();
	}

	public List<IAttachmentType> findAll() {
		throw new UnsupportedOperationException();
	}

	public List<IAttachmentType> findByExample(
			final IAttachmentType exampleInstance,
			final String... excludeProperty) {
		throw new UnsupportedOperationException();
	}

	public IAttachmentType get(final Long id, final boolean lock) {
		throw new UnsupportedOperationException();
	}

	public IAttachmentType getTypeByNamespaceAndLabel(
			final String namespaceLabel,
			final String typeLabel) {
		final Map<String, IAttachmentType> typesByLabel =
				typesByNamespaceLabelAndTypeLabel.get(namespaceLabel);
		if (typesByLabel == null) {
			return null;
		}
		return typesByLabel.get(typeLabel);
	}

	public Serializable getIdentifier(final Object o) {
		throw new UnsupportedOperationException();
	}

	public void makePersistent(final IAttachmentType entity) {
		return;
	}

	public TestAttachmentTypeDAO setTypesByNamespaceLabelAndTypeLabel(
			final Map<String, Map<String, IAttachmentType>> typesByNamespaceLabelAndTypeLabel) {
		this.typesByNamespaceLabelAndTypeLabel = typesByNamespaceLabelAndTypeLabel;
		return this;
	}

	public void evict(IAttachmentType entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void flush() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void evictEntities(final Iterable<? extends IAttachmentType> entities) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void initialize(IAttachmentType entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String getEntityName(IAttachmentType entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String getEntityName(Class<? extends IAttachmentType> entityClass) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
