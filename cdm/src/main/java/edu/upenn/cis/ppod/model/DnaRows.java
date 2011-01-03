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
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.annotations.Parent;

import edu.upenn.cis.ppod.imodel.IDnaMatrix;
import edu.upenn.cis.ppod.imodel.IDnaRow;
import edu.upenn.cis.ppod.imodel.IOtu;
import edu.upenn.cis.ppod.imodel.IOtuKeyedMap;
import edu.upenn.cis.ppod.imodel.IOtuKeyedMapPlus;
import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.OtuDnaRowPair;

/**
 * Maps {@link OTU}s to {@link DNARow}s.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@Embeddable
@Access(AccessType.PROPERTY)
public class DnaRows implements IOtuKeyedMap<IDnaRow> {

	private final IOtuKeyedMapPlus<IDnaRow, IDnaMatrix, OtuDnaRowPair> rows =
			new OTUKeyedMapPlus<IDnaRow, IDnaMatrix, OtuDnaRowPair>();

	/** {@inheritDoc} */
	public void accept(final IVisitor visitor) {
		rows.accept(visitor);
	}

	/** {@inheritDoc} */
	public void afterUnmarshal() {
		rows.afterUnmarshal();
	}

	protected void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent) {
		// Don't checkNotNull(parent) since JAXB is the caller and we can't
		// control what it does
		rows.afterUnmarshal((DnaMatrix) parent);
	}

	protected boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {
		getOTUSomethingPairs().clear();
		for (final Map.Entry<IOtu, IDnaRow> otuToRow : getValues()
				.entrySet()) {
			getOTUSomethingPairs().add(
					new OtuDnaRowPair(otuToRow.getKey(), otuToRow
							.getValue()));
		}
		return true;
	}

	public void clear() {
		rows.clear();
	}

	/** {@inheritDoc} */
	public IDnaRow get(final IOtu key) {
		return rows.get(key);
	}

	@XmlElement(name = "otuRowPair")
	@Transient
	public Set<OtuDnaRowPair> getOTUSomethingPairs() {
		return rows.getOTUSomethingPairs();
	}

	@Parent
	public IDnaMatrix getParent() {
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
			orphanRemoval = true,
			targetEntity = DnaRow.class)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = DnaRow.JOIN_COLUMN))
	@MapKeyJoinColumn(name = Otu.JOIN_COLUMN)
	@MapKeyClass(Otu.class)
	public Map<IOtu, IDnaRow> getValues() {
		return rows.getValues();
	}

	/** {@inheritDoc} */
	public IDnaRow put(final IOtu key, final IDnaRow value) {
		return rows.put(key, value);
	}

	/** {@inheritDoc} */
	public void updateOTUs() {
		rows.updateOTUs();
	}

	/**
	 * Set the owner of this object.
	 * 
	 * @param parent the owner
	 */
	public void setParent(final IDnaMatrix parent) {
		rows.setParent(parent);
	}

	/** {@inheritDoc} */
	public void setValues(
			final Map<IOtu, IDnaRow> values) {
		rows.setValues(values);
	}
}
