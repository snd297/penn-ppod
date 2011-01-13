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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.imodel.IChild;
import edu.upenn.cis.ppod.imodel.ILabeled;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * Operational Taxonomic Unit.
 * 
 * @author Shirley Cohen
 * @author Sam Donnelly
 */
@Entity
@Table(name = Otu.TABLE)
public class Otu
		extends UuPPodEntity
		implements IChild<OtuSet>, ILabeled {

	/** The table for this entity. */
	public static final String TABLE = "OTU";

	/**
	 * To be used for the names of foreign keys that point at this table.
	 */
	public static final String JOIN_COLUMN = TABLE + "_ID";

	/**
	 * The column where we store {@code #label}. Intentionally package-private.
	 */
	static final String LABEL_COLUMN = "LABEL";

	/** Non-unique label. */
	@Column(name = LABEL_COLUMN, nullable = false)
	@CheckForNull
	private String label;

	/**
	 * The {@code OTUSet} that this {@code OTU} belongs to.
	 */
	@CheckForNull
	@ManyToOne
	@JoinColumn(name = OtuSet.JOIN_COLUMN, insertable = false,
			updatable = false, nullable = false)
	private OtuSet parent;

	public Otu() {}

	public Otu(final String label) {
		this.label = label;
	}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitOTU(this);
	}

	/**
	 * Return the label of this {@code OTU}.
	 * 
	 * @return the label of this {@code OTU}
	 */
	@Nullable
	public String getLabel() {
		return label;
	}

	/**
	 * Get the owning {@code IOtuSet}. Will be {@code null} for parentless
	 * {@code Otu}s. Will never be {@code null} for {@code OTU}s just pulled out
	 * of the database since persisted {@code Otu}s must have parents.
	 * 
	 * @return the {@code OTUSet} that owns this {@code OTU}
	 */
	@Nullable
	public OtuSet getParent() {
		return parent;
	}

	/**
	 * Mark this {@code OTU} and its parent {@code IOTUSet}, if it has one, as
	 * in need of a new pPod version info.
	 */
	@Override
	public void setInNeedOfNewVersion() {
		if (getParent() != null) {
			getParent().setInNeedOfNewVersion();
		}
		super.setInNeedOfNewVersion();
	}

	/**
	 * Set the label.
	 * 
	 * @param label the label
	 * 
	 * @return this
	 */
	public Otu setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(getLabel())) {

		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
		return this;
	}

	/**
	 * Set the parent OTU set.
	 * 
	 * @param parent the parent OTU set
	 */
	public void setParent(@CheckForNull final OtuSet parent) {
		this.parent = parent;
	}
}
