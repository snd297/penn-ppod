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

import org.hibernate.annotations.Target;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
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
public abstract class Matrix<R extends IRow<?, ?>>
		extends UUPPodEntityWithDocId
		implements IMatrix<R> {

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
	@CheckForNull
	private String label;

	/**
	 * These are the <code>OTU</code>s whose data comprises this
	 * {@code CharacterStateMatrix}.
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = OTUSet.JOIN_COLUMN)
	@CheckForNull
	@Target(OTUSet.class)
	private IOTUSet parent;

	/** Default constructor. */
	Matrix() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		getOTUKeyedRows().accept(visitor);
		super.accept(visitor);
	}

	public void afterUnmarshal() {
		getOTUKeyedRows().afterUnmarshal();
	}

	/**
	 * Set the values of the column version numbers for marshalled matrices.
	 * <p>
	 * <strong>Outside of testing, clients should never call this
	 * method.</strong> In normal usage, these values are populated when an
	 * object in marshalled.
	 * 
	 * @param columnVersions values
	 */
	// @VisibleForTesting
	// private void setColumnVersions(final List<Integer> columnVersions) {
	// checkNotNull(columnVersions);
	// checkArgument(columnVersions.size() == getColumnsSize(),
	// "columnVersions size is not the same as getColumnsSize()");
	//
	// this.columnVersions.clear();
	// this.columnVersions.addAll(this.columnVersions);
	// }

	protected void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent) {
		this.parent = (OTUSet) parent;
		setColumnsSize(getColumnVersions().size());
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
	@CheckForNull
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
	@Nullable
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
	@Nullable
	public IOTUSet getParent() {
		return parent;
	}

	/**
	 * Get the row indexed by an OTU, or {@code null} if no such row has been
	 * inserted yet.
	 * <p>
	 * The return value won't be {@code null} for matrices straight out of the
	 * database.
	 * <p>
	 * {@code null} return values occur when {@link #setOTUSet(OTUSet)} contains
	 * OTUs newly introduced to this matrix.
	 * 
	 * @param otu the key
	 * 
	 * @return the row, or {@code null} of no such row has been inserted yet
	 * 
	 * @throws IllegalArgumentException if {@code otu} does not belong to this
	 *             matrix's {@code OTUSet}
	 */
	// @Nullable
	// public R getRow(final IOTU otu) {
	// checkNotNull(otu);
	// return getOTUKeyedRows().get(otu);
	// }

	/**
	 * Get the rows that make up this matrix.
	 * 
	 * @return the rows that make up this matrix
	 */
	public Map<IOTU, R> getRows() {
		return Collections
				.unmodifiableMap(getOTUKeyedRows().getValues());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Assumes {@code row} is not detached.
	 * 
	 * @throws IllegalArgumentException if {@code otu} does not belong to this
	 *             matrix's {@code OTUSet}
	 * @throws IllegalArgumentException if this matrix already contains a row
	 *             {@code .equals} to {@code row}
	 */
	@CheckForNull
	public R putRow(final IOTU otu, final R row) {
		checkNotNull(otu);
		checkNotNull(row);
		return getOTUKeyedRows().put(otu, row);
	}

	/** {@inheritDoc} */
	public void setColumnsSize(final int columnsSize) {
		checkArgument(columnsSize >= 0, "columnsSize < 0");

		// Add in column versions as necessary
		nullFill(getColumnVersionInfosModifiable(), columnsSize);

		// Remove column versions as necessary
		while (getColumnVersionInfos().size() > columnsSize) {
			getColumnVersionInfosModifiable()
					.remove(getColumnVersionInfos().size() - 1);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throw IllegalArgumentException if {@code pos >=
	 *        getColumnVersionInfos().size()}
	 */
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
			@CheckForNull final String description) {
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
			@CheckForNull final IOTUSet otuSet) {
		checkState(
				getOTUKeyedRows() != null,
				"getOTUKeyedRows() returned null - has the conrete class been constructed correctly, w/ its OTU->X dependency?");
		this.parent = otuSet;
		getOTUKeyedRows().setOTUs();
	}
}
