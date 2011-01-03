/*
I * Copyright (C) 2010 Trustees of the University of Pennsylvania
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

import edu.upenn.cis.ppod.imodel.IOtu;
import edu.upenn.cis.ppod.imodel.IOTUKeyedMap;
import edu.upenn.cis.ppod.imodel.IOTUKeyedMapPlus;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStandardRow;
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
		implements IOTUKeyedMap<IStandardRow> {

	private final IOTUKeyedMapPlus<IStandardRow, IStandardMatrix, OTUStandardRowPair> rows =
			new OTUKeyedMapPlus<IStandardRow, IStandardMatrix, OTUStandardRowPair>();

	StandardRows() {}

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
		// Don't do checkNotNull(parent) since this is called by JAXB and we
		// can't control it
		rows.afterUnmarshal((IStandardMatrix) parent);
	}

	protected boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {
		getOTUSomethingPairs().clear();
		for (final Map.Entry<IOtu, IStandardRow> otuToRow : getValues()
				.entrySet()) {
			getOTUSomethingPairs().add(
					new OTUStandardRowPair(otuToRow.getKey(),
							otuToRow.getValue()));
		}
		return true;
	}

	public void clear() {
		rows.clear();
	}

	public IStandardRow get(final IOtu key) {
		return rows.get(key);
	}

	@XmlElement(name = "otuRowPair")
	@Transient
	public Set<OTUStandardRowPair> getOTUSomethingPairs() {
		return rows.getOTUSomethingPairs();
	}

	@Parent
	public IStandardMatrix getParent() {
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
			orphanRemoval = true,
			targetEntity = StandardRow.class)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = StandardRow.JOIN_COLUMN))
	@MapKeyJoinColumn(name = OtuChangeCase.JOIN_COLUMN)
	@MapKeyClass(OtuChangeCase.class)
	public Map<IOtu, IStandardRow> getValues() {
		return rows.getValues();
	}

	/** {@inheritDoc} */
	public IStandardRow put(final IOtu key, final IStandardRow value) {
		return rows.put(key, value);
	}

	/** {@inheritDoc} */
	public void updateOTUs() {
		rows.updateOTUs();
	}

	/**
	 * Set the owner of this {@code StandardRows}.
	 * 
	 * @param parent the owner
	 */
	public void setParent(final IStandardMatrix parent) {
		rows.setParent(parent);
	}

	@SuppressWarnings("unused")
	private void setValues(final Map<IOtu, IStandardRow> values) {
		rows.setValues(values);
	}

}
