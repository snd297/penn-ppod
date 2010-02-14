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
import java.util.Map;

/**
 * @author Sam Donnelly
 */
public abstract class MolecularStateMatrix extends CharacterStateMatrix {

	/**
	 * Set the {@code Character} at {@code characterIdx}.
	 * <p>
	 * If {@code getCharacters().size() <= characterIdx}, then this method pads
	 * {@code getCharacters()} with {@code character}. NOTE: this is because in
	 * a {@code MolecularStateMatrix} all characters are the same instance.
	 * <p>
	 * This method is does not reorder the columns of the matrix, unlike {@code
	 * setOTUs(List)} which reorders rows.
	 * 
	 * @param characterIdx index
	 * @param character value
	 * 
	 * @return the {@code Character} previously at that position or {@code null}
	 *         if there was no such {@code Character}
	 */
	@Override
	public MolecularStateMatrix setCharacters(
			final List<Character> dnaCharacters) {

		checkNotNull(dnaCharacters);

		if (getCharacters().size() > 0) {
			for (final Character dnaCharacter : dnaCharacters) {
				checkArgument(dnaCharacter.equals(dnaCharacters.get(0)),
						"all characters must be .equals() in a molecular matrix");
			}
		}

		while (getCharacters().size() < dnaCharacters.size()) {
			getCharactersMutable().add(dnaCharacters.get(0));
			getColumnPPodVersionInfosMutable().add(null);
		}

		dnaCharacters.get(0).addMatrix(this);

		// the matrix has changed
		resetPPodVersionInfo();

		// Try to stick with the contract even though it's a little funny
		return this;
	}

	@Override
	public Map<Character, Integer> getCharacterIdx() {
		throw new UnsupportedOperationException(
				"character index is not supported for a MolecularMatrix since all characters are the same instance.");
	}
}
