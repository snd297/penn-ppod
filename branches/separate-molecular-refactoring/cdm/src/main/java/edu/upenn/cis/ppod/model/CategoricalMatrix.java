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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Maps.newHashMap;
import static edu.upenn.cis.ppod.util.CollectionsUtil.nullFillAndSet;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnegative;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import edu.upenn.cis.ppod.util.IVisitor;

@Entity
@Table(name = CategoricalMatrix.TABLE)
public class CategoricalMatrix extends CharacterStateMatrix {

	static final String TABLE = "CATEGORICAL_MATRIX";

	/**
	 * The inverse of {@link #characters}. So it's a {@code AbstractCharacter}
	 * ->columnNumber lookup.
	 */
	@ElementCollection
	@JoinTable(name = TABLE + "_" + CHARACTER_IDX_COLUMN, joinColumns = @JoinColumn(name = ID_COLUMN))
	@MapKeyJoinColumn(name = PPodEntity.ID_COLUMN)
	@Column(name = CHARACTER_IDX_COLUMN)
	private final Map<Character, Integer> charactersToPositions = newHashMap();

	/**
	 * The position of a {@code Character} in <code>characters</code> signifies
	 * its column number in each <code>row</cod>. So <code>characters</code> is
	 * a columnNumber-> <code>Character</code> lookup.
	 * <p>
	 * This relationship is really only many-to-many for {@code MolecularMatrix}
	 * s, for character matrices it is one-to-many and a refactoring should be
	 * considered.
	 */
	@OneToMany(mappedBy = "matrix")
	private final List<Character> characters = newArrayList();

	@Override
	public void accept(final IVisitor visitor) {
		for (final Character abstractCharacter : getCharacters()) {
			abstractCharacter.accept(visitor);
		}
		super.accept(visitor);
		visitor.visit(this);
	}

	@Override
	public void afterUnmarshal() {
		super.afterUnmarshal();
		int i = -1;
		for (final Character character : getCharacters()) {
			i++;
			charactersToPositions.put(character, i);
			character.setMatrix(this);
		}
	}

	private List<PPodVersionInfo> determineNewColumnHeaderPPodVersionInfos(
			final List<? extends Character> newCharacters) {

		final BiMap<Integer, Integer> originalPositionsToNewPositions = HashBiMap
				.create(getColumnsSize());
		for (int originalPosition = 0; originalPosition < getColumnsSize(); originalPosition++) {
			final Character originalCharacter = getCharacters().get(
					originalPosition);
			final Integer newPosition = newCharacters
					.indexOf(originalCharacter);
			// Use unique negative values to indicate not present. Unique since
			// this is a BiMap
			originalPositionsToNewPositions.put(originalPosition,
					newPosition == -1 ? -(originalPosition + 1) : newPosition);
		}
		final List<PPodVersionInfo> newColumnHeaderPPodVersionInfos = newArrayListWithCapacity(newCharacters
				.size());
		for (final Entry<Integer, Integer> originalPositionToNewPosition : originalPositionsToNewPositions
				.entrySet()) {
			final Integer originalPosition = originalPositionToNewPosition
					.getKey();
			final Integer newPosition = originalPositionToNewPosition
					.getValue();
			if (newPosition < 0) {
				// The character has been removed, nothing to do
			} else {
				nullFillAndSet(newColumnHeaderPPodVersionInfos, newPosition,
						getColumnPPodVersionInfos().get(originalPosition));
			}
		}

		final Map<Integer, Integer> newPositionsByOriginalPositions = originalPositionsToNewPositions
				.inverse();
		// Now we add in null values for newly added characters
		for (int newCharacterPosition = 0; newCharacterPosition < newCharacters
				.size(); newCharacterPosition++) {
			if (null == newPositionsByOriginalPositions
					.get(newCharacterPosition)) {
				nullFillAndSet(newColumnHeaderPPodVersionInfos,
						newCharacterPosition, null);
			}
		}
		return newColumnHeaderPPodVersionInfos;
	}

	/**
	 * Get the character at the given position.
	 * 
	 * @param characterPosition the character's position
	 * 
	 * @return the character at the given position\
	 * 
	 * @throws IndexOutOfBoundsException if {@code characterPosition} is out of
	 *             bounds
	 */
	public Character getCharacter(
			@Nonnegative final int characterPosition) {
		return getCharacters().get(characterPosition);
	}

	/**
	 * Created for testing.
	 */
	Map<Character, Integer> getCharacterPosition() {
		return charactersToPositions;
	}

	/**
	 * Get the position of a character in this matrix, or {@code null} if the
	 * character is not in this matrix.
	 * 
	 * @param character the character who's position we want
	 * 
	 * @return the position of a character in this matrix, or {@code null} if
	 *         the character is not in this matrix
	 */
	public Integer getCharacterPosition(
			final Character character) {
		return charactersToPositions.get(character);
	}

	/**
	 * Get the characters.
	 * 
	 * @return a modifiable reference to the characters
	 */
	@XmlElementWrapper(name = "characters")
	@XmlElement(name = "character")
	protected List<Character> getCharacters() {
		return characters;
	}

	/**
	 * Iterates over this matix's characters in column order.
	 * 
	 * @return an iterator that iterates over this matix's characters in column
	 *         order
	 */
	public Iterator<Character> getCharactersIterator() {
		return Collections.unmodifiableList(getCharacters()).iterator();
	}

	@Override
	public int getColumnsSize() {
		return getCharacters().size();
	}

	/**
	 * Set the characters.
	 * <p>
	 * This method is does not reorder the columns of the matrix, unlike
	 * {@link #setOTUSet(OTUSet)} which reorders the rows. Reordering definitely
	 * does not makes sense in a {@link MolecularMatrix} since all of the
	 * characters will be the same instance.
	 * <p>
	 * This method does reorder {@link #getColumnPPodVersionInfos()}.
	 * <p>
	 * It is legal for two characters to have the same label, but not to be
	 * {@code .equals} to each other.
	 * 
	 * @param newCharacters the new characters
	 * 
	 * @return the characters removed as a result of this operation
	 * 
	 * @throws IllegalArgumentException if any of {code newCharacters} is
	 *             {@code null}
	 * @throws IllegalArgumentException if any of {@code newCharacters} are
	 *             {@code .equals} to each other.
	 */
	public List<Character> setCharacters(
			final List<? extends Character> newCharacters) {
		checkNotNull(newCharacters);

		if (newCharacters.equals(getCharacters())) {
			return Collections.emptyList();
		}

		int newCharacterPos = -1;
		for (final Character newCharacter : newCharacters) {
			newCharacterPos++;
			checkArgument(newCharacter != null, "newCharacters["
												+ newCharacterPos
												+ "] is null");

			for (final Iterator<? extends Character> itr = newCharacters
					.listIterator(newCharacterPos + 1); itr
					.hasNext();) {
				final Character character2 = itr.next();
				checkArgument(!newCharacter.equals(character2),
						"two characters are the same "
								+ newCharacter.getLabel()
								+ " at positions "
								+ newCharacters.indexOf(newCharacter) + " and "
								+ newCharacters.indexOf(character2));
			}
		}

		final List<PPodVersionInfo> newColumnPPodVersionInfos = determineNewColumnHeaderPPodVersionInfos(newCharacters);
		getColumnPPodVersionInfos().clear();
		getColumnPPodVersionInfos().addAll(newColumnPPodVersionInfos);

		final List<Character> removedCharacters = newArrayList(getCharacters());

		removedCharacters.removeAll(newCharacters);
		for (final Character removedCharacter : removedCharacters) {
			removedCharacter.setMatrix(null);
		}

		getCharacters().clear();
		charactersToPositions.clear();

		getCharacters().addAll(newCharacters);

		int characterPosition = 0;
		for (final Character character : getCharacters()) {
			charactersToPositions.put(character, characterPosition++);
			character.setMatrix(null);
		}

		// the matrix has changed
		setInNeedOfNewPPodVersionInfo();
		return removedCharacters;
	}

}
