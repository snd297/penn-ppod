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
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;

import edu.upenn.cis.ppod.util.OTUStandardRowPair;
import edu.upenn.cis.ppod.util.OTUSomethingPair;

/**
 * Maps {@code OTU}s to {@code CharacterStateRow}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = "OTUS_TO_CHARACTER_STATE_ROWS")
public class OTUsToStandardRows extends OTUKeyedMap<StandardRow> {

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "otusToRows")
	@CheckForNull
	private StandardMatrix matrix;

	/**
	 * For marshalling {@code rows}. Since a {@code Map}'s key couldn't be an
	 * {@code XmlIDREF} in JAXB - at least not easily.
	 */
	@Transient
	private final Set<OTUStandardRowPair> otuRowPairs = newHashSet();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@MapKeyJoinColumn(name = OTU.JOIN_COLUMN)
	private final Map<OTU, StandardRow> rows = newHashMap();

	OTUsToStandardRows() {}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@Override
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		super.afterUnmarshal(u, parent);
		setMatrix((StandardMatrix) parent);
		for (final OTUSomethingPair<StandardRow> otuRowPair : otuRowPairs) {
			otuRowPair.getSecond().setOTUsToRows(this);
		}
	}

	public boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {
		getOTURowPairs().clear();
		for (final Map.Entry<OTU, StandardRow> otuToRow : getOTUsToValues()
				.entrySet()) {
			getOTURowPairs().add(
					OTUStandardRowPair.of(otuToRow.getKey(), otuToRow
							.getValue()));
		}
		return true;
	}

	@XmlElement(name = "otuRowPair")
	protected Set<OTUStandardRowPair> getOTURowPairs() {
		return otuRowPairs;
	}

	@Override
	protected Map<OTU, StandardRow> getOTUsToValues() {
		return rows;
	}

	@Override
	protected Set<OTUSomethingPair<StandardRow>> getOTUValuePairs() {
		final Set<OTUSomethingPair<StandardRow>> otuValuePairs = newHashSet();
		for (final OTUStandardRowPair otuRowPair : otuRowPairs) {
			otuValuePairs.add(otuRowPair);
		}
		return otuValuePairs;
	}

	@Nullable
	@Override
	protected StandardMatrix getParent() {
		return matrix;
	}

	@Override
	public StandardRow put(final OTU otu, final StandardRow row) {
		checkNotNull(otu);
		checkNotNull(row);
		row.setOTUsToRows(this);
		final StandardRow originalRow = super.put(otu, row);
		if (originalRow != null && originalRow != row) {
			originalRow.setOTUsToRows(null);
		}
		return originalRow;
	}

	@Override
	protected OTUsToStandardRows setInNeedOfNewPPodVersionInfo() {
		if (matrix != null) {
			matrix.setInNeedOfNewPPodVersionInfo();
		}
		return this;
	}

	protected OTUsToStandardRows setMatrix(
			final StandardMatrix matrix) {
		checkNotNull(matrix);
		this.matrix = matrix;
		return this;
	}
}
