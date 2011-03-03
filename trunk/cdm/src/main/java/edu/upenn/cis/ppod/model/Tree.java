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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A phylogenetic tree.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = Tree.TABLE)
public class Tree extends UuPPodEntity {

	public static final String TABLE = "TREE";

	@Column(name = "LABEL", nullable = false)
	@CheckForNull
	private String label;

	@Lob
	@Column(name = "NEWICK", nullable = false)
	@CheckForNull
	private String newick;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = TreeSet.JOIN_COLUMN, insertable = false,
				updatable = false)
	@CheckForNull
	private TreeSet parent;

	/**
	 * For Hibernate.
	 */
	public Tree() {}

	/**
	 * Return the label. {@code null} when the tree is constructed, but will
	 * never be {@code null} once set.
	 * 
	 * @return the label
	 */
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
	@Nullable
	public String getNewick() {
		return newick;
	}

	@Nullable
	public TreeSet getParent() {
		return parent;
	}

	/**
	 * Set the label of this tree.
	 * 
	 * @param label the label
	 */
	public void setLabel(final String label) {
		this.label = checkNotNull(label);
	}

	/**
	 * Setter.
	 * 
	 * @param newick the Newick tree, composed of pPOD id's.
	 * 
	 * @return this {@code Tree}
	 */
	public void setNewick(final String newick) {
		this.newick = checkNotNull(newick);
	}

	/** {@inheritDoc} */
	public void setParent(@CheckForNull final TreeSet parent) {
		this.parent = parent;
	}
}
