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

import static com.google.common.base.Objects.equal;
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
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
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
@XmlSeeAlso( { DNAStateMatrix.class, RNAStateMatrix.class })
@Entity
@Table(name = CharacterStateMatrix.TABLE)
public class CharacterStateMatrix extends UUPPodEntityWXmlId {

	/** This entity's table name. Intentionally package-private. */
	static final String TABLE = "CHARACTER_STATE_MATRIX";

	/** Description column. Intentionally package-private. */
	static final String DESCRIPTION_COLUMN = "DESCRIPTION";

	/**
	 * Name for foreign key columns. Intentionally package-private.
	 */
	static final String ID_COLUMN = TABLE + "_ID";

	static final String LABEL_COLUMN = "LABEL";

	static final String CHARACTER_IDX_COLUMN = Character.TABLE + "_IDX";

	/**
	 * Column that orders the {@link Character}s. Intentionally package-private.
	 */
	static final String CHARACTERS_INDEX_COLUMN = Character.TABLE + "_POSITION";

	/**
	 * Column that orders the otusToRows. Intentionally package-private.
	 */
	static final String ROWS_INDEX_COLUMN = CharacterStateRow.TABLE
			+ "_POSITION";

	/** The pPod versions of the columns. */
	@ManyToMany
	@JoinTable(inverseJoinColumns = { @JoinColumn(name = PPodVersionInfo.ID_COLUMN) })
	@org.hibernate.annotations.IndexColumn(name = PPodVersionInfo.TABLE
			+ "_POSITION")
	final List<PPodVersionInfo> columnPPodVersionInfos = newArrayList();

	/**
	 * So we can just send the version numbers over the wire, and not all of the
	 * {@code PPodVersionInfo}s.
	 */
	@XmlElement(name = "columnPPodVersion")
	@Transient
	private final List<Long> columnPPodVersions = newArrayList();

	/** Free-form description. */
	@Column(name = DESCRIPTION_COLUMN)
	@CheckForNull
	private String description;

	/** The label for this {@code CharacterStateMatrix}. */
	@Column(name = LABEL_COLUMN, nullable = false)
	@CheckForNull
	private String label;

	/**
	 * The position of a {@code Character} in <code>characters</code> signifies
	 * its column number in each <code>row</cod>. So <code>characters</code> is
	 * a columnNumber-> <code>Character</code> lookup.
	 */
	@ManyToMany
	@JoinTable(joinColumns = { @JoinColumn(name = ID_COLUMN) }, inverseJoinColumns = { @JoinColumn(name = Character.ID_COLUMN) })
	@IndexColumn(name = CHARACTERS_INDEX_COLUMN)
	private final List<Character> characters = newArrayList();

	/**
	 * Should only be {@code null} during unmarshalling.
	 */
	@Embedded
	@Nullable
	private OTUsToCharacterStateRows otusToRows;

	/**
	 * Get the otusToRows.
	 * 
	 * @return the otusToRows
	 */
	@XmlElement(name = "otusToRows")
	private OTUsToCharacterStateRows getOTUsToRows() {
		return otusToRows;
	}

	/**
	 * Set the otusToRows.
	 * 
	 * @param otusToRows the otusToRows to set
	 * 
	 * @return this
	 */
	@SuppressWarnings("unused")
	private CharacterStateMatrix setOTUsToRows(
			OTUsToCharacterStateRows otusToRows) {
		this.otusToRows = otusToRows;
		return this;
	}

	CharacterStateMatrix() {}

	@Inject
	CharacterStateMatrix(final OTUsToCharacterStateRows otusToRows) {
		this.otusToRows = otusToRows;
		otusToRows.setParent(this);
	}

	@Override
	public CharacterStateMatrix accept(final IVisitor visitor) {
		visitor.visit(this);
		for (final Character character : getCharacters()) {
			character.accept(visitor);
		}
		for (final CharacterStateRow row : getRows()) {
			row.accept(visitor);
		}
		super.accept(visitor);
		return this;
	}

	/**
	 * Take actions after unmarshalling that need to occur after
	 * {@link #afterUnmarshal(Unmarshaller, Object)} is called, specifically
	 * after {@code @XmlIDRef} elements are resolved.
	 */
	@Override
	public void afterUnmarshal() {
		int i = 0;
		for (final Character character : getCharacters()) {
			columnPPodVersionInfos.add(null);
			character.addMatrix(this);
		}
		super.afterUnmarshal();
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		otusToRows.setOTUSet((OTUSet) parent);
	}

	@Override
	public boolean beforeMarshal(@Nullable final Marshaller marshaller) {
		super.beforeMarshal(marshaller);
		for (final PPodVersionInfo columnVersionInfo : getColumnPPodVersionInfos()) {
			if (columnVersionInfo == null) {
				columnPPodVersions.add(null);
			} else {
				columnPPodVersions.add(columnVersionInfo.getPPodVersion());
			}
		}
		return true;
	}

	/**
	 * Made package-private (instead of private) for unit testing.
	 * 
	 * @param originalCharacters
	 * @param newCharacters
	 * @return
	 */
	private List<PPodVersionInfo> determineNewColumnHeaderPPodVersionInfos(
			final List<? extends Character> newCharacters) {

		final BiMap<Integer, Integer> originalPositionsToNewPositions = HashBiMap
				.create(getCharacters().size());
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
	 * Get an unmodifiable view of the <code>Character</code> ordering.
	 * 
	 * @return an unmodifiable view of the <code>Character</code> ordering.
	 */
	public List<Character> getCharacters() {
		return Collections.unmodifiableList(characters);
	}

	/**
	 * Get a modifiable reference to the characters.
	 * 
	 * @return a modifiable reference to the characters
	 */
	@XmlElement(name = "characterDocId")
	@XmlIDREF
	protected List<Character> getCharactersModifiable() {
		return characters;
	}

	/**
	 * Get an unmodifiable view of the {@code PPodVersionInfo}s for each for the
	 * columns of the matrix.
	 * <p>
	 * This value is {@code equals()} to the max pPOD version info in a column.
	 * 
	 * @return an unmodifiable view of the columns' {@code PPodVersionInfo}s
	 */
	public List<PPodVersionInfo> getColumnPPodVersionInfos() {
		return Collections.unmodifiableList(columnPPodVersionInfos);
	}

	/**
	 * Get a mutable view of the {@code PPodVersionInfo}s for each for the
	 * columns of the matrix.
	 * <p>
	 * Intentionally package-private.
	 * 
	 * @return a mutable view of the {@code PPodVersionInfo}s for each for the
	 *         columns of the matrix
	 */
	List<PPodVersionInfo> getColumnPPodVersionInfosModifiable() {
		return columnPPodVersionInfos;
	}

	/**
	 * Get an unmodifiable view of the column pPOD versions.
	 * <p>
	 * A column version is the max pPOD version of the cells in a column.
	 * 
	 * @return the column pPOD versions
	 */
	public List<Long> getColumnPPodVersions() {
		return Collections.unmodifiableList(columnPPodVersions);
	}

	/**
	 * Get the column pPOD versions. This is a modifiable list.
	 * 
	 * @see #getColumnPPodVersions()
	 * 
	 * @return the column pPOD versions
	 */
	@XmlElement(name = "columnPPodVersion")
	protected List<Long> getColumnPPodVersionsModifiable() {
		return columnPPodVersions;
	}

	/**
	 * Getter.
	 * <p>
	 * {@code null} is a legal value.
	 * 
	 * @return the description
	 */
	@XmlAttribute
	@CheckForNull
	public String getDescription() {
		return description;
	}

	/**
	 * Getter. {@code null} when the object is constructed.
	 * 
	 * @return the label
	 */
	@XmlAttribute
	@Nullable
	public String getLabel() {
		return label;
	}

	public List<OTU> getOTUOrdering() {
		return getRowsByOTU().getOTUOrdering();
	}

	public OTUSet getOTUSet() {
		return getRowsByOTU().getOTUSet();
	}

	/**
	 * Get the row indexed by an OTU, or {@code null} if there is no such
	 * element.
	 * 
	 * @param otu the index
	 * @return the row
	 * 
	 * @throws IllegalArgumentException if {@code otu} does not belong to this
	 *             matrix
	 */
	@CheckForNull
	public CharacterStateRow getRow(final OTU otu) {
		checkNotNull(otu);
		return getRowsByOTU().getItems().get(otu);
	}

	/**
	 * Get a possibly unmodifiable view of this matrix's otusToRows.
	 * 
	 * @return a possible unmodifiable view of this matrix's otusToRows
	 */
	public List<CharacterStateRow> getRows() {
		return otusToRows.getItemsInOTUOrder();
	}

	/**
	 * Get the otusToRows.
	 * <p>
	 * Should only be {@code null} during unmarshalling.
	 * 
	 * @return the otusToRows
	 */
	@Nullable
	protected OTUsToCharacterStateRows getRowsByOTU() {
		return otusToRows;
	}

	public CharacterStateRow putRow(final OTU otu,
			final CharacterStateRow newRow) {
		final CharacterStateRow originalRow = getRowsByOTU().put(otu, newRow);
		newRow.setMatrix(this);
		if (originalRow != null) {
			originalRow.setMatrix(null);
		}
		return originalRow;
	}

	/**
	 * Set the {@link PPodVersionInfo} at {@code idx} to {@code null}. Fills
	 * with <code>null</code>s if necessary.
	 * <p>
	 * Intentionally package-private.
	 * 
	 * @param idx see description
	 * 
	 * @return this {@code CharacterStateMatrix}
	 */
	CharacterStateMatrix resetColumnPPodVersion(final int idx) {
		if (getAllowResetPPodVersionInfo()) {
			nullFillAndSet(getColumnPPodVersionInfosModifiable(), idx, null);
		}
		return this;
	}

	/**
	 * {@code null} out {@code pPodVersionInfo} and the {@link PPodVersionInfo}
	 * of the owning study.
	 * 
	 * @return this {@code CharacterStateMatrix}
	 */
	@Override
	public CharacterStateMatrix resetPPodVersionInfo() {
		if (getRowsByOTU() != null) {
			if (getRowsByOTU().getOTUSet() != null) {
				getRowsByOTU().getOTUSet().resetPPodVersionInfo();
			}
		}
		super.resetPPodVersionInfo();
		return this;
	}

	/**
	 * Set the characters.
	 * <p>
	 * This method is does not reorder the columns of the matrix, unlike
	 * {@link #setOTUs(List)} which reorders otusToRows. Reordering definitely
	 * does not makes sense in a {@link MolecularStateMatrix} since all of the
	 * characters will be the same instance.
	 * <p>
	 * This method does reorder {@link #getColumnPPodVersionInfos()}.
	 * <p>
	 * It is legal for two characters to have the same label, but to to be
	 * {@code .equals} to each other.
	 * 
	 * @param newCharacters the new characters
	 * 
	 * @return the characters removed as a result of this operation
	 * 
	 * @throws IllegalArgumentException if any of {@code characters} are {@code
	 *             .equals} to each other. NOTE: this constraint does not hold
	 *             in a {@code MolecularStateMatrix}
	 */
	public List<Character> setCharacters(
			final List<? extends Character> newCharacters) {
		checkNotNull(newCharacters);

		// We leave this instanceof here since it is at worst ineffectual w/
		// proxies, but
		// it should still help us on the client side where hibernate is not a
		// factor.
		for (final Character character : newCharacters) {
			if (character instanceof MolecularCharacter) {
				throw new AssertionError(
						"character should not be a MolecularCharacter");
			}
			for (final Iterator<? extends Character> itr = newCharacters
					.listIterator(newCharacters.indexOf(character) + 1); itr
					.hasNext();) {
				final Character character2 = itr.next();
				checkArgument(!character.equals(character2),
						"two characters are the same " + character.getLabel()
								+ " at positions "
								+ newCharacters.indexOf(character) + " and "
								+ newCharacters.indexOf(character2));
			}
		}

		if (newCharacters.equals(getCharacters())) {
			return Collections.emptyList();
		}

		final List<PPodVersionInfo> newColumnPPodVersionInfos = determineNewColumnHeaderPPodVersionInfos(newCharacters);
		getColumnPPodVersionInfosModifiable().clear();
		getColumnPPodVersionInfosModifiable().addAll(newColumnPPodVersionInfos);

		final List<Character> removedCharacters = newArrayList(getCharacters());

		removedCharacters.removeAll(newCharacters);
		for (final Character removedCharacter : removedCharacters) {
			removedCharacter.removeMatrix(this);
		}

		getCharactersModifiable().clear();

		getCharactersModifiable().addAll(newCharacters);

		for (final Character character : getCharacters()) {
			character.addMatrix(this);
		}

		// the matrix has changed
		resetPPodVersionInfo();
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
		checkArgument(pos < getColumnPPodVersionInfos().size(),
				"pos is bigger than getColumnPPodVersionInfos().size()");
		getColumnPPodVersionInfosModifiable().set(pos, pPodVersionInfo);
		return this;
	}

	/**
	 * Set all of the columns' {@code PPodVersionInfo}s to a given value
	 * 
	 * @param pPodVersionInfo the given value
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
	 * 
	 * @param description the description value, {@code null} is allowed
	 * 
	 * @return this matrix
	 */
	public CharacterStateMatrix setDescription(
			@Nullable final String description) {
		if (equal(getDescription(), description)) {
			// nothing to do
		} else {
			this.description = description;
			resetPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Set the label of this matrix.
	 * 
	 * @param label the value for the label
	 * 
	 * @return this matrix
	 */
	public CharacterStateMatrix setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(getLabel())) {
			// they're the same, nothing to do
		} else {
			this.label = label;
			resetPPodVersionInfo();
		}
		return this;
	}

	public CharacterStateMatrix setOTUOrdering(final List<OTU> newOTUOrdering) {
		getRowsByOTU().setOTUOrdering(newOTUOrdering);
		return this;
	}

	protected CharacterStateMatrix setOTUSet(@Nullable final OTUSet newOTUSet) {
		getRowsByOTU().setOTUSet(newOTUSet);
		return this;
	}

}
