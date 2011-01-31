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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Parent;

import edu.upenn.cis.ppod.imodel.IOtuKeyedMap;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * Maps {@link OTU}s to {@link StandardRow}s.
 * 
 * @author Sam Donnelly
 */
@Embeddable
@Access(AccessType.PROPERTY)
class StandardRows
		implements IOtuKeyedMap<StandardRow> {

	private final OtuKeyedMapPlus<StandardRow, StandardMatrix> rows = new OtuKeyedMapPlus<StandardRow, StandardMatrix>();

	StandardRows() {}

	StandardRows(final StandardMatrix parent) {
		checkNotNull(parent);
		rows.setParent(parent);
	}

	public void accept(final IVisitor visitor) {
		rows.accept(visitor);
	}

	public void clear() {
		rows.clear();
	}

	public StandardRow get(final Otu key) {
		return rows.get(key);
	}

	@Parent
	public StandardMatrix getParent() {
		return rows.getParent();
	}

	/**
	 * We want everything but SAVE_UPDATE (which ALL will give us) - once it's
	 * evicted out of the persistence context, we don't want it back in via
	 * cascading UPDATE. So that we can run leaner for large matrices. This is
	 * more important for protein matrices but we do it here for at least
	 * consistency since the same client code works with the different kinds of
	 * matrices.
	 */
	@OneToMany(cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE,
			CascadeType.REMOVE,
			CascadeType.DETACH,
			CascadeType.REFRESH },
			orphanRemoval = true)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = StandardRow.JOIN_COLUMN))
	@MapKeyJoinColumn(name = Otu.JOIN_COLUMN)
	public Map<Otu, StandardRow> getValues() {
		return rows.getValues();
	}

	/** {@inheritDoc} */
	public StandardRow put(final Otu key, final StandardRow value) {
		return rows.put(key, value);
	}

	/** {@inheritDoc} */
	public void updateOtus() {
		rows.updateOtus();
	}

	/**
	 * Set the owner of this {@code StandardRows}.
	 * 
	 * @param parent the owner
	 */
	public void setParent(final StandardMatrix parent) {
		rows.setParent(parent);
	}

	@SuppressWarnings("unused")
	private void setValues(final Map<Otu, StandardRow> values) {
		rows.setValues(values);
	}

}
