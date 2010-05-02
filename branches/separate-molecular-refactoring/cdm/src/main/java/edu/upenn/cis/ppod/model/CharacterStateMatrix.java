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
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Lists.newArrayList;
import static edu.upenn.cis.ppod.util.CollectionsUtil.nullFill;
import static edu.upenn.cis.ppod.util.CollectionsUtil.nullFillAndSet;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.modelinterfaces.IPPodVersionedWithOTUSet;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A standard matrix - aka a character matrix.
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class CharacterStateMatrix extends UUPPodEntityWXmlId implements
		IPPodVersionedWithOTUSet, Iterable<CharacterStateRow> {

	static final String CHARACTER_IDX_COLUMN = PPodEntity.TABLE + "_IDX";

	/**
	 * Column that orders the {@link AbstractCharacter}s. Intentionally
	 * package-private.
	 */
	static final String CHARACTERS_POSITION_COLUMN = PPodEntity.TABLE
														+ "_POSITION";

	/** Description column. Intentionally package-private. */
	static final String DESCRIPTION_COLUMN = "DESCRIPTION";

	/** This entity's table name. Intentionally package-private. */
	static final String TABLE = "CHARACTER_STATE_MATRIX";

	/**
	 * Name for foreign key columns. Intentionally package-private.
	 */
	static final String ID_COLUMN = TABLE + "_ID";

	static final String LABEL_COLUMN = "LABEL";

	static final String OTU_IDX_COLUMN = "OTU_IDX";

	/** The pPOD versions of the columns. */
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

	/**
	 * These are the <code>OTU</code>s whose data comprises this {@code
	 * CharacterStateMatrix}.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = OTUSet.ID_COLUMN, nullable = false)
	@CheckForNull
	private OTUSet otuSet;

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

		getOTUsToRows().accept(visitor);

		super.accept(visitor);

	}

	@Override
	public void afterUnmarshal() {
		super.afterUnmarshal();
		nullFill(columnPPodVersionInfos,
				get(getOTUsToRows()
						.getOTUsToValues().values(), 0).getCellsSize());
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
				columnPPodVersions.add(null);
			} else {
				columnPPodVersions.add(columnVersionInfo.getPPodVersion());
			}
		}
		return true;
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

	public abstract int getColumnsSize();

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
		return getOTUsToRows().get(otu);
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
		return getOTUsToRows().getValuesInOTUOrder(getOTUSet()).iterator();
	}

	/**
	 * Set row at <code>otu</code> to <code>row</code>.
	 * <p>
	 * Assumes {@code row} does not belong to another matrix.
	 * <p>
	 * Assumes {@code row} is not detached.
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
			final CharacterStateRow row) {
		checkNotNull(otu);
		checkNotNull(row);
		return getOTUsToRows().put(otu, row);
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
	CharacterStateMatrix resetColumnPPodVersion(@Nonnegative final int position) {
		checkArgument(position >= 0, "position is negative");
		nullFillAndSet(getColumnPPodVersionInfos(), position, null);
		return this;
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
		getOTUsToRows().setOTUs(getOTUSet());
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
