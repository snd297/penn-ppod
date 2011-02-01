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
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.imodel.IDependsOnParentOtus;
import edu.upenn.cis.ppod.imodel.IOtuKeyedMap;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A matrix is a set of OTU-keyed rows with column header pPOD versions which
 * are each equal to the largest cell version in the column.
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
abstract class Matrix<R extends Row<C, ?>, C extends Cell<?, ?>>
		extends UuPPodEntity
		implements IDependsOnParentOtus {

	/** Description column. */
	public static final String DESCRIPTION_COLUMN = "DESCRIPTION";

	/** Label column. */
	public static final String LABEL_COLUMN = "LABEL";

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
	@JoinColumn(name = OtuSet.JOIN_COLUMN, insertable = false,
				updatable = false)
	@CheckForNull
	private OtuSet parent;

	/** Default constructor. */
	Matrix() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		getOtuKeyedRows().accept(visitor);
		super.accept(visitor);
	}

	/** {@inheritDoc} */
	protected void addColumn(
			final int columnNo,
			final List<? extends C> column) {
		checkArgument(columnNo >= 0, "columnNo < 0");

		int rowPos = -1;
		for (final R row : getRows().values()) {
			rowPos++;
			final List<C> cells = newArrayList(row.getCells());
			cells.add(columnNo, column.get(rowPos));
			row.setCells(cells);
		}
	}

	/**
	 * Getter.
	 * 
	 * @return the description
	 */
	@Nullable
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
	abstract IOtuKeyedMap<R> getOtuKeyedRows();

	/**
	 * Getter. Will be {@code null} when object is first created or matrices
	 * that have been severed from their parent, but never {@code null} for
	 * objects freshly pulled out of the database.
	 * 
	 * @return this matrix's {@code OTUSet}
	 */
	public OtuSet getParent() {
		return parent;
	}

	/**
	 * Get the rows that make up this matrix.
	 * <p>
	 * Rows will only be {@code null} for OTUs newly introduced to this matrix
	 * by {@link #setOTUs}.
	 * 
	 * @return the rows that make up this matrix
	 */
	public Map<Otu, R> getRows() {
		return Collections
				.unmodifiableMap(getOtuKeyedRows().getValues());
	}

	/**
	 * Set row at <code>otu</code> to <code>row</code>.
	 * <p>
	 * Assumes {@code row} does not belong to another matrix.
	 * <p>
	 * {@code otu} must be a member of {@link #getParent()}.
	 * <p>
	 * Assumes {@code row} is not detached.
	 * 
	 * @param otu index of the row we are adding
	 * @param row the row we're adding
	 * 
	 * @return the row that was previously there, or {@code null} if there was
	 *         no row previously there
	 * 
	 * @throws IllegalArgumentException if {@code otu} does not belong to this
	 *             matrix's {@code OTUSet}
	 * @throws IllegalArgumentException if this matrix already contains a row
	 *             {@code .equals} to {@code row}
	 */
	@CheckForNull
	public R putRow(final Otu otu, final R row) {
		checkNotNull(otu);
		checkNotNull(row);
		return getOtuKeyedRows().put(otu, row);
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

		final List<C> removedColumn = newArrayList();
		for (final R row : getRows().values()) {
			final List<C> cells = newArrayList(row.getCells());
			final C removedCell = cells.remove(columnNo);
			row.setCells(cells);
			removedColumn.add(removedCell);
		}
		return removedColumn;
	}


	/**
	 * Setter.
	 * 
	 * @param description the description value
	 */
	public void setDescription(
			@CheckForNull final String description) {
		if (equal(description, getDescription())) {
			// nothing to do
		} else {
			this.description = description;
			setInNeedOfNewVersion();
		}
	}

	@Override
	public void setInNeedOfNewVersion() {
		if (getParent() != null) {
			getParent().setInNeedOfNewVersion();
		}
		super.setInNeedOfNewVersion();
	}

	/**
	 * Set the label of this matrix.
	 * 
	 * @param label the value for the label
	 */
	public void setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(this.label)) {
			// they're the same, nothing to do
		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
	}

	/** {@inheritDoc} */
	public void setParent(
			@CheckForNull final OtuSet otuSet) {
		this.parent = otuSet;
		updateOtus();
	}

	/** {@inheritDoc} */
	public void updateOtus() {
		getOtuKeyedRows().updateOtus();
	}
}
