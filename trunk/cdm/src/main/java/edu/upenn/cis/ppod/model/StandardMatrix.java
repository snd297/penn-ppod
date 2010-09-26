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
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Inject;

import edu.upenn.cis.ppod.imodel.IStandardCell;
import edu.upenn.cis.ppod.imodel.IStandardCharacter;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStandardRow;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A standard matrix - aka a character matrix.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StandardMatrix.TABLE)
public class StandardMatrix
		extends Matrix<IStandardRow, IStandardCell>
		implements IStandardMatrix {

	public static class Adapter extends
			XmlAdapter<StandardMatrix, IStandardMatrix> {

		@Override
		public StandardMatrix marshal(final IStandardMatrix matrix) {
			return (StandardMatrix) matrix;
		}

		@Override
		public IStandardMatrix unmarshal(final StandardMatrix matrix) {
			return matrix;
		}
	}

	/** This entity's table name. */
	public static final String TABLE = "STANDARD_MATRIX";

	/**
	 * Name for foreign key columns that point at this table.
	 */
	public static final String JOIN_COLUMN = TABLE + "_ID";

	@OneToMany(
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			targetEntity = StandardCharacter.class)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = JOIN_COLUMN, nullable = false)
	private final List<IStandardCharacter> characters = newArrayList();

	@Embedded
	private StandardRows rows;

	/** No-arg constructor for Hibernate. */
	StandardMatrix() {}

	@Inject
	StandardMatrix(final StandardRows rows) {
		this.rows = rows;
		this.rows.setParent(this);
	}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitStandardMatrix(this);
		for (final IStandardCharacter character : getCharacters()) {
			character.accept(visitor);
		}
		super.accept(visitor);
	}

	/** {@inheritDoc} */
	public void addColumn(
			final int columnNo,
			final IStandardCharacter character,
			final List<? extends IStandardCell> cells) {
		checkArgument(columnNo <= getColumnsSize(),
				"columnNo " + columnNo + " too big for matrix column size "
						+ getColumnsSize());
		checkArgument(columnNo >= 0, "columnNo is negative: " + columnNo);
		final List<IStandardCharacter> thisCharacters = newArrayList(getCharacters());
		thisCharacters.add(columnNo, character);
		setCharacters(thisCharacters);
		addColumn(columnNo, cells);
	}

	@Override
	public void afterUnmarshal() {
		rows.afterUnmarshal();
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@Override
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		super.afterUnmarshal(u, parent);
		resizeColumnVersionInfos(getCharacters().size());
	}

	private List<VersionInfo> determineNewColumnHeaderPPodVersionInfos(
			final List<? extends IStandardCharacter> newCharacters) {

		final BiMap<Integer, Integer> originalPositionsToNewPositions = HashBiMap
				.create(getColumnsSize());
		for (int originalPosition = 0; originalPosition < getCharacters()
				.size(); originalPosition++) {
			final IStandardCharacter originalCharacter =
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

	/** {@inheritDoc} */
	public List<IStandardCharacter> getCharacters() {
		return Collections.unmodifiableList(characters);
	}

	/**
	 * Get a modifiable reference to this matrix's characters.
	 * 
	 * @return a modifiable reference to this matrix's characters
	 */
	@XmlElement(name = "character")
	protected List<IStandardCharacter> getCharactersModifiable() {
		return characters;
	}

	/**
	 * Get the otusToRows.
	 * 
	 * @return the otusToRows
	 */
	@XmlElement(name = "rows")
	@Override
	protected StandardRows getOTUKeyedRows() {
		return rows;
	}

	/** {@inheritDoc} */
	public List<IStandardCell> removeColumn(final int columnNo) {
		final List<IStandardCharacter> characters = newArrayList(getCharacters());
		characters.remove(columnNo);
		setCharacters(characters);
		return super.removeColumnHelper(columnNo);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalArgumentException if any of {code newCharacters} is
	 *             {@code null}
	 * @throws IllegalArgumentException if any of {@code newCharacters} are
	 *             {@code .equals} to each other
	 * @throws IllegalStateExeption if {@code characters.size() !=
	 *             getColumnsSize()}
	 */
	public List<IStandardCharacter> setCharacters(
			final List<? extends IStandardCharacter> characters) {
		checkNotNull(characters);

		if (characters.equals(getCharacters())) {
			return Collections.emptyList();
		}

		int newCharacterPos = -1;
		for (final IStandardCharacter character : characters) {
			newCharacterPos++;
			checkArgument(character != null, "newCharacters["
												+ newCharacterPos
												+ "] is null");

			for (final Iterator<? extends IStandardCharacter> itr = characters
					.listIterator(newCharacterPos + 1); itr
					.hasNext();) {
				final IStandardCharacter character2 = itr.next();
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

		final List<IStandardCharacter> removedCharacters = newArrayList(getCharacters());

		removedCharacters.removeAll(characters);
		for (final IStandardCharacter removedCharacter : removedCharacters) {
			removedCharacter.setParent(null);
		}

		getCharactersModifiable().clear();

		getCharactersModifiable().addAll(characters);

		for (final IStandardCharacter character : getCharacters()) {
			character.setParent(this);
		}

		// the matrix has changed
		setInNeedOfNewVersion();
		return removedCharacters;
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
