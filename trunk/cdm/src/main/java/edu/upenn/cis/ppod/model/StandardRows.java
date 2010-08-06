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
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.annotations.Parent;

import edu.upenn.cis.ppod.modelinterfaces.IOTUKeyedMap;
import edu.upenn.cis.ppod.modelinterfaces.IOTUKeyedMapPlus;
import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.OTUStandardRowPair;

/**
 * Maps {@link OTU}s to {@link StandardRow}s.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@Embeddable
@Access(AccessType.PROPERTY)
public class StandardRows
		implements IOTUKeyedMap<StandardRow> {

	private final IOTUKeyedMapPlus<StandardRow, StandardMatrix, OTUStandardRowPair> rows =
			new OTUKeyedMapPlus<StandardRow, StandardMatrix, OTUStandardRowPair>();

	StandardRows() {}

	public void accept(final IVisitor visitor) {
		rows.accept(visitor);
	}

	/** {@inheritDoc} */
	public void afterUnmarshal() {
		rows.afterUnmarshal();
	}

	public void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent) {
		// Don't do checkNotNull(parent) since this is called by JAXB and we
		// can't control it
		rows.afterUnmarshal((StandardMatrix) parent);
	}

	public boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {
		getOTUSomethingPairs().clear();
		for (final Map.Entry<OTU, StandardRow> otuToRow : getValues()
				.entrySet()) {
			getOTUSomethingPairs().add(
					OTUStandardRowPair.of(otuToRow.getKey(),
							otuToRow.getValue()));
		}
		return true;
	}

	public StandardRows clear() {
		rows.clear();
		return this;
	}

	public StandardRow get(final OTU key) {
		return rows.get(key);
	}

	@XmlElement(name = "otuRowPair")
	@Transient
	public Set<OTUStandardRowPair> getOTUSomethingPairs() {
		return rows.getOTUSomethingPairs();
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
	@MapKeyJoinColumn(name = OTU.JOIN_COLUMN)
	public Map<OTU, StandardRow> getValues() {
		return rows.getValues();
	}

	/** {@inheritDoc} */
	public StandardRow put(final OTU key, final StandardRow value) {
		return rows.put(key, value);
	}

	/** {@inheritDoc} */
	public StandardRows setOTUs() {
		rows.setOTUs();
		return this;
	}

	/**
	 * Set the owner of this {@code StandardRows}.
	 * 
	 * @param parent the owner
	 * 
	 * @return this
	 */
	public StandardRows setParent(final StandardMatrix parent) {
		rows.setParent(parent);
		return this;
	}

	@SuppressWarnings("unused")
	private StandardRows setValues(final Map<OTU, StandardRow> values) {
		rows.setValues(values);
		return this;
	}

}
