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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Maps.newHashMap;
import static edu.upenn.cis.ppod.util.UPennCisPPodUtil.nullSafeEquals;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import org.hibernate.annotations.Cascade;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A standard matrix - aka a character matrix.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = CharacterStateMatrix.TABLE)
public final class CharacterStateMatrix extends UUPPodEntityWXmlId {

	/** This entity's table name. Intentionally package-private. */
	static final String TABLE = "CHARACTER_STATE_MATRIX";

	/** Description column. Intentionally package-private. */
	static final String DESCRIPTION_COLUMN = "DESCRIPTION";

	/**
	 * Name for foreign key columns. Intentionally package-private.
	 */
	static final String ID_COLUMN = TABLE + "_ID";

	/** Label column. Intentionally package-private. */
	static final String LABEL_COLUMN = "LABEL";

	/**
	 * {@code CharacterStateMatrix}-{@link Character} join table. Intentionally
	 * package-private.
	 */
	static final String MATRIX_CHARACTER_JOIN_TABLE = TABLE + "_"
			+ Character.TABLE;

	static final String CHARACTER_IDX_COLUMN = "PHYLO_CHARACTER_IDX";

	/**
	 * Column that orders the {@link Character}s. Intentionally package-private.
	 */
	static final String CHARACTER_INDEX_COLUMN = Character.TABLE + "_POSITION";

	/**
	 * Column that orders the rows. Intentionally package-private.
	 */
	static final String ROW_INDEX_COLUMN = CharacterStateRow.TABLE
			+ "_POSITION";

	/** The pPod versions of the columns. */
	@ManyToMany(fetch = FetchType.EAGER)
	// EAGER so we can manipulate it in PPodVersionInfoInterceptor: see the
	// javadoc for Interceptor: "a callback [may not] cause a collection or
	// proxy to be lazily initialized)."
	@JoinTable(name = TABLE + "_" + PPodVersionInfo.TABLE, joinColumns = { @JoinColumn(name = ID_COLUMN) }, inverseJoinColumns = { @JoinColumn(name = PPodVersionInfo.ID_COLUMN) })
	@org.hibernate.annotations.IndexColumn(name = PPodVersionInfo.TABLE
			+ "_POSITION")
	private final List<PPodVersionInfo> columnPPodVersionInfos = newArrayList();

	@XmlElement(name = "columnPPodVersion")
	@Transient
	private final List<Long> columnPPodVersions = newArrayList();

	/** Free-form description. */
	@XmlAttribute
	@Column(name = DESCRIPTION_COLUMN)
	private String description;

	/** The label for this {@code CharacterStateMatrix}. */
	@XmlAttribute
	@Column(name = LABEL_COLUMN, nullable = false)
	private String label;

	static final String OTU_IDX_COLUMN = "OTU_IDX";

	/**
	 * The inverse of {@code otus}: it's an {@code OTU}->rowNumber lookup.
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
	 * <p>
	 * Cascade-saved through {@code this.otuSet}.
	 */
	@XmlElement(name = "otuDocId")
	@XmlIDREF
	@ManyToMany
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@JoinTable(name = TABLE + "_" + OTU.TABLE, joinColumns = { @JoinColumn(name = ID_COLUMN) }, inverseJoinColumns = { @JoinColumn(name = OTU.ID_COLUMN) })
	@org.hibernate.annotations.IndexColumn(name = OTU.TABLE + "_POSITION")
	private final List<OTU> otus = newArrayList();

	/**
	 * These are the <code>OTU</code>s whose data comprises this {@code
	 * CharacterStateMatrix}.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
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
	@XmlElement(name = "character")
	@ManyToMany
	@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinTable(name = MATRIX_CHARACTER_JOIN_TABLE, joinColumns = { @JoinColumn(name = ID_COLUMN) }, inverseJoinColumns = { @JoinColumn(name = Character.ID_COLUMN) })
	@org.hibernate.annotations.IndexColumn(name = CHARACTER_INDEX_COLUMN)
	private final List<Character> characters = newArrayList();

	@XmlElement(name = "row")
	@OneToMany
	@org.hibernate.annotations.IndexColumn(name = ROW_INDEX_COLUMN)
	@JoinColumn(name = ID_COLUMN, nullable = false)
	@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private final List<CharacterStateRow> rows = newArrayList();

	/** No-arg constructor for (at least) Hibernate. */
	CharacterStateMatrix() {}

	@Override
	public CharacterStateMatrix accept(final IVisitor visitor) {
		visitor.visit(this);
		return this;
	}

	/**
	 * Add a character to the end of this matrix's characters.
	 * 
	 * @param character the character to be added
	 * @return {@code character}
	 */
	public Character addCharacter(final Character character) {
		setCharacter(getCharacters().size(), character);
		return character;
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		if (parent instanceof OTUSet) {
			this.otuSet = (OTUSet) parent;
		}

		if (characterIdx.size() == 0) {
			int i = 0;
			for (final Character character : characters) {
				characterIdx.put(character, i++);

				// Mark these as ready for new versions when persisted
				columnPPodVersionInfos.add(null);
			}
		}
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
		characters.clear();
		characterIdx.clear();
		return clearedCharacters;
	}

	/**
	 * Get a <code>Character</code> given by an index, or <code>null</code> if
	 * <code>idx</code> is too big or negative.
	 * 
	 * @param idx see description
	 * @return see description
	 */
	public Character getCharacter(final int idx) {
		if ((idx < 0) || (idx + 1 > getCharacters().size())) {
			return null;
		}
		return getCharacters().get(idx);
	}

	/**
	 * Given a pPOD id, retrieve a {@code Character}, or <code>null</code> if
	 * there is no such {@code Character}.
	 * 
	 * @param characterPPodId the pPodid
	 * @return see description
	 */
	public Character getCharacterByPPodId(final String characterPPodId) {
		for (final Character character : characters) {
			if (character.getPPodId().equals(characterPPodId)) {
				return character;
			}
		}
		return null;
	}

	/**
	 * Get an unmodifiable view of characterIdx.
	 * <p>
	 * Intentionally package-private for the time being - created for testing.
	 * 
	 * @return the characterIdx
	 */
	Map<Character, Integer> getCharacterIdx() {
		return Collections.unmodifiableMap(characterIdx);
	}

	/**
	 * Get the index of {@code character}, or <code>-1</code> if
	 * <code>character</code> is not associated with this matrix.
	 * 
	 * @param character see description
	 * @return see description
	 */
	public Integer getCharacterIdx(final Character character) {
		final Integer characterIdx = this.characterIdx.get(character);
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
	 * Getter.
	 * 
	 * @return an unmodifiable view of <code>this.columnPPodVersions</code>.
	 */
	public List<PPodVersionInfo> getColumnPPodVersionInfos() {
		return Collections.unmodifiableList(columnPPodVersionInfos);
	}

	/**
	 * Get the column pPOD versions.
	 * <p>
	 * A column version is the max pPOD version of the cells in a column.
	 * 
	 * @return the column pPOD versions
	 */
	public List<Long> getColumnPPodVersions() {
		return columnPPodVersions;
	}

	/**
	 * Getter.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Getter.
	 * 
	 * @return the label
	 */
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
		if (otuIdx.size() == 0) {
			int i = 0;
			for (final OTU otu : otus) {
				otuIdx.put(otu, i++);
			}
		}
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
	 * @return this maytrix's {@code OTUSet}
	 */
	public OTUSet getOTUSet() {
		return otuSet;
	}

	/**
	 * Get a row of this matrix given an index, or {@code null} if {@code idx}
	 * is too big or negative.
	 * 
	 * @param idx see description
	 * @return see description
	 */
	public CharacterStateRow getRow(final int idx) {
		if ((idx < 0) || (idx + 1 > getRows().size())) {
			return null;
		}
		return getRows().get(idx);
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
	 * Remove the last {@code Character}.
	 * 
	 * @return the removed {@code Character}, could be <code>null</code>.
	 */
	public Character removeLastCharacter() {
		final int lastCharacterIdx = characters.size() - 1;
		if (lastCharacterIdx == 0) {
			// nothing to do
			return null;
		}
		final Character oldCharacter = characters.remove(lastCharacterIdx);
		characterIdx.remove(oldCharacter);

		columnPPodVersionInfos.remove(lastCharacterIdx);

		// Remember, we could have a null there: see the setCharacter
		// method for
		// how we end up with a null here.
		if (oldCharacter != null) {
			oldCharacter.removeMatrix(this);
		}
		resetPPodVersionInfo();
		return oldCharacter;
	}

	/**
	 * Remove the last row of the matrix.
	 * 
	 * @return the removed row, or {@code null} if no row was removed
	 */
	public CharacterStateRow removeLastRow() {
		if (rows.get(rows.size() - 1) != null) {
			rows.get(rows.size() - 1).setMatrix(null);
			resetPPodVersionInfo();
		}
		return rows.remove(rows.size() - 1);
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
		while (columnPPodVersionInfos.size() - 1 < idx) {
			columnPPodVersionInfos.add(null);
		}
		columnPPodVersionInfos.set(idx, null);
		return this;
	}

	/**
	 * {@code null} out {@code pPodVersionInfo} and the {@link PPodVersionInfo}
	 * of the owning study.
	 * 
	 * @return this {@code CharacterStateMatrix}
	 */
	@Override
	protected CharacterStateMatrix resetPPodVersionInfo() {
		if (getPPodVersionInfo() == null) {
			// nothing to do
		} else {
			if (otuSet != null) {
				otuSet.resetPPodVersionInfo();
			}
			super.resetPPodVersionInfo();
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
	 * 
	 * @param characterIdx index
	 * @param character value
	 * 
	 * @return the {@code Character} previously at that position or {@code null}
	 *         if there was no such {@code Character}
	 */
	public Character setCharacter(final int characterIdx,
			final Character character) {
		checkNotNull(character);
		if (characters.size() > characterIdx
				&& character.equals(characters.get(characterIdx))) {
			// Nothing to do
			return character;
		}
		while (characters.size() <= characterIdx) {
			characters.add(null);
			columnPPodVersionInfos.add(null);
		}
		final Integer oldCharIdx = this.characterIdx.get(character);
		if (oldCharIdx != null) {
			characters.set(oldCharIdx, null);
			resetColumnPPodVersion(oldCharIdx);
		}
		final Character oldChar = characters.get(characterIdx);
		if (oldChar != null) {
			this.characterIdx.remove(oldChar);
		}
		characters.set(characterIdx, character);
		this.characterIdx.put(character, characterIdx);
		character.addMatrix(this);

		// the matrix has changed
		resetPPodVersionInfo();

		return oldChar;
	}

	/**
	 * Setter.
	 * 
	 * @param description the description value. {@code null} is legal.
	 * 
	 * @return this matrix
	 */
	public CharacterStateMatrix setDescription(final String description) {
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
	 * in {@code null} values for newly introduced {@code OTU}s. These {@code
	 * null}'d {@code OTU}s must be filled in by the client.
	 * <p>
	 * A copy of {@code newOtus} is made so subsequent modifications of {@code
	 * newOTUs} will have no affect on this matrix.
	 * <p>
	 * Assumes all members of {@code newOtus} are not Hibernate-detached.
	 * 
	 * @param newOtus order of the {@link OTUSet} associated with this {@code
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
	public CharacterStateMatrix setOTUs(final List<OTU> newOtus) {
		if (newOtus.equals(otus)) {
			// They're the same, nothing to do
			return this;
		}

		if (otuSet == null) {
			throw new IllegalStateException(
					"otuSet needs to be set before setOTUs(...) is called");
		}

		if (newOtus.containsAll(otuSet.getOTUs())
				&& otuSet.getOTUs().containsAll(newOtus)) {
			// They have the same elements, that's good.
		} else {
			throw new IllegalArgumentException(
					"otus (size "
							+ newOtus.size()
							+ ") does not contain the same OTU's as the matrix's OTUSet (size "
							+ otuSet.getOTUs().size() + ").");
		}

		// We're now going to move around the rows to match the new ordering
		final List<CharacterStateRow> newRows = newArrayListWithCapacity(newOtus
				.size());
		for (int newOtuIdx = 0; newOtuIdx < newOtus.size(); newOtuIdx++) {
			final OTU newOtu = newOtus.get(newOtuIdx);

			// oldIdx is where newOtu used to be
			final Integer oldIdx = otuIdx.get(newOtu);
			CharacterStateRow oldRow = null;
			if (oldIdx == null) {
				// It's a new row - it will be filled in later.
			} else {
				oldRow = rows.get(oldIdx);
			}
			newRows.add(oldRow);
		}

		otus.clear();
		otuIdx.clear();
		for (int i = 0; i < newRows.size(); i++) {
			final OTU newOtu = newOtus.get(i);
			otus.add(newOtu);
			otuIdx.put(newOtu, i);
			if (i < rows.size()) {
				rows.set(i, newRows.get(i));
			} else {
				rows.add(newRows.get(i));
			}
		}

		// Get rid of rows that no longer have OTU's
		while (rows.size() > newRows.size()) {
			rows.remove(rows.size() - 1);
		}

		resetPPodVersionInfo();
		return this;
	}

	/**
	 * Setter. Intentionally package-private and meant to be called from {@code
	 * OTUSet}.
	 * 
	 * @param otuSet new {@code OTUSet} for this matrix. nullable.
	 * 
	 * @return {@code otuSet}
	 */
	OTUSet setOTUSet(final OTUSet otuSet) {
		if (nullSafeEquals(this.otuSet, otuSet)) {
			// still the same
		} else {
			this.otuSet = otuSet;
			resetPPodVersionInfo();
		}
		return otuSet;
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
	 * @param index see description
	 * @param row see description
	 * 
	 * @return the {@code CharacterStateRow} that was previously there, or
	 *         {@code null} if there was no {@code CharacterStateRow} previously
	 *         there
	 */
	public CharacterStateRow setRow(final int index, final CharacterStateRow row) {
		checkNotNull(row);

		while (rows.size() <= index) {
			rows.add(null);
			resetPPodVersionInfo();
		}
		final CharacterStateRow oldRow = rows.get(index);
		if (row.equals(oldRow)) {
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
			rows.set(index, row.setMatrix(this));
			resetPPodVersionInfo();
		}
		return oldRow;
	}

	/**
	 * Constructs a <code>String</code> with attributes in name=value format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	@Override
	public String toString() {
		final String TAB = ",";

		final StringBuilder retValue = new StringBuilder();

		retValue.append(
				"CharacterStateMatrix(" + otuSet.getOTUs().size() + ","
						+ otus.size() + "," + otuIdx.size() + ",").append(TAB)
				.append(TAB).append("label=").append(this.label).append(TAB)
				.append("otuSet=").append(this.otuSet).append(TAB).append(
						"otus=").append(this.otus).append(TAB)
				.append("otuIdx=").append(this.otuIdx).append(TAB).append('\n')
				.append("rows=").append(this.rows).append(TAB).append(
						"characters=").append(this.characters).append(TAB)
				.append("characterIdx=").append(this.characterIdx).append(")");

		return retValue.toString();
	}

}
