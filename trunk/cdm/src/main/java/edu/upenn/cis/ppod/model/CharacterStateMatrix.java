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
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.util.CollectionsUtil.nullFillAndSet;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.IndexColumn;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;

import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.OTUCharacterStateRowPair;
import edu.upenn.cis.ppod.util.OTUSomethingPair;

/**
 * A standard matrix - aka a character matrix.
 * 
 * @author Sam Donnelly
 */
@XmlSeeAlso( { DNAStateMatrix.class, RNAStateMatrix.class })
@Entity
@Table(name = CharacterStateMatrix.TABLE)
public class CharacterStateMatrix extends UUPPodEntityWXmlId implements
		Iterable<CharacterStateRow> {

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
	private final List<PPodVersionInfo> columnPPodVersionInfos = newArrayList();

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
	 * The rows of the matrix. We don't do save_update cascades since we want to
	 * control when rows are added to the persistence context. We sometimes
	 * don't want the rows saved or reattached when the the matrix is.
	 */
	@org.hibernate.annotations.CollectionOfElements
	@JoinTable(name = OTU.TABLE + "_" + CharacterStateRow.TABLE, joinColumns = @JoinColumn(name = ID_COLUMN), inverseJoinColumns = @JoinColumn(name = CharacterStateRow.ID_COLUMN))
	@org.hibernate.annotations.MapKeyManyToMany(joinColumns = @JoinColumn(name = OTU.ID_COLUMN))
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private final Map<OTU, CharacterStateRow> otusToRows = newHashMap();

	/**
	 * The position of an {@code OTU} in {@code otuOrdering} signifies its row
	 * number in <code>row</code>. So <code>otuOrdering</code> is a rowNumber->
	 * {@code OTU} lookup.
	 */
	@ManyToMany
	@JoinTable(inverseJoinColumns = @JoinColumn(name = OTU.ID_COLUMN))
	@org.hibernate.annotations.IndexColumn(name = OTU.TABLE + "_POSITION")
	private final List<OTU> otuOrdering = newArrayList();

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
	 * For marshalling {@code otusToRows}. Since a {@code Map}'s key couldn't be
	 * an {@code XmlIDREF} in JAXB - at least not easily.
	 */
	@Transient
	private final Set<OTUCharacterStateRowPair> otuRowPairs = newHashSet();

	/** No-arg constructor for (at least) Hibernate. */
	CharacterStateMatrix() {}

	@Override
	public CharacterStateMatrix accept(final IVisitor visitor) {
		visitor.visit(this);
		for (final Character character : getCharacters()) {
			character.accept(visitor);
		}
		for (final CharacterStateRow row : getOTUsToRowsModifiable().values()) {
			row.accept(visitor);
		}
		super.accept(visitor);
		return this;
	}

	@Override
	public void afterUnmarshal() {
		for (final OTUSomethingPair<CharacterStateRow> otuRowPair : getOTURowPairsModifiable()) {
			getOTUsToRowsModifiable().put(otuRowPair.getFirst(),
					otuRowPair.getSecond());
			otuRowPair.getSecond().setMatrix(this);
		}

		// We're done with this - clear it out
		getOTURowPairsModifiable().clear();

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
		for (final Map.Entry<OTU, CharacterStateRow> otuToRow : getOTUsToRowsModifiable()
				.entrySet()) {
			getOTURowPairsModifiable().add(
					OTUCharacterStateRowPair.of(otuToRow.getKey(), otuToRow
							.getValue()));
		}
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
	 * @return an unmodifiable view of the <code>Character</code> ordering
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
	 * Return an unmodifiable view of this matrix's <code>OTUSet</code>
	 * ordering.
	 * 
	 * @return see description
	 */
	public List<OTU> getOTUOrdering() {
		return Collections.unmodifiableList(getOTUOrderingModifiable());
	}

	@XmlElementWrapper(name = "otuOrdering")
	@XmlElement(name = "otuDocId")
	@XmlIDREF
	private List<OTU> getOTUOrderingModifiable() {
		return otuOrdering;
	}

	@XmlElement(name = "otuRowPair")
	private Set<OTUCharacterStateRowPair> getOTURowPairsModifiable() {
		return otuRowPairs;
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

	/**
	 * Get the otusToRows.
	 * 
	 * @return the otusToRows
	 */
	private Map<OTU, CharacterStateRow> getOTUsToRowsModifiable() {
		return otusToRows;
	}

	/**
	 * Get the row indexed by an OTU or {@code null} if there is no such row.
	 * 
	 * @param otu the index
	 * 
	 * @return the row
	 */
	@Nullable
	public CharacterStateRow getRow(final OTU otu) {
		checkNotNull(otu);
		return getOTUsToRowsModifiable().get(otu);
	}

	/**
	 * Get a possibly unmodifiable view of this matrix's rows in
	 * {@link #getOTUOrdering()} order.
	 * 
	 * @return a possibly unmodifiable view of this matrix's rows in {@code
	 *         getOTUOrdering()} order
	 * 
	 * @throws IllegalStateException if this matrix's OTU ordering does not have
	 *             the same elements as {@code getOTUSet()}
	 */
	public List<CharacterStateRow> getRows() {
		final ImmutableSet<OTU> otuOrderingAsSet = ImmutableSet
				.copyOf(getOTUOrdering());
		checkState(otuOrderingAsSet.equals(getOTUSet().getOTUs()),
				"otu ordering is not in sync with this matrix's OTUSet");

		final List<CharacterStateRow> rows = newArrayList();
		for (final OTU otu : getOTUOrdering()) {
			rows.add(getOTUsToRowsModifiable().get(otu));
		}
		return Collections.unmodifiableList(rows);
	}

	public Iterator<CharacterStateRow> iterator() {
		return getRows().iterator();
	}

	/**
	 * Set row at <code>otu</code> to <code>row</code>.
	 * <p>
	 * Assumes {@code newRow} does not belong to another matrix.
	 * <p>
	 * Assumes {@code newRow} is not detached.
	 * 
	 * @param otu index of the row we are adding
	 * @param newRow the row we're adding
	 * 
	 * @return the row that was previously there, or {@code null} if there was
	 *         no row previously there
	 * 
	 * @throw IllegalArgumentException if {@code otu} does not belong to this
	 *        matrix's {@code OTUSet}
	 */
	@CheckForNull
	public CharacterStateRow putRow(final OTU otu,
			final CharacterStateRow newRow) {
		checkNotNull(otu);
		checkNotNull(newRow);
		checkArgument(getOTUSet().getOTUs().contains(otu),
				"otu does not belong to this matrix");

		final CharacterStateRow originalRow = getOTUsToRowsModifiable().put(
				otu, newRow);
		if (newRow.equals(originalRow)) {

		} else {
			if (originalRow != null) {
				originalRow.setMatrix(null);
			}
			newRow.setMatrix(this);
			resetPPodVersionInfo();
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
	 * It is legal for two characters to have the same label, but not to be
	 * {@code .equals} to each other.
	 * 
	 * @param newCharacters the new characters
	 * 
	 * @return the characters removed as a result of this operation
	 * 
	 * @throws IllegalArgumentException if any of {@code characters} are {@code
	 *             .equals} to each other. NOTE: this constraint does not hold
	 *             in a {@link MolecularStateMatrix}
	 */
	public List<Character> setCharacters(
			final List<? extends Character> newCharacters) {
		checkNotNull(newCharacters);

		if (newCharacters.equals(getCharacters())) {
			return Collections.emptyList();
		}

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
	 * @throws IllegalArgumentException if the members of {@code otuOrdering}
	 *             are not the same as those of this {@code
	 *             CharacterStateMatrix}'s associated {@code OTUSet}
	 * @throws IllegalStateException if the OTU set has not been set, i.e. if
	 *             {@link #getOTUSet() == null}
	 */
	public CharacterStateMatrix setOTUOrdering(final List<OTU> newOTUOrdering) {
		checkNotNull(newOTUOrdering);

		checkState(getOTUSet() != null,
				"otuSet needs to be set before setOTUOrdering(...) is called");

		// We want both newOTUs and this.otuSet to have the same elements
		checkArgument(
				newOTUOrdering.containsAll(getOTUSet().getOTUs())
						&& getOTUSet().getOTUs().containsAll(newOTUOrdering),
				"otuOrdering (size "
						+ newOTUOrdering.size()
						+ ") does not contain the same OTU's as the matrix's OTUSet (size "
						+ getOTUSet().getOTUs().size() + ").");

		if (newOTUOrdering.equals(getOTUOrdering())) {
			// They're the same, nothing to do
			return this;
		}

		getOTUOrderingModifiable().clear();
		getOTUOrderingModifiable().addAll(newOTUOrdering);
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

}
