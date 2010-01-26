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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

/**
 * @author Sam Donnelly
 * 
 */
public class MolecularMatrix extends CharacterStateMatrix {

	/**
	 * Set the {@code Character} at {@code characterIdx}.
	 * <p>
	 * If {@code getCharacters().size() <= characterIdx}, then this method pads
	 * {@link #getCharacters()} with {@code null}s.
	 * <p>
	 * If {@code character} was already contained in this matrix, then its
	 * former position is filled in with {@code null}.
	 * <p>
	 * This method is does not reorder the columns of the matrix, unlike
	 * {@link #setOTUs(List)} which reorders rows.
	 * 
	 * @param characterIdx index
	 * @param character value
	 * 
	 * @return the {@code Character} previously at that position or {@code null}
	 *         if there was no such {@code Character}
	 */
	public MolecularCharacter setCharacter(final int characterIdx,
			final Character character) {
		checkNotNull(character);
		checkArgument(character instanceof DNACharacter,
				"character must be a DNACharacer for a DNAMatrix");
		if (getCharacters().size() > 0) {
			checkArgument(character.equals(getCharacters().get(0)),
					"all characters must be .equals() in a molecular matrix");
		}
		final MolecularCharacter molecularCharacter = (MolecularCharacter) character;
		if (getCharacters().size() > characterIdx
				&& character.equals(getCharacters().get(characterIdx))) {
			// Nothing to do
			return molecularCharacter;
		}

		boolean addedNewCharacters = false;
		while (getCharacters().size() <= characterIdx) {
			getCharactersMutable().add(molecularCharacter);
			getColumnPPodVersionInfosMutable().add(null);
			addedNewCharacters = true;
		}

		getCharacterIdxMutable().put(character, characterIdx);
		character.addMatrix(this);

		// the matrix has changed
		resetPPodVersionInfo();

		// Try to stick with the contract even though it's a little funny
		return addedNewCharacters ? null : molecularCharacter;
	}
}
