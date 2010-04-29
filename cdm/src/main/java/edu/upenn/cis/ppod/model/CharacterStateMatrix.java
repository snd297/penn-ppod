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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
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

import edu.upenn.cis.ppod.modelinterfaces.IPPodVersionedWithOTUSet;
import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.OTUSomethingPair;

/**
 * A standard matrix - aka a character matrix.
 * 
 * @author Sam Donnelly
 */
@XmlSeeAlso( { DNAMatrix.class })
@Entity
@Table(name = CharacterStateMatrix.TABLE)
public class CharacterStateMatrix extends UUPPodEntityWXmlId implements
		IPPodVersionedWithOTUSet, Iterable<CharacterStateRow> {

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
	static final String CHARACTERS_POSITION_COLUMN = Character.TABLE
														+ "_POSITION";

	/** The pPod versions of the columns. */
	@ManyToMany
	@JoinTable(inverseJoinColumns = { @JoinColumn(name = PPodVersionInfo.ID_COLUMN) })
	@OrderColumn(name = PPodVersionInfo.TABLE + "_POSITION")
	private final List<PPodVersionInfo> columnPPodVersionInfos = newArrayList();

	@XmlElement(name = "columnPPodVersion")
	@Transient
	private final List<Long> columnPPodVersions = newArrayList();

	/** Free-form description. */
	@Column(name = DESCRIPTION_COLUMN, nullable = true)
	@CheckForNull
	private String description;

	/** The label for this {@code CharacterStateMatrix}. */
	@Column(name = LABEL_COLUMN, nullable = false)
	@CheckForNull
	private String label;

	static final String OTU_IDX_COLUMN = "OTU_IDX";

	@OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
	private OTUsToCharacterStateRows otusToRows;

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
	@ElementCollection
	@JoinTable(name = TABLE + "_" + CHARACTER_IDX_COLUMN, joinColumns = @JoinColumn(name = ID_COLUMN))
	@MapKeyJoinColumn(name = Character.ID_COLUMN)
	@Column(name = CHARACTER_IDX_COLUMN)
	private final Map<Character, Integer> charactersToPositions = newHashMap();

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
	@JoinTable(joinColumns = { @JoinColumn(name = ID_COLUMN) }, inverseJoinColumns = { @JoinColumn(name = Character.ID_COLUMN) })
	@IndexColumn(name = CHARACTERS_POSITION_COLUMN)
	private final List<Character> characters = newArrayList();

	/** No-arg constructor for (at least) Hibernate. */
	CharacterStateMatrix() {}

	@Inject
	protected CharacterStateMatrix(final OTUsToCharacterStateRows otusToRows) {
		this.otusToRows = otusToRows;
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
			columnPPodVersionInfos.add(null);
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
		for (final OTUSomethingPair<CharacterStateRow> otuRowPair : otusToRows
				.getOTUValuePairs()) {
			otuRowPair.getSecond().setMatrix(this);
		}
	}

	@Override
	public boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {
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

	private List<PPodVersionInfo> determineNewColumnHeaderPPodVersionInfos(
			final List<? extends Character> newCharacters) {

		final BiMap<Integer, Integer> originalPositionsToNewPositions = HashBiMap
				.create(getCharactersSize());
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
	 * @param pPodCharacterPosition the position of the character we want
	 * @return
	 */
	public Character getCharacter(final int characterPosition) {
		return getCharacters().get(characterPosition);
	}

	/**
	 * Created for testing.
	 */
	Map<Character, Integer> getCharacterPosition() {
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

	public Iterator<Character> getCharactersIterator() {
		return Collections.unmodifiableList(getCharacters()).iterator();
	}

	/**
	 * Get the number of characters this matrix has.
	 * 
	 * @return the number of characters this matrix has
	 */
	public int getCharactersSize() {
		return getCharacters().size();
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
	 * Get a reference of the {@code PPodVersionInfo}s for each for the columns
	 * of the matrix.
	 * <p>
	 * Intentionally package-private.
	 * 
	 * @return a mutable view of the {@code PPodVersionInfo}s for each for the
	 *         columns of the matrix
	 */
	protected List<PPodVersionInfo> getColumnPPodVersionInfos() {
		return columnPPodVersionInfos;
	}

	public Iterator<PPodVersionInfo> getColumnPPodVersionInfosIterator() {
		return Collections.unmodifiableList(getColumnPPodVersionInfos())
				.iterator();
	}

	/**
	 * Created for testing.
	 * 
	 * @return
	 */
	List<Long> getColumnPPodVersions() {
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
	 * Getter. {@code null} when the object is constructed, but never {@code
	 * null} for persistent objects.
	 * 
	 * @return the label
	 */
	@XmlAttribute
	@Nullable
	public String getLabel() {
		return label;
	}

	/**
	 * Getter. Will be {@code null} when object is first created, but never
	 * {@code null} for persistent objects.
	 * 
	 * @return this matrix's {@code OTUSet}
	 */
	@Nullable
	public OTUSet getOTUSet() {
		return otuSet;
	}

	/**
	 * Get the otusToRows.
	 * <p>
	 * Not {@code final} for JAXB
	 * 
	 * @return the otusToRows
	 */
	@XmlElement(name = "otusToRows")
	protected OTUsToCharacterStateRows getOTUsToRows() {
		return otusToRows;
	}

	/**
	 * Get the row indexed by an OTU or {@code null} if {@code otu} has not had
	 * a row assigned to it.
	 * 
	 * @param otu the index
	 * 
	 * @return the row, or {@code null} if {@code otu} has not had a row
	 *         assigned to it
	 * 
	 * @throws IllegalArgumentException if {@code otu} does not belong to this
	 *             matrix's {@code OTUSet}
	 */
	@Nullable
	public CharacterStateRow getRow(final OTU otu) {
		checkNotNull(otu);
		return getOTUsToRows().get(otu, this);
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
	 * return an iterator over this matrix's rows
	 */
	public Iterator<CharacterStateRow> iterator() {
		return getOTUsToRows().getValuesInOTUOrder(getOTUSet()).iterator();
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
		return getOTUsToRows().put(otu, newRow, this);
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
	CharacterStateMatrix resetColumnPPodVersion(final int position) {
		checkArgument(position >= 0, "position is negative");
		nullFillAndSet(getColumnPPodVersionInfos(), position, null);
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
	 * It is legal for two characters to have the same label, but not to be
	 * {@code .equals} to each other.
	 * 
	 * @param newCharacters the new characters
	 * 
	 * @return the characters removed as a result of this operation
	 * 
	 * @throws IlelgalArgumentException if any of {code newCharacters} is
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
	 * 
	 * @param description the description value, {@code null} is allowed
	 * 
	 * @return this matrix
	 */
	public CharacterStateMatrix setDescription(
			@CheckForNull final String description) {
		if (equal(getDescription(), description)) {
			// nothing to do
		} else {
			this.description = description;
			setInNeedOfNewPPodVersionInfo();
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
	public CharacterStateMatrix setInNeedOfNewPPodVersionInfo() {
		if (getOTUSet() != null) {
			getOTUSet().setInNeedOfNewPPodVersionInfo();
		}
		super.setInNeedOfNewPPodVersionInfo();
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
			setInNeedOfNewPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Setter.
	 * <p>
	 * Meant to be called only from objects responsible for managing the {@code
	 * OTUSET<->CharacterStateMatrix} relationship.
	 * <p>
	 * This method will remove otusToRows from this matrix as necessary.
	 * <p>
	 * If there are any new {@code OTU}s in {@code newOTUSet}, then {@code
	 * getRow(theNewOTU) == null}. That is, it adss {@code null} rows for new
	 * {@code OTU}s.
	 * 
	 * @param newOTUSet new {@code OTUSet} for this matrix, or {@code null} if
	 *            we're destroying the association
	 * 
	 * @return this
	 */
	protected CharacterStateMatrix setOTUSet(
			@CheckForNull final OTUSet newOTUSet) {
		otuSet = newOTUSet;
		getOTUsToRows().setOTUs(getOTUSet(), this);
		return this;
	}

	/**
	 * Set the otusToRows.
	 * 
	 * @param otusToRows the otusToRows to set
	 * 
	 * @return this
	 */
	@edu.umd.cs.findbugs.annotations.SuppressWarnings
	@SuppressWarnings("unused")
	private CharacterStateMatrix setOTUsToRows(
			final OTUsToCharacterStateRows newOTUsToRows) {
		otusToRows = newOTUsToRows;
		return this;
	}

}
