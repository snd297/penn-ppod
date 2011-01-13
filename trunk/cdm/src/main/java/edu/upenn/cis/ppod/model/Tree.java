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

import edu.upenn.cis.ppod.util.IVisitor;

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
	@Nullable
	private String label;

	@Lob
	@Column(name = "NEWICK", nullable = false)
	@Nullable
	private String newick;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = TreeSet.JOIN_COLUMN, insertable = false,
				updatable = false)
	@Nullable
	private TreeSet parent;

	/**
	 * For Hibernate.
	 */
	public Tree() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitTree(this);
		super.accept(visitor);
	}

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

	@Override
	public void setInNeedOfNewVersion() {
		if (parent != null) {
			parent.setInNeedOfNewVersion();
		}
		super.setInNeedOfNewVersion();
	}

	/**
	 * Set the label of this tree.
	 * 
	 * @param label the label
	 */
	public void setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(this.label)) {
			// nothing to do
		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
	}

	/**
	 * Setter.
	 * 
	 * @param newick the Newick tree, composed of pPOD id's.
	 * 
	 * @return this {@code Tree}
	 */
	public void setNewick(final String newick) {
		checkNotNull(newick);
		if (newick.equals(getNewick())) {

		} else {
			this.newick = newick;
			setInNeedOfNewVersion();
		}
	}

	/** {@inheritDoc} */
	public void setParent(@CheckForNull final TreeSet parent) {
		this.parent = parent;
	}
}
