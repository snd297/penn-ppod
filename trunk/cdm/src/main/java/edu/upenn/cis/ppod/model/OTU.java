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

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;

import com.google.common.base.Function;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * Operational Taxonomic Unit.
 * 
 * @author Shirley Cohen
 * @author Sam Donnelly
 */
@Entity
@Table(name = OTU.TABLE)
public class OTU extends UUPPodEntityWXmlId {

	/**
	 * {@link Function} wrapper of {@link #getLabel()}.
	 */
	public static final Function<OTU, String> getLabel = new Function<OTU, String>() {
		public String apply(final OTU arg0) {
			return arg0.getLabel();
		}
	};

	/** The table for this entity. Intentionally package-private. */
	static final String TABLE = "OTU";

	/**
	 * The column where a {@code OTU}'s {@link javax.persistence.Id} gets
	 * stored. Intentionally package-private.
	 */
	static final String ID_COLUMN = TABLE + "_ID";

	/**
	 * The column where we store {@code #label}. Intentionally package-private.
	 */
	static final String LABEL_COLUMN = "LABEL";

	/** Non-unique label. */
	@Column(name = LABEL_COLUMN, nullable = false)
	@CheckForNull
	private String label;

	/**
	 * These are the {@code OTUSet}s that this {@code OTU} belongs to.
	 */
	@ManyToOne
	@JoinColumn(name = OTUSet.ID_COLUMN, insertable = false, updatable = false, nullable = false)
	@CheckForNull
	private OTUSet otuSet;

	OTU() {}

	@Override
	public void accept(final IVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * See {@link Unmarshaller}.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@Override
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		super.afterUnmarshal(u, parent);
		if (parent instanceof OTUSet) {
			this.otuSet = (OTUSet) parent;
		}
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
	 * Get the owning {@code OTUSet}.
	 * 
	 * @return the {@code OTUSet} that owns this {@code OTU}
	 */
	@Nullable
	public OTUSet getOTUSet() {
		return otuSet;
	}

	/**
	 * Reset this OTU's pPOD version info to {@code null}, and call
	 * {@link OTUSet#resetPPodVersionInfo()} on all of the OTU sets that contain
	 * this OTU.
	 * 
	 * @return this {@code OTU}
	 */
	@Override
	public OTU setInNeedOfNewPPodVersionInfo() {
		if (getOTUSet() != null) {
			getOTUSet().setInNeedOfNewPPodVersionInfo();
		}
		super.setInNeedOfNewPPodVersionInfo();
		return this;
	}

	/**
	 * Set this OTU's label.
	 * 
	 * @param label the label
	 * 
	 * @return this OTU
	 */
	public OTU setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(getLabel())) {

		} else {
			this.label = label;
			setInNeedOfNewPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Add <code>otuSet</code> to this <code>OTU</code>'s associated
	 * <code>OTUSet</code>s.
	 * <p>
	 * Intended to be package protected and used in conjunction with
	 * {@link OTUSet#addOTU(OTU)}.
	 * 
	 * @param otuSet to be added to this <code>OTU</code>.
	 */
	OTU setOTUSet(@Nullable final OTUSet otuSet) {
		this.otuSet = otuSet;
		return this;
	}

	/**
	 * Constructs a <code>String</code> with all attributes in name = value
	 * format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	@Override
	public String toString() {
		final StringBuilder retValue = new StringBuilder();

		retValue.append("OTU(").append("label=").append(this.label).append(")");

		return retValue.toString();
	}
}
