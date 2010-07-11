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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A standard matrix - aka a character matrix.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StandardMatrix.TABLE)
public class StandardMatrix extends Matrix<StandardRow> {

	public static final String CHARACTER_POSITION_COLUMN =
			StandardCharacter.TABLE + "_POSITION";

	/** This entity's table name. */
	public static final String TABLE = "STANDARD_MATRIX";

	/**
	 * Name for foreign key columns that point at this table.
	 */
	public static final String JOIN_COLUMN = TABLE + "_ID";

	/**
	 * The position of a {@code Character} in <code>characters</code> signifies
	 * its column number in each <code>row</cod>. So <code>characters</code> is
	 * a columnNumber-> <code>Character</code> lookup.
	 * <p>
	 * This relationship is really only many-to-many for
	 * {@code MolecularStateMatrix}s, for character matrices it is one-to-many
	 * and a refactoring should be considered.
	 */
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = JOIN_COLUMN, nullable = false)
	private final List<StandardCharacter> characters = newArrayList();

	@Embedded
	private StandardRows rows;

	/** No-arg constructor for (at least) Hibernate. */
	StandardMatrix() {}

	@Inject
	StandardMatrix(final StandardRows rows) {
		this.rows = rows;
		this.rows.setParent(this);
	}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visit(this);
		for (final StandardCharacter character : getCharacters()) {
			character.accept(visitor);
		}
		super.accept(visitor);
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
		setColumnsSize(getCharacters().size());
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
	@XmlElement(name = "character")
	protected List<StandardCharacter> getCharactersModifiable() {
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

	/**
	 * Set the characters.
	 * <p>
	 * This method is does not reorder the columns of the matrix.
	 * <p>
	 * This method does reorder {@link #getColumnVersionInfos()}.
	 * <p>
	 * It is legal for two characters to have the same label, but not to be
	 * {@code .equals} to each other.
	 * 
	 * @param characters the new characters
	 * 
	 * @return the characters removed as a result of this operation
	 * 
	 * @throws IllegalArgumentException if any of {code newCharacters} is
	 *             {@code null}
	 * @throws IllegalArgumentException if any of {@code newCharacters} are
	 *             {@code .equals} to each other. NOTE: this constraint does not
	 *             hold in a {@link MolecularStateMatrix}
	 * @throws IllegalStateExeption if {@code characters.size() !=
	 *             getColumnsSize()}
	 */
	public List<StandardCharacter> setCharacters(
			final List<? extends StandardCharacter> characters) {
		checkNotNull(characters);

		if (characters.equals(getCharacters())) {
			return Collections.emptyList();
		}

		int newCharacterPos = -1;
		for (final StandardCharacter newCharacter : characters) {
			newCharacterPos++;
			checkArgument(newCharacter != null, "newCharacters["
												+ newCharacterPos
												+ "] is null");

			for (final Iterator<? extends StandardCharacter> itr = characters
					.listIterator(newCharacterPos + 1); itr
					.hasNext();) {
				final StandardCharacter character2 = itr.next();
				checkArgument(!newCharacter.equals(character2),
						"two characters are the same "
								+ newCharacter.getLabel()
								+ " at positions "
								+ characters.indexOf(newCharacter)
								+ " and "
								+ characters.indexOf(character2));
			}
		}

		setColumnsSize(characters.size());

		final List<StandardCharacter> removedCharacters = newArrayList(getCharactersModifiable());

		removedCharacters.removeAll(characters);
		for (final StandardCharacter removedCharacter : removedCharacters) {
			removedCharacter.setMatrix(null);
		}

		getCharactersModifiable().clear();

		getCharactersModifiable().addAll(characters);

		for (final StandardCharacter character : getCharactersModifiable()) {
			character.setMatrix(this);
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
	 * 
	 * @return this
	 */
	@edu.umd.cs.findbugs.annotations.SuppressWarnings
	protected StandardMatrix setOTUKeyedRows(
			final StandardRows rows) {
		this.rows = rows;
		return this;
	}
}
