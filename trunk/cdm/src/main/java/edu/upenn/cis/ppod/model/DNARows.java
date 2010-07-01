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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;

import edu.upenn.cis.ppod.util.OTUDNARowPair;
import edu.upenn.cis.ppod.util.OTUSomethingPair;

/**
 * Maps {@link OTU}s to {@link DNARow}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNARows.TABLE)
public class DNARows extends OTUKeyedMap<DNARow> {

	public static final String TABLE = "DNA_ROWS";

	public static final String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "rows", optional = false)
	@CheckForNull
	private DNAMatrix matrix;

	/**
	 * For marshalling {@code rows}. Since a {@code Map}'s key couldn't be an
	 * {@code XmlIDREF} in JAXB - at least not easily.
	 */
	@Transient
	private final Set<OTUDNARowPair> otuRowPairs = newHashSet();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
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
			otuRowPair.getSecond().setRows(this);
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
		return matrix;
	}

	@Override
	public DNARow put(final OTU otu, final DNARow row) {
		checkNotNull(otu);
		checkNotNull(row);
		row.setRows(this);
		return super.putHelper(otu, row);
	}

	@Override
	protected DNARows setInNeedOfNewVersion() {
		if (getParent() != null) {
			getParent().setInNeedOfNewVersion();
		}
		return this;
	}

	protected DNARows setMatrix(final DNAMatrix matrix) {
		this.matrix = matrix;
		return this;
	}

}
