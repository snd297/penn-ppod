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
import static com.google.common.base.Preconditions.checkState;
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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.IndexColumn;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

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
	 * Column that orders the rows. Intentionally package-private.
	 */
	static final String ROWS_INDEX_COLUMN = CharacterStateRow.TABLE
			+ "_POSITION";

	/** The pPod versions of the columns. */
	@ManyToMany
	@JoinTable(inverseJoinColumns = { @JoinColumn(name = PPodVersionInfo.ID_COLUMN) })
	@org.hibernate.annotations.IndexColumn(name = PPodVersionInfo.TABLE
			+ "_POSITION")
	final List<PPodVersionInfo> columnPPodVersionInfos = newArrayList();

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

	static final String OTU_IDX_COLUMN = "OTU_IDX";

	/**
	 * The inverse of {@code otus}: an {@code OTU}->rowNumber lookup.
	 */
	@org.hibernate.annotations.CollectionOfElements
	@JoinTable(name = TABLE + "_" + OTU_IDX_COLUMN, joinColumns = @JoinColumn(name = ID_COLUMN))
	@org.hibernate.annotations.MapKeyManyToMany(joinColumns = @JoinColumn(name = OTU.ID_COLUMN))
	@Column(name = OTU_IDX_COLUMN)
	private final Map<OTU, Integer> otuIdx = newHashMap();

	/**
	 * The position of an {@code OTU} in {@code otus} signifies its row number
	 * in <code>row</code>. So <code>otus</code> is a rowNumber-> {@code OTU}
	 * lookup.
	 */
	@ManyToMany
	@JoinTable(inverseJoinColumns = @JoinColumn(name = OTU.ID_COLUMN))
	@org.hibernate.annotations.IndexColumn(name = OTU.TABLE + "_POSITION")
	private final List<OTU> otus = newArrayList();

	/**
	 * These are the <code>OTU</code>s whose data comprises this {@code
	 * CharacterStateMatrix}.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = OTUSet.ID_COLUMN, nullable = false)
	@CheckForNull
	private OTUSet otuSet;

	/**
	 * The inverse of {@link #characters}. So it's a {@code Character}
	 * ->columnNumber lookup.
	 */
	@org.hibernate.annotations.CollectionOfElements
	@JoinTable(name = TABLE + "_" + CHARACTER_IDX_COLUMN, joinColumns = @JoinColumn(name = ID_COLUMN))
	@org.hibernate.annotations.MapKeyManyToMany(joinColumns = @JoinColumn(name = Character.ID_COLUMN))
	@Column(name = CHARACTER_IDX_COLUMN)
	private final Map<Character, Integer> characterIdx = newHashMap();

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
	 * The rows of the matrix. We don't do save_update cascades since we want to
	 * control when rows are added to the persistence context. We sometimes
	 * don't want the rows saved or reattached when the the matrix is.
	 */
	@OneToMany(mappedBy = "matrix")
	@OrderBy("position")
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private final List<CharacterStateRow> rows = newArrayList();

	/** No-arg constructor for (at least) Hibernate. */
	CharacterStateMatrix() {

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
		if (getOTUIdx().size() == 0) {
			int i = 0;
			for (final OTU otu : otus) {
				otuIdx.put(otu, i++);
			}
		}
		int i = 0;
		for (final Character character : getCharacters()) {
			if (this instanceof DNAStateMatrix
					|| this instanceof RNAStateMatrix) {
				// characterIdx is meaningless for MolecularMatrix's since
				// all of their Characters are the same
			} else {
				characterIdx.put(character, i++);
			}
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
		setOTUSet((OTUSet) parent);
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
	 * Get an unmodifiable view of characterIdx.
	 * 
	 * @return the characterIdx
	 */
	public Map<Character, Integer> getCharacterIdx() {
		return Collections.unmodifiableMap(characterIdx);
	}

	/**
	 * Get a modifiable view of characterIdx
	 * 
	 * @return a modifiable view of characterIdx
	 */
	protected Map<Character, Integer> getCharacterIdxModifiable() {
		return characterIdx;
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

	/**
	 * Get an unmodifiable view of the {@code OTU->row number} map.
	 * 
	 * @return an unmodifiable view of the {@code OTU->row number} map
	 */
	public Map<OTU, Integer> getOTUIdx() {
		return Collections.unmodifiableMap(otuIdx);
	}

	/**
	 * Return an unmodifiable view of this matrix's <code>OTUSet</code>
	 * ordering.
	 * 
	 * @return see description
	 */
	public List<OTU> getOTUs() {
		return Collections.unmodifiableList(otus);
	}

	/**
	 * Getter. Will be {@code null} when object is first created.
	 * 
	 * @return this matrix's {@code OTUSet}
	 */
	@Nullable
	public OTUSet getOTUSet() {
		return otuSet;
	}

	@XmlElement(name = "otuDocId")
	@XmlIDREF
	@SuppressWarnings("unused")
	private List<OTU> getOTUsModifiable() {
		return otus;
	}

	/**
	 * Get the row indexed by an OTU.
	 * <p>
	 * Will return {@code null} for rows that have had an OTU assigned - through
	 * {@link #setOTUs(List)} - but have yet to have a row set.
	 * 
	 * @param otu the index
	 * @return the row
	 * 
	 * @throws IllegalArgumentException if {@code otu} does not belong to this
	 *             matrix
	 */
	@Nullable
	public CharacterStateRow getRow(final OTU otu) {
		checkNotNull(otu);
		checkArgument(getOTUIdx().get(otu) != null,
				"otu does not belong to this matrix");
		return getRows().get(getOTUIdx().get(otu));
	}

	@XmlElement(name = "row")
	@SuppressWarnings("unused")
	private List<CharacterStateRow> getRowMutable() {
		return rows;
	}

	/**
	 * Get an unmodifiable view of this matrix's rows.
	 * 
	 * @return an unmodifiable view of this matrix's rows
	 */
	public List<CharacterStateRow> getRows() {
		return Collections.unmodifiableList(rows);
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
		if (otuSet != null) {
			otuSet.resetPPodVersionInfo();
		}
		super.resetPPodVersionInfo();
		return this;
	}

	/**
	 * Set the characters.
	 * <p>
	 * This method is does not reorder the columns of the matrix, unlike
	 * {@link #setOTUs(List)} which reorders rows. Reordering definitely does
	 * not makes sense in a {@link MolecularStateMatrix} since all of the
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
		getCharacterIdxModifiable().clear();

		getCharactersModifiable().addAll(newCharacters);

		int characterPosition = 0;
		for (final Character character : getCharacters()) {
			getCharacterIdxModifiable().put(character, characterPosition++);
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

	/**
	 * Order the {@code OTU}s of {@code this.getOTUSet()}. In other words, set
	 * the order of the rows in this {@code CharacterStateMatrix}.
	 * <p>
	 * This method operates on the rows and reorders them appropriately, filling
	 * in {@code null} values for newly introduced rows. These {@code null}'d
	 * rows must be filled in by the client.
	 * <p>
	 * A copy of {@code newOtus} is made so subsequent modifications of {@code
	 * newOTUs} will have no affect on this matrix.
	 * <p>
	 * Assumes all members of {@code newOtus} are not Hibernate-detached.
	 * 
	 * @param newOTUs order of the {@link OTUSet} associated with this {@code
	 *            CharacterStateMatrix}
	 * 
	 * @return this {@code CharacterStateMatrix}
	 * 
	 * @throws IllegalArgumentException if the members of {@code otus} are not
	 *             the same as those of this {@code CharacterStateMatrix}'s
	 *             associated {@code OTUSet}
	 * @throws IllegalStateException if the OTU set has not been set, i.e. if
	 *             {@link #getOTUSet() == null}
	 */
	public CharacterStateMatrix setOTUs(final List<OTU> newOTUs) {
		checkNotNull(newOTUs);
		if (newOTUs.equals(getOTUs())) {
			// They're the same, nothing to do
			return this;
		}

		checkState(getOTUSet() != null,
				"otuSet needs to be set before setOTUs(...) is called");

		// We want both newOTUs and this.otuSet to have the same elements
		checkArgument(
				newOTUs.containsAll(getOTUSet().getOTUs())
						&& getOTUSet().getOTUs().containsAll(newOTUs),
				"otus (size "
						+ newOTUs.size()
						+ ") does not contain the same OTU's as the matrix's OTUSet (size "
						+ getOTUSet().getOTUs().size() + ").");

		// We're now going to move around the rows to match the new ordering
		final List<CharacterStateRow> newRows = newArrayListWithCapacity(newOTUs
				.size());
		for (int newOtuIdx = 0; newOtuIdx < newOTUs.size(); newOtuIdx++) {
			final OTU newOtu = newOTUs.get(newOtuIdx);

			// oldIdx is where newOtu used to be
			final Integer oldIdx = getOTUIdx().get(newOtu);
			CharacterStateRow originalRow = null;
			if (oldIdx == null) {
				// It's a new row - it will be filled in later.
			} else {
				originalRow = rows.get(oldIdx);
			}
			newRows.add(originalRow); // could be adding null
		}

		otus.clear();
		otuIdx.clear();
		rows.clear();
		for (int i = 0; i < newRows.size(); i++) {
			final OTU newOtu = newOTUs.get(i);
			otus.add(newOtu);
			otuIdx.put(newOtu, i);
			setRow(newOtu, newRows.get(i)); // could be setting
			// it to null
			if (newRows.get(i) != null) {
				newRows.get(i).setPosition(i);
			}
		}

		resetPPodVersionInfo();
		return this;
	}

	/**
	 * Setter.
	 * <p>
	 * Intentionally package-private and meant to be called from {@code
	 * CharacterStateMatrix}.
	 * 
	 * @param otuSet new {@code OTUSet} for this matrix, or {@code null} if
	 *            we're destroying the association
	 * 
	 * @return this
	 */
	CharacterStateMatrix setOTUSet(@Nullable final OTUSet otuSet) {
		if (equal(this.otuSet, otuSet)) {
			// still the same
		} else {
			this.otuSet = otuSet;
			resetPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Set row at <code>index</code> to <code>row</code>. Fills with
	 * <code>null</code>s if <code>index</code> is too big.
	 * <p>
	 * If {@code row} is already in this matrix, this method sets that position
	 * to {@code null}.
	 * <p>
	 * Assumes row does not belong to another matrix.
	 * <p>
	 * Assumes {@code row} is not detached.
	 * 
	 * @param otu index of the row we are adding
	 * @param row see description
	 * 
	 * @return the row that was previously there, or {@code null} if there was
	 *         no row previously there
	 * 
	 * @throw IllegalArgumentException if {@code otu} does not belong to this
	 *        matrix's {@code OTUSet}
	 */
	@Nullable
	public CharacterStateRow setRow(final OTU otu,
			@Nullable final CharacterStateRow row) {

		checkArgument(getOTUSet().getOTUs().contains(otu),
				"otu does not belong to this matrix");

		final Integer otuIdx = getOTUIdx().get(otu);
		while (rows.size() <= otuIdx) {
			rows.add(null);
			resetPPodVersionInfo();
		}
		final CharacterStateRow oldRow = rows.get(otuIdx);
		if (row != null) {
			row.setMatrix(this);
		}
		if (equal(row, oldRow)) {
			// same, nothing to do
		} else {

			if (oldRow != null) {
				oldRow.setMatrix(null);
			}

			// Remove the row from its old position, if it's already in this
			// matrix
			final int rowOldIdx = rows.indexOf(row);
			if (rowOldIdx != -1) {
				rows.set(rowOldIdx, null);
			}

			// Now set it to it's new position.
			rows.set(otuIdx, row);
			if (row != null) {
				row.setPosition(otuIdx);
			}
			resetPPodVersionInfo();
		}
		return oldRow;
	}

}
