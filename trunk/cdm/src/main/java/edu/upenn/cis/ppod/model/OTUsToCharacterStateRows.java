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

/**
 * @author Sam Donnelly
 */
@Embeddable
public class OTUsToCharacterStateRows extends
		OTUKeyedMap<CharacterStateRow, CharacterStateMatrix> {

	@org.hibernate.annotations.CollectionOfElements
	@org.hibernate.annotations.MapKeyManyToMany(joinColumns = @JoinColumn(name = OTU.ID_COLUMN))
	private final Map<OTU, CharacterStateRow> rows = newHashMap();

	@OneToOne(fetch = FetchType.LAZY)
	private CharacterStateMatrix matrix;

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

	@Override
	protected OTUKeyedMap<CharacterStateRow, CharacterStateMatrix> setParent(
			final CharacterStateMatrix owner) {
		this.matrix = owner;
		return this;
	}
}
