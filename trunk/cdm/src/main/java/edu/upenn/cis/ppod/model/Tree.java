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

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * A phylogenetic tree.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = Tree.TABLE)
public class Tree extends UuPPodEntity {

	public static final String TABLE = "TREE";

	public static final String ID_COLUMN = TABLE + "_ID";

	@CheckForNull
	private Long id;

	@CheckForNull
	private Integer version;

	@CheckForNull
	private String label;

	@CheckForNull
	private String newick;

	@CheckForNull
	private TreeSet parent;

	/**
	 * For Hibernate.
	 */
	public Tree() {}

	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@Nullable
	public Long getId() {
		return id;
	}

	/**
	 * Return the label. {@code null} when the tree is constructed, but will
	 * never be {@code null} once set.
	 * 
	 * @return the label
	 */
	@Column(name = "LABEL", nullable = false)
	@Nullable
	public String getLabel() {
		return label;
	}

	/**
	 * Get the newick string. {@code null} when the tree is constructed, but
	 * will never be {@code null} once set.
	 * 
	 * @return the newick string
	 */
	@Lob
	@Column(name = "NEWICK", nullable = false)
	@Nullable
	public String getNewick() {
		return newick;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = TreeSet.ID_COLUMN, insertable = false,
				updatable = false)
	@Nullable
	public TreeSet getParent() {
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
	 * Set the label of this tree.
	 * 
	 * @param label the label
	 */
	public void setLabel(final String label) {
		this.label = label;
	}

	/**
	 * Setter.
	 * 
	 * @param newick the Newick tree, composed of pPOD id's.
	 * 
	 * @return this {@code Tree}
	 */
	public void setNewick(final String newick) {
		this.newick = newick;
	}

	/** {@inheritDoc} */
	public void setParent(@CheckForNull final TreeSet parent) {
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
