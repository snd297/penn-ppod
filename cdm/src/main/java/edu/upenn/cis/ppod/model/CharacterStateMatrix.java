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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.hibernate.annotations.IndexColumn;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Inject;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A standard matrix - aka a character matrix.
 * 
 * @author Sam Donnelly
 */
@XmlSeeAlso( { DNAStateMatrix.class })
@Entity
@Table(name = CharacterStateMatrix.TABLE)
public class CharacterStateMatrix extends Matrix<CharacterStateRow> {

	public static final String CHARACTER_IDX_COLUMN = Character.TABLE + "_IDX";

	/**
	 * Column that orders the {@link Character}s. Intentionally package-private.
	 */
	static final String CHARACTERS_POSITION_COLUMN = Character.TABLE
														+ "_POSITION";

	/** This entity's table name. */
	public static final String TABLE = "CHARACTER_STATE_MATRIX";

	/**
	 * Name for foreign key columns that point at this table.
	 */
	public static final String FK_ID_COLUMN = TABLE + "_ID";

	static final String OTU_IDX_COLUMN = "OTU_IDX";

	/**
	 * The position of a {@code Character} in <code>characters</code> signifies
	 * its column number in each <code>row</cod>. So <code>characters</code> is
	 * a columnNumber-> <code>Character</code> lookup.
	 * <p>
	 * This relationship is really only many-to-many for {@code
	 * MolecularStateMatrix}s, for character matrices it is one-to-many and a
	 * refactoring should be considered.
	 */
	@ManyToMany
	@JoinTable(joinColumns = { @JoinColumn(name = FK_ID_COLUMN) }, inverseJoinColumns = { @JoinColumn(name = Character.ID_COLUMN) })
	@IndexColumn(name = CHARACTERS_POSITION_COLUMN)
	private final List<Character> characters = newArrayList();

	/**
	 * The inverse of {@link #characters}. So it's a {@code Character}
	 * ->columnNumber lookup.
	 */
	@ElementCollection
	@JoinTable(name = TABLE + "_" + CHARACTER_IDX_COLUMN, joinColumns = @JoinColumn(name = FK_ID_COLUMN))
	@MapKeyJoinColumn(name = Character.ID_COLUMN)
	@Column(name = CHARACTER_IDX_COLUMN)
	private final Map<Character, Integer> charactersToPositions = newHashMap();

	@OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
	private OTUsToCharacterStateRows otusToRows;

	/** No-arg constructor for (at least) Hibernate. */
	protected CharacterStateMatrix() {}

	/**
	 * This constructor is {@code protected} to allow for injected {@code
	 * OTUsToCharacterStateRows} in subclasses to be passed up the inheritance
	 * hierarchy.
	 * 
	 * @param otusToRows the {@code OTUsToCharacterStateRows} for this matrix.
	 */
	@Inject
	protected CharacterStateMatrix(final OTUsToCharacterStateRows otusToRows) {
		this.otusToRows = otusToRows;
		this.otusToRows.setMatrix(this);
	}

	@Override
	public void accept(final IVisitor visitor) {
		for (final Character character : getCharacters()) {
			character.accept(visitor);
		}
		getOTUsToRows().accept(visitor);

		for (final CharacterStateRow row : getOTUsToRows()
				.getOTUsToValues().values()) {
			if (row != null) {
				row.accept(visitor);
			}
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
			if (this instanceof MolecularStateMatrix) {
				// charactersToPositions is meaningless for MolecularMatrix's
				// since
				// all of their Characters are the same
			} else {
				charactersToPositions.put(character, i);
			}
			getColumnPPodVersionInfos().add(null);
			character.addMatrix(this);
		}
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
		setOTUSet((OTUSet) parent);
	}

	@Override
	public boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {
		super.beforeMarshal(marshaller);

		for (final PPodVersionInfo columnVersionInfo : getColumnPPodVersionInfos()) {
			if (columnVersionInfo == null) {
				getColumnPPodVersions().add(null);
			} else {
				getColumnPPodVersions().add(columnVersionInfo.getPPodVersion());
			}
		}
		return true;
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

	private List<PPodVersionInfo> determineNewColumnHeaderPPodVersionInfos(
			final List<? extends Character> newCharacters) {

		final BiMap<Integer, Integer> originalPositionsToNewPositions = HashBiMap
				.create(getColumnsSize());
		for (int originalPosition = 0; originalPosition < getCharacters()
				.size(); originalPosition++) {
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
	public Character getCharacter(@Nonnegative final int characterPosition) {
		return getCharacters().get(characterPosition);
	}

	/**
	 * Created for testing.
	 */
	Map<Character, Integer> getCharactersToPositions() {
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
	public Integer getCharacterPosition(final Character character) {
		return charactersToPositions.get(character);
	}

	/**
	 * Get the characters.
	 * 
	 * @return a modifiable reference to the characters
	 */
	@XmlElement(name = "characterDocId")
	@XmlIDREF
	protected List<Character> getCharacters() {
		return characters;
	}

	public Long getColumnPPodVersion(final int columnPPodVersionPosition) {
		return getColumnPPodVersions().get(columnPPodVersionPosition);
	}

	@CheckForNull
	public PPodVersionInfo getColumnPPodVersionInfo(
			final int columnPPodVersionInfoPosition) {
		return getColumnPPodVersionInfos().get(columnPPodVersionInfoPosition);
	}

	/**
	 * Get the number of characters this matrix has.
	 * 
	 * @return the number of characters this matrix has
	 */
	@Override
	public Integer getColumnsSize() {
		return getCharacters().size();
	}

	/**
	 * Get the otusToRows.
	 * <p>
	 * Not {@code final} for JAXB
	 * 
	 * @return the otusToRows
	 */
	@XmlElement(name = "otusToRows")
	@Override
	protected OTUsToCharacterStateRows getOTUsToRows() {
		return otusToRows;
	}

	/**
	 * Get the number of rows in this matrix.
	 * 
	 * @return the number of rows in this matrix
	 */
	public int getRowsSize() {
		return getOTUsToRows().getOTUsToValues().size();
	}

	/**
	 * Get an iterator over this matrix's rows. The iterator will traverse the
	 * rows in {@code getOTUSet().getOTUs()} order.
	 * 
	 * @return an iterator over this matrix's rows
	 */
	public Iterator<CharacterStateRow> iterator() {
		return getOTUsToRows().getValuesInOTUSetOrder().iterator();
	}

	/**
	 * Set the characters.
	 * <p>
	 * This method is does not reorder the columns of the matrix, unlike
	 * {@link #setOTUSet(OTUSet)} which reorders the rows. Reordering definitely
	 * does not makes sense in a {@link MolecularStateMatrix} since all of the
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
	 *             {@code .equals} to each other. NOTE: this constraint does not
	 *             hold in a {@link MolecularStateMatrix}
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

			// We leave this instanceof here since it is at worst ineffectual w/
			// proxies, but
			// it should still help us on the webapp client side (eg Mesquite)
			// where hibernate is not a factor.
			if (newCharacter instanceof MolecularCharacter) {
				throw new AssertionError(
						"character should not be a MolecularCharacter");
			}
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
			removedCharacter.removeMatrix(this);
		}

		getCharacters().clear();
		charactersToPositions.clear();

		getCharacters().addAll(newCharacters);

		int characterPosition = 0;
		for (final Character character : getCharacters()) {
			charactersToPositions.put(character, characterPosition++);
			character.addMatrix(this);
		}

		// the matrix has changed
		setInNeedOfNewPPodVersionInfo();
		return removedCharacters;
	}

	/**
	 * Set a particular column to a version
	 * 
	 * @param pos position of the column
	 * @param pPodVersionInfo the version
	 * 
	 * @return this
	 * 
	 * @throw IllegalArgumentException if {@code pos >=
	 *        getColumnPPodVersionInfos().size()}
	 */
	public CharacterStateMatrix setColumnPPodVersionInfo(final int pos,
			final PPodVersionInfo pPodVersionInfo) {
		checkNotNull(pPodVersionInfo);
		checkArgument(pos < getColumnPPodVersionInfos().size(),
				"pos is bigger than getColumnPPodVersionInfos().size()");
		getColumnPPodVersionInfos().set(pos, pPodVersionInfo);
		return this;
	}

	/**
	 * Set all of the columns' pPOD version infos.
	 * 
	 * @param pPodVersionInfo version
	 * 
	 * @return this
	 */
	public CharacterStateMatrix setColumnPPodVersionInfos(
			final PPodVersionInfo pPodVersionInfo) {
		for (int pos = 0; pos < getColumnPPodVersionInfos().size(); pos++) {
			setColumnPPodVersionInfo(pos, pPodVersionInfo);
		}
		return this;
	}

	/**
	 * Setter.
	 * <p>
	 * Meant to be called only from objects responsible for managing the {@code
	 * OTUSet<->CharacterStateMatrix} relationship.
	 * <p>
	 * This method will remove rows from this matrix as necessary.
	 * <p>
	 * If there are any new {@code OTU}s in {@code newOTUSet}, then {@code
	 * getRow(theNewOTU) == null}. That is, it adds {@code null} rows for new
	 * {@code OTU}s.
	 * 
	 * @param newOTUSet new {@code OTUSet} for this matrix, or {@code null} if
	 *            we're destroying the association
	 * 
	 * @return this
	 */
	@Override
	public CharacterStateMatrix setOTUSet(
			@CheckForNull final OTUSet otuSet) {
		super.setOTUSet(otuSet);
		getOTUsToRows().setOTUs();
		return this;
	}

	/**
	 * Set the otusToRows.
	 * <p>
	 * Created for JAXB.
	 * 
	 * @param otusToRows the otusToRows to set
	 * 
	 * @return this
	 */
	@edu.umd.cs.findbugs.annotations.SuppressWarnings
	@SuppressWarnings("unused")
	private CharacterStateMatrix setOTUsToRows(
			final OTUsToCharacterStateRows otusToRows) {
		checkNotNull(otusToRows);
		this.otusToRows = otusToRows;
		return this;
	}

}
