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

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;

import edu.upenn.cis.ppod.util.OTUCharacterStateRowPair;
import edu.upenn.cis.ppod.util.OTUSomethingPair;

/**
 * Maps {@code OTU}s to {@code CharacterStateRow}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = "OTUS_TO_CHARACTER_STATE_ROWS")
public class OTUsToCharacterStateRows extends
		OTUKeyedBimap<CharacterStateRow, CharacterStateMatrix> {
	/**
	 * The rows of the matrix. We don't do save_update cascades since we want to
	 * control when rows are added to the persistence context. We sometimes
	 * don't want the rows saved or reattached when the the matrix is.
	 */
	@org.hibernate.annotations.CollectionOfElements
	@org.hibernate.annotations.MapKeyManyToMany(joinColumns = @JoinColumn(name = OTU.ID_COLUMN))
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private final Map<OTU, CharacterStateRow> rows = newHashMap();

	/**
	 * For marshalling {@code rows}. Since a {@code Map}'s key couldn't be an
	 * {@code XmlIDREF} in JAXB - at least not easily.
	 */
	@Transient
	private final Set<OTUCharacterStateRowPair> otuRowPairs = newHashSet();

	OTUsToCharacterStateRows() {}

	public boolean beforeMarshal(@Nullable final Marshaller marshaller) {
		getOTURowPairs().clear();
		for (final Map.Entry<OTU, CharacterStateRow> otuToRow : getOTUsToValues()
				.entrySet()) {
			getOTURowPairs().add(
					OTUCharacterStateRowPair.of(otuToRow.getKey(), otuToRow
							.getValue()));
		}
		return true;
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

	@Override
	public CharacterStateRow put(final OTU otu, final CharacterStateRow newRow,
			final CharacterStateMatrix matrix) {
		newRow.setMatrix(matrix);
		final CharacterStateRow originalRow = super.putHelper(otu, newRow,
				matrix);
		if (originalRow != null && originalRow != newRow) {
			originalRow.setMatrix(null);
		}
		return originalRow;
	}

}
