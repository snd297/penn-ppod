/*
 * Copyright (C) 2010 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Lists.newArrayList;
import static edu.upenn.cis.ppod.util.CollectionsUtil.nullFill;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OrderColumn;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.imodel.ICell;
import edu.upenn.cis.ppod.imodel.IMatrix;
import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IOTUKeyedMap;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.IRow;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A matrix is a set of OTU-keyed rows with column header pPOD versions which
 * are each equal to the largest cell version in the column.
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class Matrix<R extends IRow<C, ?>, C extends ICell<?, ?>>
		extends UUPPodEntityWithDocId
		implements IMatrix<R, C> {

	/** Description column. */
	public static final String DESCRIPTION_COLUMN = "DESCRIPTION";

	/** Label column. */
	public static final String LABEL_COLUMN = "LABEL";

	/** The pPod versions of the columns. */
	@ManyToMany
	@JoinTable(inverseJoinColumns =
		{ @JoinColumn(name = VersionInfo.JOIN_COLUMN) })
	@OrderColumn(name = VersionInfo.TABLE + "_POSITION")
	private final List<VersionInfo> columnVersionInfos = newArrayList();

	/**
	 * For sending over the wire to web clients. Because we don't want to send
	 * the whole {@link VersionInfo}.
	 */
	@Transient
	private final List<Long> columnVersions = newArrayList();

	/** Free-form description. */
	@Column(name = DESCRIPTION_COLUMN, nullable = true)
	@CheckForNull
	private String description;

	/** The label for this {@code Matrix}. */
	@Column(name = LABEL_COLUMN, nullable = false)
	@Nullable
	private String label;

	/**
	 * These are the <code>OTU</code>s whose data comprises this
	 * {@code CharacterStateMatrix}.
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false,
			targetEntity = OTUSet.class)
	@JoinColumn(name = OTUSet.JOIN_COLUMN, insertable = false,
				updatable = false)
	@Nullable
	private IOTUSet parent;

	/** Default constructor. */
	Matrix() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		getOTUKeyedRows().accept(visitor);
		super.accept(visitor);
	}

	/** {@inheritDoc} */
	protected void addColumn(
			final int columnNo,
			final List<? extends C> column) {
		checkArgument(columnNo >= 0, "columnNo < 0");
		checkArgument(
				columnNo < getColumnsSize(),
				"columnNo >= number of columns");

		int rowPos = -1;
		for (final R row : getRows().values()) {
			rowPos++;
			final List<C> cells = newArrayList(row.getCells());
			cells.add(columnNo, column.get(rowPos));
			row.setCells(cells);
		}
	}

	public void afterUnmarshal() {
		getOTUKeyedRows().afterUnmarshal();
		nullFill(getColumnVersionInfosModifiable(), get(getRows()
				.values(), 0)
				.getCells()
				.size());
	}

	protected void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent) {
		this.parent = (IOTUSet) parent;
	}

	@Override
	protected boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {

		if (getColumnVersions().size() != 0) {
			throw new AssertionError(
					"getColumnsVersions().size() should be 0 before marshal");
		}

		int columnVersionInfoPos = -1;
		for (final VersionInfo columnVersionInfo : getColumnVersionInfos()) {
			columnVersionInfoPos++;
			if (columnVersionInfo == null) {
				getColumnVersionsModifiable().add(null);
			} else {
				getColumnVersionsModifiable()
						.add(columnVersionInfo.getVersion());
			}
		}
		return true;
	}

	/** {@inheritDoc} */
	public Integer getColumnsSize() {
		return Integer.valueOf(getColumnVersionInfos().size());
	}

	/** {@inheritDoc} */
	public List<VersionInfo> getColumnVersionInfos() {
		return Collections.unmodifiableList(columnVersionInfos);
	}

	/**
	 * A modifiable reference to the column pPOD version infos.
	 * 
	 * @return a modifiable reference to the column pPOD version infos
	 */
	List<VersionInfo> getColumnVersionInfosModifiable() {
		return columnVersionInfos;
	}

	/** {@inheritDoc} */
	public List<Long> getColumnVersions() {
		return Collections.unmodifiableList(columnVersions);
	}

	@XmlElement(name = "columnVersion")
	protected List<Long> getColumnVersionsModifiable() {
		return columnVersions;
	}

	/**
	 * Getter.
	 * <p>
	 * {@code null} is a legal value.
	 * 
	 * @return the description
	 */
	@XmlAttribute
	public String getDescription() {
		return description;
	}

	/**
	 * Getter. {@code null} when the object is constructed, but never
	 * {@code null} for persistent objects.
	 * <p>
	 * Will {@code null} only until {@code setLabel()} is called for newly
	 * created objects. Will never be {@code null} for persistent objects.
	 * 
	 * @return the label
	 */
	@XmlAttribute
	public String getLabel() {
		return label;
	}

	/**
	 * Get the otusToRows.
	 * <p>
	 * In perfect world, this would live in a subclass since it does impose a
	 * certain implementation - storing rows in an OTU-to-row map.
	 * <p>
	 * 
	 * @return the otusToRows
	 */
	abstract IOTUKeyedMap<R> getOTUKeyedRows();

	/**
	 * Getter. Will be {@code null} when object is first created or matrices
	 * that have been severed from their parent, but never {@code null} for
	 * objects freshly pulled out of the database.
	 * 
	 * @return this matrix's {@code OTUSet}
	 */
	public IOTUSet getParent() {
		return parent;
	}

	/** {@inheritDoc} */
	public Map<IOTU, R> getRows() {
		return Collections
				.unmodifiableMap(getOTUKeyedRows().getValues());
	}

	/**
	 * {@inheritDoc}
	 */
	public R putRow(final IOTU otu, final R row) {
		checkNotNull(otu);
		checkNotNull(row);
		return getOTUKeyedRows().put(otu, row);
	}

	/**
	 * Removes the cells in the column.
	 * 
	 * @param columnNo which column to remove
	 * 
	 * @return the removed cells
	 */
	protected List<C> removeColumnHelper(final int columnNo) {
		checkArgument(
				columnNo >= 0,
				"columnNo < 0");
		checkArgument(
				columnNo < getColumnsSize(),
				"columnNo >= number of columns");

		final List<C> removedColumn = newArrayList();
		for (final R row : getRows().values()) {
			final List<C> cells = newArrayList(row.getCells());
			final C removedCell = cells.remove(columnNo);
			row.setCells(cells);
			removedColumn.add(removedCell);
		}
		return removedColumn;
	}

	/** {@inheritDoc} */
	public void setColumnVersionInfo(
			final int pos,
			final VersionInfo versionInfo) {
		checkNotNull(versionInfo);
		checkArgument(pos < getColumnVersionInfos().size(),
				"pos is bigger than getColumnVersionInfos().size()");
		getColumnVersionInfosModifiable().set(pos, versionInfo);
	}

	/** {@inheritDoc} */
	public void setColumnVersionInfos(
			final VersionInfo versionInfo) {
		for (int pos = 0; pos < getColumnVersionInfos().size(); pos++) {
			setColumnVersionInfo(pos, versionInfo);
		}
	}

	/** {@inheritDoc} */
	public void setDescription(
			@Nullable final String description) {
		if (equal(description, getDescription())) {
			// nothing to do
		} else {
			this.description = description;
			setInNeedOfNewVersion();
		}
	}

	/** {@inheritDoc} */
	public void setInNeedOfNewColumnVersion(final int position) {
		checkArgument(position >= 0, "position is negative");
		checkArgument(position < getColumnsSize(),
				"position " + position
						+ " is too large for the number of columns "
						+ getColumnVersionInfos().size());
		columnVersionInfos.set(position, null);
	}

	@Override
	public void setInNeedOfNewVersion() {
		if (getParent() != null) {
			getParent().setInNeedOfNewVersion();
		}
		super.setInNeedOfNewVersion();
	}

	/** {@inheritDoc} */
	public void setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(getLabel())) {
			// they're the same, nothing to do
		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
	}

	/** {@inheritDoc} */
	public void setParent(
			@Nullable final IOTUSet otuSet) {
		checkState(
				getOTUKeyedRows() != null,
				"getOTUKeyedRows() returned null - has the conrete class been constructed correctly, w/ its OTU->X dependency?");
		this.parent = otuSet;
		updateOTUs();
	}

	/** {@inheritDoc} */
	public void updateOTUs() {
		getOTUKeyedRows().updateOTUs();
	}
}
