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
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.hibernate.annotations.Target;

import com.google.common.collect.Iterators;

import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.ITree;
import edu.upenn.cis.ppod.imodel.ITreeSet;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * An ordered, unique collection of {@link Tree}s.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = TreeSet.TABLE)
public class TreeSet
		extends UUPPodEntityWithDocId
		implements ITreeSet {

	public static class Adapter extends XmlAdapter<TreeSet, ITreeSet> {

		@Override
		public TreeSet marshal(final ITreeSet treeSet) {
			return (TreeSet) treeSet;
		}

		@Override
		public ITreeSet unmarshal(final TreeSet treeSet) {
			return treeSet;
		}
	}

	public static final String TABLE = "TREE_SET";

	public static final String JOIN_COLUMN = TABLE + "_ID";

	@Column(name = "LABEL", nullable = false)
	private String label;

	@CheckForNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = OTUSet.JOIN_COLUMN)
	@Target(OTUSet.class)
	private IOTUSet parent;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,
			targetEntity = Tree.class)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = JOIN_COLUMN, nullable = false)
	private final List<ITree> trees = newArrayList();

	@Column(name = "POSITION", nullable = false)
	@Nullable
	private Integer position;

	TreeSet() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitTreeSet(this);
		for (final ITree tree : getTreesModifiable()) {
			tree.accept(visitor);
		}
		super.accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTree(final ITree tree) {
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
		this.parent = (IOTUSet) parent;
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
	public IOTUSet getParent() {
		return parent;
	}

	/** {@inheritDoc} */
	public Integer getPosition() {
		return position;
	}

	/** {@inheritDoc} */
	public List<ITree> getTrees() {
		return Collections.unmodifiableList(trees);
	}

	@XmlElement(name = "tree")
	protected List<ITree> getTreesModifiable() {
		return trees;
	}

	@Override
	public void setInNeedOfNewVersion() {
		if (getParent() != null) {
			getParent().setInNeedOfNewVersion();
		}
		super.setInNeedOfNewVersion();
	}

	/** {@inheritDoc} */
	public void setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(getLabel())) {

		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
	}

	/** {@inheritDoc} */
	public void setParent(@CheckForNull final IOTUSet parent) {
		this.parent = parent;
		updateOTUs();
	}

	/** {@inheritDoc} */
	public void setPosition(final Integer position) {
		this.position = position;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalArgumentException if {@code trees} contains any
	 *             {@code .equals(...)} duplicates
	 */
	public List<ITree> setTrees(final List<? extends ITree> trees) {
		checkNotNull(trees);
		if (trees.equals(getTreesModifiable())) {
			return Collections.emptyList();
		}

		int treePos = -1;
		for (final ITree tree : trees) {
			treePos++;
			checkArgument(
					!Iterators.contains(trees.listIterator(treePos + 1), tree),
					"argument trees contains duplicates");
		}

		final List<ITree> removedTrees = newArrayList(getTrees());
		removedTrees.removeAll(trees);

		for (final ITree removedTree : removedTrees) {
			removedTree.setParent(null);
		}

		getTreesModifiable().clear();
		for (final ITree newTree : trees) {
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

	/**
	 * There's nothing for a tree set to do since the OTU's are stored as pPOD
	 * IDs in the newick strings which can't be modified.
	 */
	public void updateOTUs() {

	}

}
