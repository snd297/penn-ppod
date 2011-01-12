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

import java.util.Collections;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.Iterators;

import edu.upenn.cis.ppod.imodel.IDependsOnParentOtus;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * An ordered, unique collection of {@link Tree}s.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = TreeSet.TABLE)
public class TreeSet extends UuPPodEntity implements IDependsOnParentOtus {

	public static final String TABLE = "TREE_SET";

	public static final String JOIN_COLUMN = TABLE + "_ID";

	@Nullable
	@Column(name = "LABEL", nullable = false)
	private String label;

	@Nullable
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = OtuSet.JOIN_COLUMN, insertable = false,
				updatable = false)
	private OtuSet parent;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = JOIN_COLUMN, nullable = false)
	private final List<Tree> trees = newArrayList();

	public TreeSet() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitTreeSet(this);
		for (final Tree tree : getTreesModifiable()) {
			tree.accept(visitor);
		}
		super.accept(visitor);
	}

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
		setInNeedOfNewVersion();
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u See {@code Unmarshaller}
	 * @param parent {@code Unmarshaller}
	 */
	protected void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent) {
		this.parent = (OtuSet) parent;
	}

	/**
	 * Get the label. Will be {@code null} when the object is first constructed.
	 * Will never be {@code null} for objects in a persistent state.
	 * 
	 * @return the label
	 */
	@XmlAttribute
	@Nullable
	public String getLabel() {
		return label;
	}

	/** {@inheritDoc} */
	@Nullable
	public OtuSet getParent() {
		return parent;
	}

	/**
	 * Get the constituent trees.
	 * 
	 * @return the constituent trees.
	 */
	public List<Tree> getTrees() {
		return Collections.unmodifiableList(trees);
	}

	@XmlElement(name = "tree")
	protected List<Tree> getTreesModifiable() {
		return trees;
	}

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
	 * @param label the label value
	 */
	public void setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(getLabel())) {

		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
	}

	/** {@inheritDoc} */
	public void setParent(@CheckForNull final OtuSet parent) {
		this.parent = parent;
		updateOtus();
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
	public void setTrees(final List<? extends Tree> trees) {
		checkNotNull(trees);
		if (trees.equals(this.trees)) {

		} else {

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
			setInNeedOfNewVersion();
		}
		return;
	}

	/**
	 * There's nothing for a tree set to do since the OTU's are stored as pPOD
	 * IDs in the newick strings which can't be modified.
	 */
	public void updateOtus() {

	}

}
