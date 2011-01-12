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
import edu.upenn.cis.ppod.imodel.IOtuKeyedMapPlus;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * Maps {@link OTU}s to {@link DNARow}s.
 * 
 * @author Sam Donnelly
 */
@Embeddable
@Access(AccessType.PROPERTY)
class DnaRows implements IOtuKeyedMap<DnaRow> {

	private final IOtuKeyedMapPlus<DnaRow, DnaMatrix> rows =
			new OtuKeyedMapPlus<DnaRow, DnaMatrix>();

	DnaRows() {}

	/** {@inheritDoc} */
	public void accept(final IVisitor visitor) {
		rows.accept(visitor);
	}

	public void clear() {
		rows.clear();
	}

	/** {@inheritDoc} */
	public DnaRow get(final Otu key) {
		return rows.get(key);
	}

	@Parent
	public DnaMatrix getParent() {
		return rows.getParent();
	}

	/**
	 * We want everything but SAVE_UPDATE (which ALL will give us) - once it's
	 * evicted out of the persistence context, we don't want it back in via
	 * cascading UPDATE. So that we can run leaner for large matrices.
	 */
	@OneToMany(cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE,
			CascadeType.REMOVE,
			CascadeType.DETACH,
			CascadeType.REFRESH },
			orphanRemoval = true)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = DnaRow.JOIN_COLUMN))
	@MapKeyJoinColumn(name = Otu.JOIN_COLUMN)
	public Map<Otu, DnaRow> getValues() {
		return rows.getValues();
	}

	/** {@inheritDoc} */
	public DnaRow put(final Otu key, final DnaRow value) {
		return rows.put(key, value);
	}

	/**
	 * Set the owner of this object.
	 * 
	 * @param parent the owner
	 */
	public void setParent(final DnaMatrix parent) {
		rows.setParent(parent);
	}

	/** {@inheritDoc} */
	public void setValues(
			final Map<Otu, DnaRow> values) {
		rows.setValues(values);
	}

	/** {@inheritDoc} */
	public void updateOtus() {
		rows.updateOtus();
	}
}
