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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Version;

import com.google.common.collect.Iterators;

import edu.upenn.cis.ppod.imodel.IDependsOnParentOtus;

/**
 * An ordered, unique collection of {@link Tree}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = TreeSet.TABLE)
public class TreeSet extends UuPPodEntity implements IDependsOnParentOtus {

	@CheckForNull
	private Long id;

	@CheckForNull
	private Integer version;

	public static final String TABLE = "tree_set";

	public static final String ID_COLUMN = TABLE + "_id";

	@Nullable
	private String label;

	@Nullable
	private OtuSet parent;

	private List<Tree> trees = newArrayList();

	public TreeSet() {}

	/**
	 * Add {@code tree} to this {@code ITreeSet}.
	 * 
	 * @param tree to be added
	 * 
	 * @throws IllegalArgumentException if the tree is already contained in this
	 *             tree set
	 */
	public void addTree(final Tree tree) {
		checkNotNull(tree);
		checkArgument(!trees.contains(tree),
				"tree set already contains a tree [" + tree.getLabel() + "]");
		trees.add(tree);
		tree.setParent(this);
	}

	/**
	 * Set the trees in this tree set.
	 * <p>
	 * This handles both sides of the {@code ITreeSet<->ITree} relationship.
	 * 
	 * @param trees the trees we're setting
	 * 
	 * @throws IllegalArgumentException if {@code trees} contains any .equals
	 *             dups
	 */
	public void clearAndAddTrees(final List<? extends Tree> trees) {
		checkNotNull(trees);
		int treePos = -1;
		for (final Tree tree : trees) {
			treePos++;
			checkArgument(
						!Iterators.contains(trees.listIterator(treePos + 1),
								tree),
						"argument trees contains duplicates");
		}

		final List<Tree> removedTrees = newArrayList(this.trees);
		removedTrees.removeAll(trees);

		for (final Tree removedTree : removedTrees) {
			removedTree.setParent(null);
		}

		this.trees.clear();
		for (final Tree newTree : trees) {
			addTree(newTree);
		}
	}

	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@Nullable
	public Long getId() {
		return id;
	}

	/**
	 * Get the label. Will be {@code null} when the object is first constructed.
	 * Will never be {@code null} for objects in a persistent state.
	 * 
	 * @return the label
	 */
	@Column(name = "label", nullable = false)
	@Nullable
	public String getLabel() {
		return label;
	}

	/** {@inheritDoc} */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = OtuSet.ID_COLUMN, insertable = false,
				updatable = false)
	@Nullable
	public OtuSet getParent() {
		return parent;
	}

	/**
	 * Get the constituent trees.
	 * 
	 * @return the constituent trees.
	 */
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderColumn(name = "position")
	@JoinColumn(name = ID_COLUMN, nullable = false)
	public List<Tree> getTrees() {
		return trees;
	}

	/**
	 * @return the version
	 */
	@Version
	@Column(name = "obj_version")
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
	 * @param label the label value
	 */
	public void setLabel(final String label) {
		this.label = checkNotNull(label);
	}

	/** {@inheritDoc} */
	public void setParent(@CheckForNull final OtuSet parent) {
		this.parent = parent;
		updateOtus();
	}

	@SuppressWarnings("unused")
	private void setTrees(final List<Tree> trees) {
		this.trees = trees;
	}

	/**
	 * @param version the version to set
	 */
	@SuppressWarnings("unused")
	private void setVersion(final Integer version) {
		this.version = version;
	}

	/**
	 * There's nothing for a tree set to do since the OTU's are stored as pPOD
	 * IDs in the newick strings which can't be modified.
	 */
	public void updateOtus() {

	}

}
