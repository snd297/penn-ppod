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

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.imodel.IChild;
import edu.upenn.cis.ppod.imodel.IDependsOnParentOtus;

/**
 * A matrix is a set of OTU-keyed rows with column header pPOD versions which
 * are each equal to the largest cell version in the column.
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class Matrix<R extends IChild<?>>
		extends UuPPodEntity2
		implements IDependsOnParentOtus {

	/** Description column. */
	public static final String DESCRIPTION_COLUMN = "DESCRIPTION";

	/** Label column. */
	public static final String LABEL_COLUMN = "LABEL";

	/** Free-form description. */
	@CheckForNull
	private String description;

	/** The label for this {@code Matrix}. */
	@CheckForNull
	private String label;

	/**
	 * These are the <code>OTU</code>s whose data comprises this {@code Matrix}.
	 */
	@CheckForNull
	private OtuSet parent;

	/** Default constructor. */
	Matrix() {}

	/**
	 * Getter.
	 * 
	 * @return the description
	 */
	@Column(name = DESCRIPTION_COLUMN, nullable = true)
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
	@Column(name = LABEL_COLUMN, nullable = false)
	@Nullable
	public String getLabel() {
		return label;
	}

	/** {$inheritDoc} */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = OtuSet.ID_COLUMN, insertable = false,
				updatable = false)
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
	@Transient
	public abstract Map<Otu, R> getRows();

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
	 * @throws IllegalArgumentException if {@code otu} does not belong to this
	 *             matrix's {@code OTUSet}
	 * @throws IllegalArgumentException if this matrix already contains a row
	 *             {@code .equals} to {@code row}
	 */
	@Nullable
	public abstract void putRow(final Otu otu, final R row);

	/**
	 * Setter.
	 * 
	 * @param description the description value
	 */
	public void setDescription(
			@CheckForNull final String description) {
		this.description = description;
	}

	/**
	 * Set the label of this matrix.
	 * 
	 * @param label the value for the label
	 */
	public void setLabel(final String label) {
		this.label = label;
	}

	/** {@inheritDoc} */
	public void setParent(
			@CheckForNull final OtuSet parent) {
		this.parent = parent;

	}

	/** {@inheritDoc} */
	public abstract void updateOtus();
}
