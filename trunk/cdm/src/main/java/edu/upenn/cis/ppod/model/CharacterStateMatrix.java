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
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Maps.newHashMap;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;
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

//
// /**
// * Keep it private because it's a non-cheap operation to be used sparingly.
// */
// private List<CharacterStateCell> getColumn(final Character character) {
// final int characterIdx = getCharacterIdx().get(character);
// final List<CharacterStateCell> columnCells = newArrayList();
// for (final CharacterStateRow row : getRows()) {
// columnCells.add(row.getCells().get(characterIdx));
// }
// return columnCells;
// }
//
// private List<CharacterStateCell> setColumn(final Character character,
// final List<CharacterStateCell> column) {
// checkArgument(column.size() == getRows().size(), "column has "
// + column.size() + " entries, but matrix has "
// + getRows().size() + " rows");
// final int characterIdx = getCharacterIdx().get(character);
// final List<CharacterStateCell> columnCells = newArrayList();
// for (int i = 0; i < getRows().size(); i++) {
// final List<CharacterStateCell> originalCells = getRows().get(i).s
//			
// }
// return this;
// }

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
	 * there is no such {@code Character} or if {@code characterPPodId == null}.
	 * 
	 * @param characterPPodId the pPOD id
	 * @return see description
	 */
	public Character getCharacterByPPodId(final String characterPPodId) {
		return findIf(getCharacters(), compose(equalTo(characterPPodId),
				IUUPPodEntity.getPPodId));
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
	 * Get an unmodifiable view of the <code>Character</code> ordering.
	 * 
	 * @return an unmodifiable view of the <code>Character</code> ordering.
	 */
	public List<Character> getCharacters() {
		return Collections.unmodifiableList(characters);
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
	 * Get a row of this matrix given the OTU of the row, or {@code null} if
	 * {@code otu} does not belong to this matrix.
	 * 
	 * 
	 * @param otu see description
	 * @return see description
	 */
	public CharacterStateRow getRow(final OTU otu) {
		final Integer otuIdx = getOTUIdx().get(otu);
		if (otuIdx == null) {
			return null;
		}
		return getRows().get(otuIdx);
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
		if (getPPodVersionInfo() == null || getSuppressResetPPodVersionInfo()) {
			// nothing to do
		} else {
			if (otuSet != null) {
				otuSet.resetPPodVersionInfo();
			}
			super.resetPPodVersionInfo();
		}
		return this;
	}

// public List<Character> setCharacters(final List<Character> newCharacters) {
// checkNotNull(newCharacters);
// if (newCharacters.equals(getCharacters())) {
// // they're the same, nothing to do
// return getCharacters();
// }
//
// // Move Characters around - start by removing all characters
// final List<Character> clearedTargetCharacters = clearCharacters();
// final Map<Character, Integer> originalCharacterIndex =
	// newHashMap(getCharacterIdx());
//
// final Map<Integer, Integer> oldCharIdxsByNewCharIdx = newHashMap();
// for (final Character newCharacter : newCharacters) {
// Character newTargetCharacter;
// if (null == (newTargetCharacter = findIf(clearedTargetCharacters,
// compose(equalTo(newCharacter.getPPodId()),
// IUUPPodEntity.getPPodId)))) {
// newTargetCharacter = newCharacter;
// newTargetCharacter.setPPodId();
// }
// this.characters.add(newTargetCharacter);
// newTargetCharacter.setLabel(newCharacter.getLabel());
//
// for (final CharacterState sourceState : newCharacter.getStates()
// .values()) {
// CharacterState targetState;
// if (null == (targetState = newTargetCharacter.getStates().get(
// sourceState.getStateNumber()))) {
// targetState = newTargetCharacter.addState(stateFactory
// .create(sourceState.getStateNumber()));
//
// }
// targetState.setLabel(sourceState.getLabel());
// }
//
// oldCharIdxsByNewCharIdx.put(targetMatrix.getCharacterIdx().get(
// newTargetCharacter), oldIdxsByChararacter
// .get(newTargetCharacter));
//
// for (final Attachment sourceAttachment : newCharacter
// .getAttachments()) {
// final Set<Attachment> targetAttachments = newTargetCharacter
// .getAttachmentsByStringValue(sourceAttachment
// .getStringValue());
// Attachment targetAttachment = getOnlyElement(targetAttachments,
// null);
// if (targetAttachment == null) {
// targetAttachment = attachmentProvider.get();
// targetAttachment.setPPodId();
// }
// newTargetCharacter.addAttachment(targetAttachment);
// mergeAttachment.merge(targetAttachment, sourceAttachment);
// }
// }
//
// return originalCharacters;
// 

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
	 * {@link #setOTUs(List)} which reorders rows.
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
		final Integer newCharacterOriginalIdx = this.characterIdx
				.get(character);
		if (newCharacterOriginalIdx != null) {
			characters.set(newCharacterOriginalIdx, null);
			resetColumnPPodVersion(newCharacterOriginalIdx);
		}
		final Character oldCharacter = characters.get(characterIdx);
		if (oldCharacter != null) {
			this.characterIdx.remove(oldCharacter);
		}
		characters.set(characterIdx, character);
		this.characterIdx.put(character, characterIdx);
		character.addMatrix(this);

		// the matrix has changed
		resetPPodVersionInfo();

		return oldCharacter;
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
	 * in {@code null} values for newly introduced rows. These {@code null}'d
	 * rows must be filled in by the client.
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
			final OTU newOtu = newOtus.get(i);
			otus.add(newOtu);
			otuIdx.put(newOtu, i);
			setRow(newOtu, newRows.get(i)); // could be setting it to null
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
	 * @param otu index of the row we are adding
	 * @param row see description
	 * 
	 * @return the {@code CharacterStateRow} that was previously there, or
	 *         {@code null} if there was no {@code CharacterStateRow} previously
	 *         there
	 */
	public CharacterStateRow setRow(final OTU otu, final CharacterStateRow row) {
		checkArgument(getOTUIdx().get(otu) != null,
				"otu does not belong to this matrix");

		final Integer otuIdx = getOTUIdx().get(otu);
		while (rows.size() <= otuIdx) {
			rows.add(null);
			resetPPodVersionInfo();
		}
		final CharacterStateRow oldRow = rows.get(otuIdx);
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
			rows.set(otuIdx, row.setMatrix(this));
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
