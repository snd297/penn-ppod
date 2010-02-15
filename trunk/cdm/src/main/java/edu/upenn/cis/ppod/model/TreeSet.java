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
import static edu.upenn.cis.ppod.util.UPennCisPPodUtil.nullSafeEquals;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.annotations.Cascade;

import com.sun.source.util.Trees;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A tree set.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = TreeSet.TABLE)
public class TreeSet extends UUPPodEntityWXmlId {

	static final String TABLE = "TREE_SET";

	static final String ID_COLUMN = TABLE + "_ID";

	@Column(name = "LABEL", nullable = false)
	@org.hibernate.annotations.Index(name = "IDX_LABEL")
	private String label;

	/**
	 * The {@link OTU}s that this {@code Tree} contains.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = OTUSet.ID_COLUMN, nullable = false)
	private OTUSet otuSet;

	/** The set of {@code Tree}s this {@code TreeSet} contains. */
	@ManyToMany
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@JoinTable(name = TABLE + "_" + Tree.TABLE, joinColumns = { @JoinColumn(name = TreeSet.ID_COLUMN) }, inverseJoinColumns = { @JoinColumn(name = PersistentObject.ID_COLUMN) })
	@org.hibernate.annotations.IndexColumn(name = "TREE_POSITION")
	private final List<Tree> trees = newArrayList();

	@XmlElement(name = "tree")
	private List<Tree> getTreesMutable() {
		return trees;
	}

	TreeSet() {}

	@Override
	public TreeSet accept(final IVisitor visitor) {
		visitor.visit(this);
		return this;
	}

	public TreeSet setTrees(final List<Tree> newTrees) {
		checkNotNull(newTrees);
		if (newTrees.equals(getTrees())) {
			return this;
		}
		for (final Tree tree : getTrees()) {
			if (!newTrees.contains(tree)) {
				tree.removeTreeSet(this);
			}
		}
		getTreesMutable().clear();
		getTreesMutable().addAll(newTrees);
		for (final Tree tree : getTrees()) {
			tree.addTreeSet(this);
		}
		resetPPodVersionInfo();
		return this;
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u See {@code Unmarshaller}
	 * @param parent {@code Unmarshaller}
	 */
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		this.otuSet = (OTUSet) parent;
	}

	/**
	 * Get the label.
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

	/**
	 * Get an unmodifiable view of the <code>Tree</code>s that comprise this
	 * <code>TreeSet</code>.
	 * 
	 * @return an unmodifiable view of the <code>Tree</code>s that comprise this
	 *         <code>TreeSet</code>
	 */
	public List<Tree> getTrees() {
		return Collections.unmodifiableList(trees);
	}

	/**
	 * Scaffolding codes that does two things:
	 * <ol>
	 * <li>Removes <code>tree</code> from this <code>TreeSet</code>'s
	 * constituent <code>Tree</code>s.</li>
	 * <li>Removes this <code>TreeSet
	 * </code> from <code> tree</code>'s <code>TreeSet</code>s.
	 * </li>
	 * </ol>
	 * So it takes care of both sides of the <code>TreeSet</code><->
	 * <code>Tree</code> relationship.
	 * 
	 * @param tree see description.
	 * 
	 * @return {@code true} if the tree was removed, {@code false} if it wasn't
	 *         present to begin with
	 */
// public boolean removeTree(final Tree tree) {
// final boolean treeWasRemoved = trees.remove(tree);
// if (treeWasRemoved) {
// tree.removeTreeSet(this);
// resetPPodVersionInfo();
// }
// return treeWasRemoved;
// }

	@Override
	public TreeSet resetPPodVersionInfo() {
		if (getAllowResetPPodVersionInfo()) {
			if (isInNeedOfNewPPodVersionInfo()) {

			} else {
				if (otuSet != null) {
					otuSet.resetPPodVersionInfo();
				}
				super.resetPPodVersionInfo();
			}
		}
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
			resetPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Setter. Assumes that {@code otuSet} is not detached.
	 * <p>
	 * Intention package-private and meant to be called from {@code TreeSet}.
	 * 
	 * @param otuSet the {@code OTUSet}
	 * 
	 * @return this {@code TreeSet}
	 */
	TreeSet setOTUSet(@Nullable final OTUSet otuSet) {
		if (nullSafeEquals(this.otuSet, otuSet)) {

		} else {
			this.otuSet = otuSet;
			resetPPodVersionInfo();
		}
		return this;
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

		retValue.append("TreeSet(").append(super.toString()).append(TAB)
				.append("id=").append(TAB).append(TAB).append("label=").append(
						this.label).append(TAB).append("trees=").append(
						this.trees).append(TAB).append(")");

		return retValue.toString();
	}

}
