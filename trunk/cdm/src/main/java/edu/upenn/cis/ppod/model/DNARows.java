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
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.annotations.Parent;

import edu.upenn.cis.ppod.util.OTUDNARowPair;
import edu.upenn.cis.ppod.util.OTUSomethingPair;

/**
 * Maps {@link OTU}s to {@link DNARow}s.
 * 
 * @author Sam Donnelly
 */
@Embeddable
public class DNARows extends OTUKeyedMap<DNARow> {

	@Parent
	private DNAMatrix matrix;

	/**
	 * For marshalling {@code rows}. Since a {@code Map}'s key couldn't be an
	 * {@code XmlIDREF} in JAXB - at least not easily.
	 */
	@Transient
	private final Set<OTUDNARowPair> otuRowPairs = newHashSet();

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
	private final Map<OTU, DNARow> rows = newHashMap();

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		setMatrix((DNAMatrix) parent);
		for (final OTUSomethingPair<DNARow> otuRowPair : otuRowPairs) {
			otuRowPair.getSecond().setMatrix(getParent());
		}
	}

	public boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {
		getOTURowPairs().clear();
		for (final Map.Entry<OTU, DNARow> otuToRow : getOTUsToValues()
				.entrySet()) {
			getOTURowPairs().add(
					OTUDNARowPair.of(otuToRow.getKey(), otuToRow
							.getValue()));
		}
		return true;
	}

	/**
	 * Hibernate requires this method.
	 */
	private DNAMatrix getMatrix() {
		return matrix;
	}

	@XmlElement(name = "otuRowPair")
	protected Set<OTUDNARowPair> getOTURowPairs() {
		return otuRowPairs;
	}

	@Override
	protected Map<OTU, DNARow> getOTUsToValues() {
		return rows;
	}

	@Override
	protected Set<OTUSomethingPair<DNARow>> getOTUValuePairs() {
		final Set<OTUSomethingPair<DNARow>> otuValuePairs = newHashSet();
		for (final OTUDNARowPair otuRowPair : otuRowPairs) {
			otuValuePairs.add(otuRowPair);
		}
		return otuValuePairs;
	}

	@Override
	protected DNAMatrix getParent() {
		return getMatrix();
	}

	@Override
	public DNARow put(final OTU otu, final DNARow row) {
		checkNotNull(otu);
		checkNotNull(row);
		row.setMatrix(getParent());
		return super.putHelper(otu, row);
	}

	/**
	 * Intentionally package-private.
	 * <p>
	 * Hibernate requires that this method is called "setMatrix". Otherwise,
	 * we'd call it "setParent" for symmetry w/ {@link getParent()}.
	 * 
	 * @param matrix the owning matrix.
	 */
	void setMatrix(final DNAMatrix matrix) {
		checkNotNull(matrix);
		this.matrix = matrix;
	}
}
