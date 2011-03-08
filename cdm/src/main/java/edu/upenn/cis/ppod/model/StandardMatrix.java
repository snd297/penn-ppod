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
import static com.google.common.collect.Maps.newHashMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Version;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.util.UPennCisPPodUtil;

/**
 * A standard matrix - aka a character matrix.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StandardMatrix.TABLE)
public class StandardMatrix extends Matrix<StandardRow> {

	@Access(AccessType.PROPERTY)
	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@CheckForNull
	private Long id;

	@SuppressWarnings("unused")
	@Version
	@Column(name = "OBJ_VERSION")
	@CheckForNull
	private Integer objVersion;

	@Nullable
	public Long getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private void setId(final Long id) {
		this.id = id;
	}

	/** This entity's table name. */
	public static final String TABLE = "STANDARD_MATRIX";

	/**
	 * Name for foreign key columns that point at this table.
	 */
	public static final String ID_COLUMN = TABLE + "_ID";

	@OneToMany(
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = ID_COLUMN, nullable = false)
	private final List<StandardCharacter> characters = newArrayList();

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
			inverseJoinColumns = @JoinColumn(name = StandardRow.ID_COLUMN))
	@MapKeyJoinColumn(name = Otu.ID_COLUMN)
	private final Map<Otu, StandardRow> rows = newHashMap();

	/** No-arg constructor. */
	public StandardMatrix() {}

	/**
	 * Get the characters contained in this matrix.
	 * 
	 * @return the characters contained in this matrix
	 */
	public List<StandardCharacter> getCharacters() {
		return characters;
	}

	@Override
	public Map<Otu, StandardRow> getRows() {
		return rows;
	}

	@Override
	public StandardRow putRow(final Otu otu, final StandardRow row) {
		checkNotNull(otu);
		checkNotNull(row);
		final StandardRow oldRow = rows.put(otu, row);
		row.setParent(this);

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

		final List<StandardCharacter> removedCharacters = newArrayList(getCharacters());

		removedCharacters.removeAll(characters);
		for (final StandardCharacter removedCharacter : removedCharacters) {
			removedCharacter.setParent(null);
		}

		this.characters.clear();
		this.characters.addAll(characters);

		for (final StandardCharacter character : getCharacters()) {
			character.setParent(this);
		}

	}

	/** {@inheritDoc} */
	@Override
	public void updateOtus() {
		UPennCisPPodUtil.updateOtus(getParent(), rows);
	}
}
