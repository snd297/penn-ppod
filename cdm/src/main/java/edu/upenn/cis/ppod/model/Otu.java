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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.dto.ILabeled;
import edu.upenn.cis.ppod.imodel.IChild;

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
	public static final String ID_COLUMN = TABLE + "_ID";

	@CheckForNull
	private Long id;

	@CheckForNull
	private Integer version;

	/** Globally non-unique label, must be unique within an OTU set. */
	@CheckForNull
	private String label;

	/**
	 * The {@code OTUSet} that this {@code OTU} belongs to.
	 */
	@CheckForNull
	private OtuSet parent;

	public Otu() {}

	public Otu(final String label) {
		this.label = label;
	}

	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@Nullable
	public Long getId() {
		return id;
	}

	/**
	 * Return the label of this {@code OTU}.
	 * 
	 * @return the label of this {@code OTU}
	 */
	@Column(name = "LABEL", nullable = false)
	@Index(name = "IDX_LABEL")
	@Nullable
	public String getLabel() {
		return label;
	}

	/**
	 * Get the owning {@code IOtuSet}. Will be {@code null} for parentless
	 * {@code Otu}s. Will never be {@code null} for {@code OTU}s just pulled out
	 * of the database since persisted {@code Otu}s must have parents.
	 * 
	 * @return the OTU set that owns this OTU
	 */
	@ManyToOne
	@JoinColumn(name = OtuSet.ID_COLUMN, insertable = false,
			updatable = false, nullable = false)
	@Nullable
	public OtuSet getParent() {
		return parent;
	}

	/**
	 * @return the version
	 */
	@Version
	@Column(name = "OBJ_VERSION")
	@Nullable
	public Integer getVersion() {
		return version;
	}

	@SuppressWarnings("unused")
	private void setId(final Long id) {
		this.id = id;
	}

	/**
	 * Set the label.
	 * 
	 * @param label the label
	 * 
	 * @return this
	 */
	public void setLabel(final String label) {
		this.label = label;
	}

	/**
	 * Set the parent OTU set.
	 * 
	 * @param parent the parent OTU set
	 */
	public void setParent(@CheckForNull final OtuSet parent) {
		this.parent = parent;
	}

	/**
	 * @param version the version to set
	 */
	@SuppressWarnings("unused")
	private void setVersion(final Integer version) {
		this.version = version;
	}
}
