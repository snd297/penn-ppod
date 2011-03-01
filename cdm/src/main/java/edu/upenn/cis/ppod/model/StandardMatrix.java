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
import static com.google.common.collect.Maps.newHashMap;
import static edu.upenn.cis.ppod.util.CollectionsUtil.nullFillAndSet;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import edu.upenn.cis.ppod.imodel.IHasColumnVersionInfos;
import edu.upenn.cis.ppod.imodel.IOtuKeyedMap;
import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.UPennCisPPodUtil;

/**
 * A standard matrix - aka a character matrix.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StandardMatrix.TABLE)
public class StandardMatrix extends Matrix<StandardRow> implements
		IHasColumnVersionInfos {

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

	/** The pPod versions of the columns. */
	@ManyToMany
	@JoinTable(inverseJoinColumns =
		{ @JoinColumn(name = VersionInfo.JOIN_COLUMN) })
	@OrderColumn(name = VersionInfo.TABLE + "_POSITION")
	private final List<VersionInfo> columnVersionInfos = newArrayList();

	/**
	 * We want everything but SAVE_UPDATE (which ALL will give us) - once it's
	 * evicted out of the persistence context, we don't want it back in via
	 * cascading UPDATE. So that we can run leaner for large matrices.
	 */
	@OneToMany(cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE,
			CascadeType.REMOVE,
			CascadeType.DETACH,
			CascadeType.REFRESH },
			orphanRemoval = true)
	@JoinTable(name = TABLE + "_" + StandardRow.TABLE,
			inverseJoinColumns = @JoinColumn(name = StandardRow.JOIN_COLUMN))
	@MapKeyJoinColumn(name = Otu.JOIN_COLUMN)
	private final Map<Otu, StandardRow> rows = newHashMap();

	/** No-arg constructor. */
	public StandardMatrix() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitStandardMatrix(this);
		for (final StandardCharacter character : getCharacters()) {
			character.accept(visitor);
		}
		for (final StandardRow row : rows.values()) {
			if (row != null) {
				row.accept(visitor);
			}
		}
		super.accept(visitor);
	}

	/** {@inheritDoc} */
	public void addColumn(
			final int columnNo,
			final StandardCharacter character,
			final List<? extends StandardCell> cells) {
		throw new UnsupportedOperationException();
		// checkNotNull(character);
		// checkNotNull(cells);
		// checkArgument(columnNo <= getColumnsSize(),
		// "columnNo " + columnNo + " too big for matrix column size "
		// + getColumnsSize());
		// checkArgument(columnNo >= 0, "columnNo is negative: " + columnNo);
		// checkArgument(cells.size() == rows.getValues().size());
		//
		// final List<StandardCharacter> thisCharacters =
		// newArrayList(getCharacters());
		// thisCharacters.add(columnNo, character);
		// setCharacters(thisCharacters);
		// addColumn(columnNo, cells);
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
	 * The number of columns which any newly introduced rows must have.
	 * <p>
	 * Will return {@code 0} for newly constructed matrices.
	 * 
	 * @param columnsSize the number of columns in this matrix
	 */
	public Integer getColumnsSize() {
		return Integer.valueOf(getColumnVersionInfos().size());
	}

	/**
	 * Get the column pPOD version infos. These are equal to the largest pPOD
	 * version in the columns, where largest list determined determined by
	 * {@link VersionInfo#getVersion()} .
	 * <p>
	 * The behavior of this method is undefined for unmarshalled matrices.
	 * 
	 * @return get the column pPOD version infos
	 */
	public List<VersionInfo> getColumnVersionInfos() {
		return Collections.unmodifiableList(columnVersionInfos);
	}

	/**
	 * A modifiable reference to the column pPOD version infos.
	 * 
	 * @return a modifiable reference to the column pPOD version infos
	 */
	List<VersionInfo> getColumnVersionInfosModifiable() {
		return columnVersionInfos;
	}

	@Override
	IOtuKeyedMap<StandardRow> getOtuKeyedRows() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<Otu, StandardRow> getRows() {
		return Collections.unmodifiableMap(rows);
	}

	@Override
	public StandardRow putRow(final Otu otu, final StandardRow row) {
		checkNotNull(otu);
		checkNotNull(row);
		final StandardRow oldRow = rows.put(otu, row);
		row.setParent(this);
		if (row != oldRow || oldRow == null) {
			setInNeedOfNewVersion();
		}
		if (row != oldRow && oldRow != null) {
			oldRow.setParent(null);
		}
		return oldRow;
	}

	/**
	 * Set the characters.
	 * <p>
	 * This method does not reorder the columns of the matrix because that is a
	 * potentially expensive operation - it could load the entire matrix into
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
	 * Set a particular column to a version.
	 * 
	 * @param pos position of the column
	 * @param versionInfo the version
	 * 
	 * @throw IllegalArgumentException if {@code pos >=
	 *        getColumnVersionInfos().size()}
	 */
	public void setColumnVersionInfo(
			final int pos,
			final VersionInfo versionInfo) {
		checkNotNull(versionInfo);
		checkArgument(pos < getColumnVersionInfos().size(),
				"pos is bigger than getColumnVersionInfos().size()");
		getColumnVersionInfosModifiable().set(pos, versionInfo);
	}

	/**
	 * Set all of the columns' pPOD version infos.
	 * 
	 * @param versionInfo the pPOD version info
	 * 
	 * @return this
	 */
	public void setColumnVersionInfos(
			final VersionInfo versionInfo) {
		for (int pos = 0; pos < getColumnVersionInfos().size(); pos++) {
			setColumnVersionInfo(pos, versionInfo);
		}
	}

	/**
	 * Set the column at {@code position} as in need of a new
	 * {@link VersionInfo}. Which means to set {@link #getColumnVersionInfos()}
	 * {@code .get(position)} to {@code null}.
	 * 
	 * @param position the column that needs the new {@code VersionInfo}
	 */
	public void setInNeedOfNewColumnVersion(final int position) {
		checkArgument(position >= 0, "position is negative");
		checkArgument(position < getColumnsSize(),
				"position " + position
						+ " is too large for the number of columns "
						+ getColumnVersionInfos().size());
		columnVersionInfos.set(position, null);
	}

	/** {@inheritDoc} */
	@Override
	public void updateOtus() {
		if (UPennCisPPodUtil.updateOtus(getParent(), rows)) {
			setInNeedOfNewVersion();
		}
	}
}
