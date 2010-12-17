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
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * Operational Taxonomic Unit.
 * 
 * @author Shirley Cohen
 * @author Sam Donnelly
 */
@Entity
@Table(name = OTU.TABLE)
public class OTU
		extends UUPPodEntityWithDocId
		implements IOTU {

	public static class Adapter extends XmlAdapter<OTU, IOTU> {

		@Override
		public OTU marshal(final IOTU otu) {
			return (OTU) otu;
		}

		@Override
		public IOTU unmarshal(final OTU otu) {
			return otu;
		}
	}

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
	@Nullable
	private String label;

	/**
	 * The {@code OTUSet} that this {@code OTU} belongs to.
	 */
	@Nullable
	@ManyToOne(targetEntity = OTUSet.class)
	@JoinColumn(name = OTUSet.JOIN_COLUMN, insertable = false,
			updatable = false, nullable = false)
	private IOTUSet parent;

	public OTU() {}

	public OTU(final String label) {
		this.label = label;
	}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitOTU(this);
	}

	/**
	 * See {@link Unmarshaller}.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	public void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent) {
		this.parent = (IOTUSet) parent;
	}

	/**
	 * Return the label of this {@code OTU}.
	 * 
	 * @return the label of this {@code OTU}
	 */
	@XmlAttribute
	@Nullable
	public String getLabel() {
		return label;
	}

	/**
	 * Get the owning {@code OTUSet}. Will be {@code null} for parentless
	 * {@code OTU}s. Will never be {@code null} for {@code OTU}s just pulled out
	 * of the database since persisted {@code OTU}s must have parents.
	 * 
	 * @return the {@code OTUSet} that owns this {@code OTU}
	 */
	@Nullable
	public IOTUSet getParent() {
		return parent;
	}

	/**
	 * Mark this {@code OTU} and its parent {@code IOTUSet}, if it has one, as
	 * in need of a new pPod version info.
	 * 
	 * @return this {@code OTU}
	 */
	@Override
	public void setInNeedOfNewVersion() {
		if (getParent() != null) {
			getParent().setInNeedOfNewVersion();
		}
		super.setInNeedOfNewVersion();
	}

	/** {@inheritDoc} */
	public OTU setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(getLabel())) {

		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
		return this;
	}

	/** {@inheritDoc} */
	public void setParent(@CheckForNull final IOTUSet parent) {
		this.parent = parent;
	}

	/**
	 * Constructs a <code>String</code> with attributes in name = value format.
	 * 
	 * @return a <code>String</code> representation of this object
	 */
	@Override
	public String toString() {
		final StringBuilder retValue = new StringBuilder();

		retValue.append("OTU(").append("label=").append(this.label).append(")");

		return retValue.toString();
	}

}
