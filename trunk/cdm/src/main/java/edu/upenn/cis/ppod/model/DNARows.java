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
import edu.upenn.cis.ppod.util.OTUDNARowPair;

/**
 * Maps {@link OTU}s to {@link DNARow}s.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@Embeddable
@Access(AccessType.PROPERTY)
public class DNARows implements
		IOTUKeyedMap<DNARow> {

	private final IOTUKeyedMapPlus<DNARow, DNAMatrix, OTUDNARowPair> rows = new OTUKeyedMapPlus<DNARow, DNAMatrix, OTUDNARowPair>();

	public void accept(final IVisitor visitor) {
		rows.accept(visitor);
	}

	public void afterUnmarshal() {
		rows.afterUnmarshal();
	}

	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		rows.afterUnmarshal(u, parent);
	}

	public boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {
		getOTUSomethingPairs().clear();
		for (final Map.Entry<OTU, DNARow> otuToRow : getValues()
				.entrySet()) {
			getOTUSomethingPairs().add(
					OTUDNARowPair.of(otuToRow.getKey(), otuToRow
							.getValue()));
		}
		return true;
	}

	public IOTUKeyedMapPlus<DNARow, DNAMatrix, OTUDNARowPair> clear() {
		return rows.clear();
	}

	public DNARow get(final OTU key) {
		return rows.get(key);
	}

	@XmlElement(name = "otuRowPair")
	@Transient
	public Set<OTUDNARowPair> getOTUSomethingPairs() {
		return rows.getOTUSomethingPairs();
	}

	@Parent
	public DNAMatrix getParent() {
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
	@JoinTable(inverseJoinColumns = @JoinColumn(name = DNARow.JOIN_COLUMN))
	@MapKeyJoinColumn(name = OTU.JOIN_COLUMN)
	public Map<OTU, DNARow> getValues() {
		return rows.getValues();
	}

	public DNARow put(final OTU key, final DNARow value) {
		return rows.put(key, value);
	}

	public DNARows setOTUs() {
		rows.setOTUs();
		return this;
	}

	public DNARows setParent(final DNAMatrix parent) {
		rows.setParent(parent);
		return this;
	}

	public DNARows setValues(
			final Map<OTU, DNARow> values) {
		rows.setValues(values);
		return this;
	}
}
