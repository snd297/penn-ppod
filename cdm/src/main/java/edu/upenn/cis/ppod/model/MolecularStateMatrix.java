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
	 * Set all of the characters of the molecular matrix. All of the characters
	 * must be {@code .equals} to each other and must be
	 * {@link MolecularCharacter}s.
	 * <p>
	 * Because they could be proxies, this method does not verify that all
	 * members of {@code newMolecularCharacters} are of the correct type.
	 * <p>
	 * Assumes that none of {@code newMolecularCharacters} are detached.
	 * 
	 * @param newMolecularCharacters the new {@code MolecularCharacter}s to be
	 *            added.
	 * 
	 * @return this
	 * 
	 * @throws IllegalArgumentException if all of {@code dnaCharacters} are not
	 *             {@code .equal} to each other
	 */
	@Override
	public MolecularStateMatrix setCharacters(
			final List<? extends Character> newMolecularCharacters) {

		checkNotNull(newMolecularCharacters);

		for (final Character dnaCharacter : newMolecularCharacters) {
			checkArgument(dnaCharacter.equals(newMolecularCharacters.get(0)),
					"all characters must be .equals() in a molecular matrix");
		}

		while (getCharacters().size() < newMolecularCharacters.size()) {
			getCharactersMutable().add(newMolecularCharacters.get(0));
			getColumnPPodVersionInfosMutable().add(null);
		}

		newMolecularCharacters.get(0).addMatrix(this);

		// the matrix has changed
		resetPPodVersionInfo();

		return this;
	}

	@Override
	public Map<Character, Integer> getCharacterIdx() {
		throw new UnsupportedOperationException(
				"character index is not supported for a MolecularMatrix since all characters are the same instance.");
	}
}
