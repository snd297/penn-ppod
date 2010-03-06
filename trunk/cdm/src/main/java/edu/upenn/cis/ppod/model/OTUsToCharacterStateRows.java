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

import java.util.Map;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * @author Sam Donnelly
 */
@Embeddable
public class OTUsToCharacterStateRows extends
		OTUKeyedMap<CharacterStateRow, CharacterStateMatrix> {
	/**
	 * The rows of the matrix. We don't do save_update cascades since we want to
	 * control when otusToRows are added to the persistence context. We
	 * sometimes don't want the otusToRows saved or reattached when the the
	 * matrix is.
	 */
	@org.hibernate.annotations.CollectionOfElements
	@org.hibernate.annotations.MapKeyManyToMany(joinColumns = @JoinColumn(name = OTU.ID_COLUMN))
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private final Map<OTU, CharacterStateRow> rows = newHashMap();

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = CharacterStateMatrix.ID_COLUMN)
	private CharacterStateMatrix matrix;

	OTUsToCharacterStateRows() {}

	@XmlElementWrapper(name = "rows")
	@Override
	protected Map<OTU, CharacterStateRow> getItemsModifiable() {
		return rows;
	}

	/**
	 * Get the matrix.
	 * 
	 * @return the matrix
	 */
	@Override
	public CharacterStateMatrix getParent() {
		return matrix;
	}

	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		setParent((CharacterStateMatrix) parent);
	}

	@Override
	protected OTUKeyedMap<CharacterStateRow, CharacterStateMatrix> setParent(
			final CharacterStateMatrix parent) {
		this.matrix = parent;
		return this;
	}
}
