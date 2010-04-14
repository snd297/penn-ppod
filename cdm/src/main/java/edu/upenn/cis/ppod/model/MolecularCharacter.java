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

import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.MappedSuperclass;

/**
 * A molecular character is different from a {@code Character} in that there
 * should exist exactly one of each type in a pPOD database. For example, there
 * should be exactly one {@link DNACharacter} and exactly one
 * {@link RNACharacter} in the database.
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class MolecularCharacter extends Character {


	MolecularCharacter() {}

	/**
	 * Unlike {@link Character#addMatrix(CharacterStateMatrix)}, this allows for
	 * a {@code MolecularCharacter} to be added to more than one matrix.
	 */
	@Override
	protected boolean addMatrix(final CharacterStateMatrix matrix) {
		checkNotNull(matrix);
		return getMatrices().add(matrix);
	}

	protected MolecularCharacter setMolecularCharacterLabel(
			final String molecularCharacterLabel) {
		super.setLabel(molecularCharacterLabel);
		//this.molecularCharacterLabel = molecularCharacterLabel;
		return this;
	}

}
