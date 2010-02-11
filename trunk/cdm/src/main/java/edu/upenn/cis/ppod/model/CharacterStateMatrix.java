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
import static edu.upenn.cis.ppod.util.CollectionsUtil.nullFill;
import static edu.upenn.cis.ppod.util.UPennCisPPodUtil.nullSafeEquals;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.IndexColumn;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A standard matrix - aka a character matrix.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = CharacterStateMatrix.TABLE)
public class CharacterStateMatrix extends UUPPodEntityWXmlId {

	/**
	 * We use these to figure out what kind of matrix we have after
	 * unmarshalling.
	 */
	@XmlType(name = "CharacterStateMatrixType")
	public static enum Type {
		/** A {@link DNAStateMatrix}. */
		DNA,

		/** An {@link RNAStateMatrix}. */
		RNA,

		/** A standard {@link CharacterStateMatrix}. */
		STANDARD;
	}

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
	@ManyToMany(fetch = FetchType.EAGER)
	// EAGER so we can manipulate it in PPodVersionInfoInterceptor: see the
	// javadoc for Interceptor: "a callback [may not] cause a collection or
	// proxy to be lazily initialized)."
	@JoinTable(inverseJoinColumns = { @JoinColumn(name = PPodVersionInfo.ID_COLUMN) })
	@org.hibernate.annotations.IndexColumn(name = PPodVersionInfo.TABLE
			+ "_POSITION")
	private final List<PPodVersionInfo> columnPPodVersionInfos = newArrayList();

	@XmlElement(name = "columnPPodVersion")
	@Transient
	private final List<Long> columnPPodVersions = newArrayList();

	/** Free-form description. */
	@Column(name = DESCRIPTION_COLUMN)
	private String description;

	/** The label for this {@code CharacterStateMatrix}. */
	@Column(name = LABEL_COLUMN, nullable = false)
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
	@JoinTable(inverseJoinColumns = @JoinColumn(name = Character.ID_COLUMN))
	@IndexColumn(name = CHARACTERS_INDEX_COLUMN)
	private final List<Character> characters = newArrayList();

	/** No delete_orphan... TODO */
	@OneToMany
	@JoinTable(inverseJoinColumns = @JoinColumn(name = CharacterStateRow.TABLE))
	@OrderBy("position")
	private final List<CharacterStateRow> rows = newArrayList();

	@XmlAttribute
	@Transient
	private Type type = Type.STANDARD;

	/** No-arg constructor for (at least) Hibernate. */
	CharacterStateMatrix() {}

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
	 * Add a character to the end of this matrix's characters.
	 * 
	 * @param character the character to be added
	 * @return {@code character}
	 */
	public Character addCharacter(final Character character) {
		checkNotNull(character);
		setCharacter(getCharacters().size(), character);
		return character;
	}

	/**
	 * Take actions after unmarshalling that need to occur after
	 * {@link #afterUnmarshal(Unmarshaller, Object)} is called, specifically
	 * after {@code @XmlIDRef} elements are resolved.
	 */
	@Override
	public void afterUnmarshal() {
		if (otuIdx.size() == 0) {
			int i = 0;
			for (final OTU otu : otus) {
				otuIdx.put(otu, i++);
			}
		}
		if (characterIdx.size() == 0) {
			int i = 0;
			for (final Character character : getCharacters()) {
				characterIdx.put(character, i++);
				columnPPodVersionInfos.add(null);
				character.addMatrix(this);
			}
		}
		if (otuIdx.size() == 0) {
			int i = 0;
			for (final OTU otu : otus) {
				otuIdx.put(otu, i++);
			}
		}
		super.afterUnmarshal();
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@Override
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		this.otuSet = (OTUSet) parent;
		super.afterUnmarshal(u, parent);
	}

	@Override
	public boolean beforeMarshal(final Marshaller marshaller) {
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
	 * Remove this matrix's characters and return them.
	 * 
	 * @return the removed characters
	 */
	public List<Character> clearCharacters() {
		final List<Character> clearedCharacters = newArrayList(characters);
		if (characters.size() == 0) {
			return clearedCharacters;
		}
		characters.clear();
		characterIdx.clear();
		columnPPodVersionInfos.clear();
		resetPPodVersionInfo();
		return clearedCharacters;
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
	protected Map<Character, Integer> getCharacterIdxMutable() {
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
	protected List<Character> getCharactersMutable() {
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
	 * Intentionally package-private and written to ease testing.
	 * 
	 * @return a mutable view of the {@code PPodVersionInfo}s for each for the
	 *         columns of the matrix
	 */
	List<PPodVersionInfo> getColumnPPodVersionInfosMutable() {
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
	protected List<Long> getColumnPPodVersionsMutable() {
		return columnPPodVersions;
	}

	/**
	 * Getter.
	 * 
	 * @return the description
	 */
	@XmlAttribute
	@Nullable
	public String getDescription() {
		return description;
	}

	/**
	 * Getter.
	 * 
	 * @return the label
	 */
	@XmlAttribute
	public String getLabel() {
		return label;
	}

	/**
	 * Get an unmodifiable view of the {@code OTU->row number} map.
	 * 
	 * @return an unmodifiable view of the {@code OTU->row number} map
	 */
	public Map<OTU, Integer> getOTUIdx() {
		// Now we build up {@code this.otuIdx} and {@code this.characterIdx}
		// since they are not sent over the wire.
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
	 * Getter.
	 * 
	 * @return this matrix's {@code OTUSet}
	 */
	public OTUSet getOTUSet() {
		return otuSet;
	}

	@XmlElement(name = "otuDocId")
	@XmlIDREF
	@SuppressWarnings("unused")
	private List<OTU> getOTUsMutable() {
		return otus;
	}

	/**
	 * Get the row indexed by an OTU.
	 * 
	 * @param otu the index
	 * @return the row
	 * @throws IllegalArgumentException if {@code otu} does not belong to this
	 *             matrix
	 */
	public CharacterStateRow getRow(final OTU otu) {
		checkNotNull(otu);
		if (getOTUIdx().get(otu) == null) {
			throw new IllegalArgumentException(
					"otu does not belong to this matrix");
		}
		return getRows().get(getOTUIdx().get(otu));
	}

	/**
	 * Get an unmodifiable view of this matrix's rows.
	 * 
	 * @return an unmodifiable view of this matrix's rows
	 */
	public List<CharacterStateRow> getRows() {
		return Collections.unmodifiableList(rows);
	}

	@XmlElement(name = "row")
	@SuppressWarnings("unused")
	private List<CharacterStateRow> getRowMutable() {
		return rows;
	}

	/**
	 * Get the type of this matrix. Used to determine the type of matrix after
	 * marhsalling->unmarshalling, which loses class info.
	 * 
	 * @return the type of this matrix
	 */
	public Type getType() {
		return type;
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
		nullFill(columnPPodVersionInfos, idx + 1);
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
		if (getAllowResetPPodVersionInfo()) {
			if (getPPodVersionInfo() == null) {
				// nothing to do
			} else {
				if (otuSet != null) {
					otuSet.resetPPodVersionInfo();
				}
				super.resetPPodVersionInfo();
			}
		}
		return this;
	}

	/**
	 * Set the {@code Character} at {@code characterIdx}.
	 * <p>
	 * If {@code getCharacters().size() <= characterIdx}, then this method pads
	 * {@link #getCharacters()} with {@code null}s.
	 * <p>
	 * If {@code character} was already contained in this matrix, then its
	 * former position is filled in with {@code null}.
	 * <p>
	 * This method is does not reorder the columns of the matrix, unlike
	 * {@link #setOTUs(List)} which reorders rows. Reordering definitely does
	 * not makes sense in a {@link MolecularStateMatrix} since all of the
	 * characters will be the same instance.
	 * 
	 * @param characterIdx index
	 * @param character value
	 * 
	 * @return the {@code Character} previously at that position or {@code null}
	 *         if there was no such {@code Character}
	 */
	@Nullable
	public Character setCharacter(final int characterIdx,
			final Character character) {
		checkNotNull(character);

		// We leave this instanceof here since it is at worst ineffectual w/
		// proxies, but
		// it should still help us on the client side where hibernate is not a
		// factor.
		if (character instanceof MolecularCharacter) {
			throw new AssertionError(
					"character should not be a MolecularCharacter");
		}

		if (characters.size() > characterIdx
				&& character.equals(characters.get(characterIdx))) {
			// Nothing to do
			return character;
		}
		nullFill(characters, characterIdx + 1);
		nullFill(columnPPodVersionInfos, characterIdx + 1);

		Character oldCharacter = null;

		final Integer newCharacterOriginalIdx = this.characterIdx
				.get(character);
		if (newCharacterOriginalIdx != null) {
			characters.set(newCharacterOriginalIdx, null);
			resetColumnPPodVersion(newCharacterOriginalIdx);
		}
		oldCharacter = characters.get(characterIdx);
		if (oldCharacter != null) {
			this.characterIdx.remove(oldCharacter);
		}

		this.characters.set(characterIdx, character);
		this.characterIdx.put(character, characterIdx);
		character.addMatrix(this);

		// the matrix has changed
		resetPPodVersionInfo();

		return oldCharacter;
	}

	/**
	 * Setter.
	 * 
	 * @param description the description value
	 * 
	 * @return this matrix
	 */
	public CharacterStateMatrix setDescription(
			@Nullable final String description) {
		if (nullSafeEquals(getDescription(), description)) {
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
	 * @throws IllegalStateException if the OTU set has not been set, ie if
	 *             {@link #getOTUSet() == null}
	 */
	public CharacterStateMatrix setOTUs(final List<OTU> newOTUs) {
		checkNotNull(newOTUs);
		if (newOTUs.equals(getOTUs())) {
			// They're the same, nothing to do
			return this;
		}

		if (otuSet == null) {
			throw new IllegalStateException(
					"otuSet needs to be set before setOTUs(...) is called");
		}

		if (newOTUs.containsAll(otuSet.getOTUs())
				&& otuSet.getOTUs().containsAll(newOTUs)) {
			// They have the same elements, that's good.
		} else {
			throw new IllegalArgumentException(
					"otus (size "
							+ newOTUs.size()
							+ ") does not contain the same OTU's as the matrix's OTUSet (size "
							+ otuSet.getOTUs().size() + ").");
		}

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
	 * @param otuSet new {@code OTUSet} for this matrix
	 * 
	 * @return this
	 */
	CharacterStateMatrix setOTUSet(@Nullable final OTUSet otuSet) {
		if (nullSafeEquals(this.otuSet, otuSet)) {
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
	 */
	@Nullable
	public CharacterStateRow setRow(final OTU otu,
			@Nullable final CharacterStateRow row) {

		checkArgument(getOTUIdx().get(otu) != null,
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
		if (nullSafeEquals(row, oldRow)) {
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

	/**
	 * Set the type of this matrix so that we know what kind it is after
	 * marshalling.
	 * 
	 * @param type the type of this matrix
	 * 
	 * @return this
	 */
	protected CharacterStateMatrix setType(final Type type) {
		this.type = type;
		return this;
	}

	/**
	 * Constructs a <code>String</code> with all attributes in name = value
	 * format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
// public String toString()
// {
// final String TAB = "";
//	
// final StringBuilder retValue = new StringBuilder();
//	    
// retValue.append("CharacterStateMatrix(")
// .append(super.toString()).append(TAB)
// .append("columnPPodVersions=").append(this.columnPPodVersions).append(TAB)
// .append("description=").append(this.description).append(TAB)
// .append("label=").append(this.label).append(TAB)
// .append("otuIdx=").append(this.otuIdx).append(TAB)
// .append("otus=").append(this.otus).append(TAB)
// .append("otuSet=").append(this.otuSet).append(TAB)
// .append("characterIdx=").append(this.characterIdx).append(TAB)
// .append("characters=").append(this.characters).append(TAB)
// .append("rows=").append(this.rows).append(TAB)
// .append("type=").append(this.type).append(TAB)
// .append(")");
//	    
// return retValue.toString();
// }

	/**
	 * Constructs a <code>String</code> with attributes in name=value format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
// @Override
// public String toString() {
// final String TAB = ",";
//
// final StringBuilder retValue = new StringBuilder();
//
// retValue.append(
// "CharacterStateMatrix(" + otuSet.getOTUs().size() + ","
// + otus.size() + "," + otuIdx.size() + ",").append(TAB)
// .append(TAB).append("label=").append(this.label).append(TAB)
// .append("otuSet=").append(this.otuSet).append(TAB).append(
// "otus=").append(this.otus).append(TAB)
// .append("otuIdx=").append(this.otuIdx).append(TAB).append('\n')
// .append("rows=").append(this.rows).append(TAB).append(
// "characters=").append(this.characters).append(TAB)
// .append("characterIdx=").append(this.characterIdx).append(")");
//
// return retValue.toString();
// }
}
