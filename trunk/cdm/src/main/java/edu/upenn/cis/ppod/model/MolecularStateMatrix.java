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
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Sam Donnelly
 */
public abstract class MolecularStateMatrix extends CharacterStateMatrix {

	/**
	 * Set all of the characters of the molecular matrix.
	 * <p>
	 * All of the characters must be {@code .equals} to each other. This
	 * condition does not hold for
	 * {@link CharacterStateMatrix#setCharacters(List)}.
	 * <p>
	 * All of the characters must be {@link IDNACharacter}s. This condition does
	 * not hold for {@link CharacterStateMatrix#setCharacters(List)}.
	 * <p>
	 * Assumes that none of {@code newMolecularCharacters} are detached.
	 * <p>
	 * The return value will is only included for consistency with the
	 * overridden method.
	 * 
	 * @param newMolecularCharacters the new {@code MolecularCharacter}s to be
	 *            added
	 * 
	 * @return characters that were removed as a result of this operation -
	 *         these will all point to the same {@code MolecuarCharacter}
	 * 
	 * @throws IllegalArgumentException if all of {@code newMolecularCharacters}
	 *             are not {@code .equals} to each other
	 * @throws IllegalArgumentException if any of {@code newMolecularCharacter}
	 *             are not {@code IDNACharacter}s
	 */
	@Override
	public List<Character> setCharacters(
			final List<? extends Character> newMolecularCharacters) {

		checkNotNull(newMolecularCharacters);

		for (final Character newMolecularCharacter : newMolecularCharacters) {
			checkArgument(newMolecularCharacter.equals(newMolecularCharacters
					.get(0)),
					"all characters must be .equals() in a molecular matrix");
			checkArgument(newMolecularCharacter instanceof IDNACharacter,
					"all newMolecularCharacters must be IDNACharacter's, found a "
							+ newMolecularCharacter.getClass().getName());
		}

		if (getCharacters().size() == newMolecularCharacters.size()) {
			// They are the same, nothing to do
			return Collections.emptyList();
		}

		if (getCharacters().size() != getColumnPPodVersionInfos().size()) {
			throw new AssertionError(
					"programming error: getCharacters() and getColumnPPodVersionInfos() should always be the same size");
		}

		final List<Character> removedCharacters = newArrayList();

		for (int i = 0; i < getCharacters().size()
				- newMolecularCharacters.size(); i++) {
			removedCharacters.add(getCharacters().get(0));
		}

		while (getCharacters().size() < newMolecularCharacters.size()) {
			getCharactersModifiable().add(newMolecularCharacters.get(0));
			getColumnPPodVersionInfosModifiable().add(null);
		}

		while (getCharacters().size() > newMolecularCharacters.size()) {
			getCharactersModifiable().remove(getCharacters().size() - 1);
			getColumnPPodVersionInfosModifiable().remove(
					getColumnPPodVersionInfos().size() - 1);

		}

		newMolecularCharacters.get(0).addMatrix(this);

		// the matrix has changed
		resetPPodVersionInfo();

		return removedCharacters;
	}

	@Override
	public Map<Character, Integer> getCharacterIdx() {
		throw new UnsupportedOperationException(
				"character index is not supported for a MolecularMatrix since all characters are the same instance.");
	}
}
