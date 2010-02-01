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
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * @author Sam Donnelly
 */
@MappedSuperclass
public class MolecularCharacter extends Character {

	/**
	 * This column should be the same as {@link Character#getLabel()} and is
	 * only here to prevent duplicate {@code DNACharacter}s from being added to
	 * the table. Duplicates are prevented by the {@code nullable = false,
	 * unique = true} combination.
	 * <p>
	 * We were just calling this column {@code "LABEL"}, but that seemed to
	 * break {@link Character#getLabel()} - it would return {@code null} after
	 * db retrieval. Because {@code Character} has a column called {@code
	 * "LABEL"}?
	 */
	@Column(name = "MOLECULAR_CHARACTER_LABEL", nullable = false, unique = true)
	@SuppressWarnings("unused")
	private String molecularCharacterLabel;

	protected MolecularCharacter setMolecularCharacterLabel(
			final String molecularCharacterLabel) {
		super.setLabel(molecularCharacterLabel);
		this.molecularCharacterLabel = molecularCharacterLabel;
		return this;
	}

}
