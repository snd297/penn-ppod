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

import edu.upenn.cis.ppod.modelinterfaces.IVersionedWithOTUSet;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * An ordered, unique collection of {@link Tree}s.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = TreeSet.TABLE)
public class TreeSet extends UUPPodEntityWithXmlId implements
		IVersionedWithOTUSet {

	public static final String TABLE = "TREE_SET";

	public static final String JOIN_COLUMN = TABLE + "_ID";

	@Column(name = "LABEL", nullable = false)
	private String label;

	@CheckForNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = OTUSet.JOIN_COLUMN)
	private OTUSet otuSet;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = JOIN_COLUMN, nullable = false)
	private final List<Tree> trees = newArrayList();

	TreeSet() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visit(this);
		for (final Tree tree : getTreesModifiable()) {
			tree.accept(visitor);
		}
		super.accept(visitor);
	}

	/**
	 * Add {@code tree} to this {@code TreeSet}.
	 * <p>
	 * If this tree set already contains the tree, calling this method does
	 * nothing.
	 * 
	 * @param tree to be added
	 * 
	 * @return {@code tree}
	 */
	public Tree addTree(final Tree tree) {
		checkNotNull(tree);
		if (!trees.contains(tree)) {
			trees.add(tree);
			tree.setTreeSet(this);
			setInNeedOfNewVersion();
		}
		return tree;
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u See {@code Unmarshaller}
	 * @param parent {@code Unmarshaller}
	 */
	@Override
	public void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent) {
		checkNotNull(parent);
		super.afterUnmarshal(u, parent);
		this.otuSet = (OTUSet) parent;
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

	/**
	 * Get the OTU set.
	 * 
	 * @return the value
	 */
	@Nullable
	public OTUSet getOTUSet() {
		return otuSet;
	}

	public List<Tree> getTrees() {
		return Collections.unmodifiableList(trees);
	}

	@XmlElement(name = "tree")
	protected List<Tree> getTreesModifiable() {
		return trees;
	}

	@Override
	public TreeSet setInNeedOfNewVersion() {
		if (getOTUSet() != null) {
			getOTUSet().setInNeedOfNewVersion();
		}
		super.setInNeedOfNewVersion();
		return this;
	}

	/**
	 * Set the label.
	 * 
	 * @param label the label
	 * 
	 * @return this {@code TreeSet}
	 */
	public TreeSet setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(getLabel())) {

		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
		return this;
	}

	/**
	 * Setter.
	 * <p>
	 * Meant to be called from objects responsible for maintaining the
	 * {@code OTUSet<->TreeSet]}
	 * <p>
	 * Calling with {@code null} severs the relationship.
	 * 
	 * @param otuSet the {@code OTUSet}
	 * 
	 * @return this {@code TreeSet}
	 */
	protected TreeSet setOTUSet(@CheckForNull final OTUSet otuSet) {
		this.otuSet = otuSet;
		return this;
	}

	/**
	 * Scaffolding codes that does two things:
	 * <ol>
	 * <li>Removes <code>tree</code> from this <code>TreeSet</code>'s
	 * constituent <code>Tree</code>s.</li>
	 * <li>Removes this <code>TreeSet
	 * </code> from <code> tree</code>'s <code>TreeSet</code>s.</li>
	 * </ol>
	 * So it takes care of both sides of the <code>TreeSet</code><->
	 * <code>Tree</code> relationship.
	 * 
	 * @param newTrees the new trees to be set
	 * 
	 * @return the trees that were removed as a result of this operation, in
	 *         their original order
	 */
	public List<Tree> setTrees(final List<Tree> newTrees) {
		checkNotNull(newTrees);
		if (newTrees.equals(getTreesModifiable())) {
			return Collections.emptyList();
		}

		final List<Tree> removedTrees = newArrayList(getTrees());
		removedTrees.removeAll(newTrees);

		for (final Tree removedTree : removedTrees) {
			removedTree.setTreeSet(null);
		}

		getTreesModifiable().clear();
		for (final Tree newTree : newTrees) {
			addTree(newTree);
		}
		setInNeedOfNewVersion();
		return removedTrees;
	}

	/**
	 * Constructs a <code>String</code> with attributes in name=value format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	@Override
	public String toString() {
		final String TAB = " ";

		final StringBuilder retValue = new StringBuilder();

		retValue.append("TreeSet(")
				.append("label=")
				.append(this.label)
				.append(TAB)
				.append("trees=")
				.append(this.trees)
				.append(TAB).append(")");

		return retValue.toString();
	}

}
