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

import edu.upenn.cis.ppod.modelinterfaces.IPPodVersionedWithOTUSet;
import edu.upenn.cis.ppod.util.OTUCharacterStateRowPair;
import edu.upenn.cis.ppod.util.OTUSomethingPair;

/**
 * Maps {@code OTU}s to {@code CharacterStateRow}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = "OTUS_TO_CHARACTER_STATE_ROWS")
public class OTUsToCharacterStateRows extends OTUsToRows<CharacterStateRow> {

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "otusToRows")
	@CheckForNull
	private CharacterStateMatrix matrix;

	/**
	 * For marshalling {@code rows}. Since a {@code Map}'s key couldn't be an
	 * {@code XmlIDREF} in JAXB - at least not easily.
	 */
	@Transient
	private final Set<OTUCharacterStateRowPair> otuRowPairs = newHashSet();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@MapKeyJoinColumn(name = OTU.ID_COLUMN)
	private final Map<OTU, CharacterStateRow> rows = newHashMap();

	OTUsToCharacterStateRows() {}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@Override
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		super.afterUnmarshal(u, parent);
		setMatrix((CharacterStateMatrix) parent);
		for (final OTUSomethingPair<CharacterStateRow> otuRowPair : otuRowPairs) {
			otuRowPair.getSecond().setOTUsToRows(this);
		}
	}

	public boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {
		getOTURowPairs().clear();
		for (final Map.Entry<OTU, CharacterStateRow> otuToRow : getOTUsToValues()
				.entrySet()) {
			getOTURowPairs().add(
					OTUCharacterStateRowPair.of(otuToRow.getKey(), otuToRow
							.getValue()));
		}
		return true;
	}

	@Nullable
	protected CharacterStateMatrix getMatrix() {
		return matrix;
	}

	@XmlElement(name = "otuRowPair")
	protected Set<OTUCharacterStateRowPair> getOTURowPairs() {
		return otuRowPairs;
	}

	@Override
	protected Map<OTU, CharacterStateRow> getOTUsToValues() {
		return rows;
	}

	@Override
	protected Set<OTUSomethingPair<CharacterStateRow>> getOTUValuePairs() {
		final Set<OTUSomethingPair<CharacterStateRow>> otuValuePairs = newHashSet();
		for (final OTUCharacterStateRowPair otuRowPair : otuRowPairs) {
			otuValuePairs.add(otuRowPair);
		}
		return otuValuePairs;
	}

	@Nullable
	@Override
	protected IPPodVersionedWithOTUSet getParent() {
		return matrix;
	}

	@Override
	public CharacterStateRow put(final OTU otu, final CharacterStateRow row) {
		checkNotNull(otu);
		checkNotNull(row);
		row.setOTUsToRows(this);
		final CharacterStateRow originalRow = super.put(otu, row);
		if (originalRow != null && originalRow != row) {
			originalRow.setOTUsToRows(null);
		}
		return originalRow;
	}

	@Override
	protected OTUsToCharacterStateRows setInNeedOfNewPPodVersionInfo() {
		if (matrix != null) {
			matrix.setInNeedOfNewPPodVersionInfo();
		}
		return this;
	}

	protected OTUsToCharacterStateRows setMatrix(
			final CharacterStateMatrix matrix) {
		checkNotNull(matrix);
		this.matrix = matrix;
		return this;
	}
}
