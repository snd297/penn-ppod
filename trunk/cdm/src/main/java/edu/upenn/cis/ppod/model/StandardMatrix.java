/*
 * Copyright (C) 2009 Trustees of the University of Pennsylvania
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
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static edu.upenn.cis.ppod.util.CollectionsUtil.nullFillAndSet;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import com.google.common.annotations.Beta;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A standard matrix - aka a character matrix.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StandardMatrix.TABLE)
public class StandardMatrix
		extends Matrix<StandardRow, StandardCell> {
	/** This entity's table name. */
	public static final String TABLE = "STANDARD_MATRIX";

	/**
	 * Name for foreign key columns that point at this table.
	 */
	public static final String JOIN_COLUMN = TABLE + "_ID";

	@OneToMany(
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = JOIN_COLUMN, nullable = false)
	private final List<StandardCharacter> characters = newArrayList();

	/**
	 * Non-final for JAXB.
	 */
	@Embedded
	private StandardRows rows = new StandardRows(this);

	/** No-arg constructor. */
	public StandardMatrix() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitStandardMatrix(this);
		for (final StandardCharacter character : getCharacters()) {
			character.accept(visitor);
		}
		super.accept(visitor);
	}

	/** {@inheritDoc} */
	public void addColumn(
			final int columnNo,
			final StandardCharacter character,
			final List<? extends StandardCell> cells) {
		checkNotNull(character);
		checkNotNull(cells);
		checkArgument(columnNo <= getColumnsSize(),
				"columnNo " + columnNo + " too big for matrix column size "
						+ getColumnsSize());
		checkArgument(columnNo >= 0, "columnNo is negative: " + columnNo);
		checkArgument(cells.size() == rows.getValues().size());

		final List<StandardCharacter> thisCharacters = newArrayList(getCharacters());
		thisCharacters.add(columnNo, character);
		setCharacters(thisCharacters);
		addColumn(columnNo, cells);
	}

	private List<VersionInfo> determineNewColumnHeaderPPodVersionInfos(
			final List<? extends StandardCharacter> newCharacters) {

		final BiMap<Integer, Integer> originalPositionsToNewPositions = HashBiMap
				.create(getColumnsSize());
		for (int originalPosition = 0; originalPosition < getCharacters()
				.size(); originalPosition++) {
			final StandardCharacter originalCharacter =
					getCharacters().get(originalPosition);
			final Integer newPosition = newCharacters
					.indexOf(originalCharacter);
			// Use unique negative values to indicate not present. Unique since
			// this is a BiMap
			originalPositionsToNewPositions.put(originalPosition,
					newPosition == -1 ? -(originalPosition + 1) : newPosition);
		}
		final List<VersionInfo> newColumnHeaderPPodVersionInfos =
				newArrayListWithCapacity(newCharacters.size());
		for (final Entry<Integer, Integer> originalPositionToNewPosition : originalPositionsToNewPositions
				.entrySet()) {
			final Integer originalPosition = originalPositionToNewPosition
					.getKey();
			final Integer newPosition = originalPositionToNewPosition
					.getValue();
			if (newPosition < 0) {
				// The character has been removed, nothing to do
			} else {
				nullFillAndSet(
						newColumnHeaderPPodVersionInfos,
						newPosition,
						getColumnVersionInfos().get(originalPosition));
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
	 * Get the characters contained in this matrix.
	 * 
	 * @return the characters contained in this matrix
	 */
	public List<StandardCharacter> getCharacters() {
		return Collections.unmodifiableList(characters);
	}

	/**
	 * Get a modifiable reference to this matrix's characters.
	 * 
	 * @return a modifiable reference to this matrix's characters
	 */
	protected List<StandardCharacter> getCharactersModifiable() {
		return characters;
	}

	/**
	 * Get the otusToRows.
	 * 
	 * @return the otusToRows
	 */
	@Override
	protected StandardRows getOtuKeyedRows() {
		return rows;
	}

	/**
	 * Remove the cells the make up the given column number.
	 * 
	 * @param columnNo the column to remove
	 * 
	 * @return the cells in the column
	 */
	@Beta
	public List<StandardCell> removeColumn(final int columnNo) {
		final List<StandardCharacter> characters = newArrayList(getCharacters());
		characters.remove(columnNo);
		setCharacters(characters);
		return super.removeColumnHelper(columnNo);
	}

	/**
	 * Set the characters.
	 * <p>
	 * This method is does not reorder the columns of the matrix because that is
	 * a potentially expensive operation - it could load the entire matrix into
	 * the persistence context.
	 * <p>
	 * This method does reorder {@link #getColumnVersionInfos()}.
	 * <p>
	 * It is legal for two characters to have the same label, but not to be
	 * {@code .equals} to each other.
	 * 
	 * @param characters the new characters
	 * 
	 * @throws IllegalArgumentException if any of {code newCharacters} is
	 *             {@code null}
	 * @throws IllegalArgumentException if any of {@code newCharacters} are
	 *             {@code .equals} to each other
	 * @throws IllegalStateExeption if {@code characters.size() !=
	 *             getColumnsSize()}
	 */
	public void setCharacters(final List<? extends StandardCharacter> characters) {
		checkNotNull(characters);

		if (characters.equals(getCharacters())) {
			return;
		}

		int newCharacterPos = -1;
		for (final StandardCharacter character : characters) {
			newCharacterPos++;
			checkArgument(character != null, "newCharacters["
												+ newCharacterPos
												+ "] is null");

			for (final Iterator<? extends StandardCharacter> itr = characters
					.listIterator(newCharacterPos + 1); itr
					.hasNext();) {
				final StandardCharacter character2 = itr.next();
				checkArgument(!character.equals(character2),
						"two characters are the same "
								+ character.getLabel()
								+ " at positions "
								+ characters.indexOf(character)
								+ " and "
								+ characters.indexOf(character2));
			}
		}

		final List<VersionInfo> columnVersionInfos = getColumnVersionInfosModifiable();
		final List<VersionInfo> newColumnVersionInfos = determineNewColumnHeaderPPodVersionInfos(characters);
		columnVersionInfos.clear();
		columnVersionInfos.addAll(newColumnVersionInfos);

		final List<StandardCharacter> removedCharacters = newArrayList(getCharacters());

		removedCharacters.removeAll(characters);
		for (final StandardCharacter removedCharacter : removedCharacters) {
			removedCharacter.setParent(null);
		}

		getCharactersModifiable().clear();

		getCharactersModifiable().addAll(characters);

		for (final StandardCharacter character : getCharacters()) {
			character.setParent(this);
		}

		// the matrix has changed
		setInNeedOfNewVersion();
	}

	/**
	 * Set the rows.
	 * <p>
	 * Created for JAXB.
	 * 
	 * @param otusToRows the otusToRows to set
	 */
	protected void setOTUKeyedRows(
			final StandardRows rows) {
		this.rows = rows;
	}

}
