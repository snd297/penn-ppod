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
 * @author Sam Donnelly
 */
@Entity
@Table(name = "OTUS_TO_CHARACTER_STATE_ROWS")
public class OTUsToCharacterStateRows extends
		OTUKeyedMap<CharacterStateRow, CharacterStateMatrix> {
	/**
	 * The otusToRows of the matrix. We don't do save_update cascades since we
	 * want to control when otusToRows are added to the persistence context. We
	 * sometimes don't want the otusToRows saved or reattached when the the
	 * matrix is.
	 */
	@org.hibernate.annotations.CollectionOfElements
	@org.hibernate.annotations.MapKeyManyToMany(joinColumns = @JoinColumn(name = OTU.ID_COLUMN))
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private final Map<OTU, CharacterStateRow> otusToRows = newHashMap();

	OTUsToCharacterStateRows() {}

	public boolean beforeMarshal(@Nullable final Marshaller marshaller) {
		for (final Map.Entry<OTU, CharacterStateRow> otuToRow : getOTUsToValuesModifiable()
				.entrySet()) {
			getOTURowPairsModifiable().add(
					OTUCharacterStateRowPair.of(otuToRow.getKey(), otuToRow
							.getValue()));
		}
		return true;
	}

	@Override
	protected Map<OTU, CharacterStateRow> getOTUsToValuesModifiable() {
		return otusToRows;
	}

	@XmlElement(name = "otuRowPair")
	private Set<OTUCharacterStateRowPair> getOTURowPairsModifiable() {
		return otuRowPairs;
	}

	/**
	 * For marshalling {@code otusToRows}. Since a {@code Map}'s key couldn't be
	 * an {@code XmlIDREF} in JAXB - at least not easily.
	 */
	@Transient
	private final Set<OTUCharacterStateRowPair> otuRowPairs = newHashSet();

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

	@Override
	protected Set<OTUSomethingPair<CharacterStateRow>> getOTUValuePairsModifiable() {
		final Set<OTUSomethingPair<CharacterStateRow>> otuValuePairs = newHashSet();
		for (final OTUCharacterStateRowPair otuRowPair : otuRowPairs) {
			otuValuePairs.add(otuRowPair);
		}
		return otuValuePairs;
	}

}
