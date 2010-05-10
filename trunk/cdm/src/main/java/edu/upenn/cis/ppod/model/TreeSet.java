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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.annotations.Cascade;

import edu.upenn.cis.ppod.modelinterfaces.IPPodVersionedWithOTUSet;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * An ordered collection of {@link Tree}s.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = TreeSet.TABLE)
public class TreeSet extends UUPPodEntityWXmlId implements
		IPPodVersionedWithOTUSet, Iterable<Tree> {

	static final String TABLE = "TREE_SET";

	static final String ID_COLUMN = TABLE + "_ID";

	@Column(name = "LABEL", nullable = false)
	private String label;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = OTUSet.JOIN_COLUMN, nullable = false)
	private OTUSet otuSet;

	@OneToMany(orphanRemoval = true)
	@org.hibernate.annotations.IndexColumn(name = "POSITION")
	@JoinColumn(name = ID_COLUMN, nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	private final List<Tree> trees = newArrayList();

	TreeSet() {}

	@Override
	public void accept(final IVisitor visitor) {
		for (final Tree tree : getTrees()) {
			tree.accept(visitor);
		}
		super.accept(visitor);
		visitor.visit(this);
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u See {@code Unmarshaller}
	 * @param parent {@code Unmarshaller}
	 */
	@Override
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		super.afterUnmarshal(u, parent);
		this.otuSet = (OTUSet) parent;
	}

	/**
	 * Get the label. Will be {@code null} when the object is first contructed.
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

	@XmlElement(name = "tree")
	private List<Tree> getTrees() {
		return trees;
	}

	public Iterator<Tree> iterator() {
		return Collections.unmodifiableList(getTrees()).iterator();
	}

	@Override
	public TreeSet setInNeedOfNewPPodVersionInfo() {
		if (isInNeedOfNewPPodVersionInfo()) {
			return this;
		}
		if (getOTUSet() != null) {
			getOTUSet().setInNeedOfNewPPodVersionInfo();
		}
		super.setInNeedOfNewPPodVersionInfo();

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
			setInNeedOfNewPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Setter.
	 * <p>
	 * Intentionally package-private and meant to be called from {@code TreeSet}.
	 * 
	 * @param otuSet the {@code OTUSet}
	 * 
	 * @return this {@code TreeSet}
	 */
	public TreeSet setOTUSet(@CheckForNull final OTUSet otuSet) {
		if (equal(otuSet, getOTUSet())) {

		} else {
			this.otuSet = otuSet;
			setInNeedOfNewPPodVersionInfo();
		}
		return this;
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
	 * @param newTrees the new trees to be set
	 * 
	 * @return the trees that were removed as a result of this operation
	 */
	public List<Tree> setTrees(final List<Tree> newTrees) {
		checkNotNull(newTrees);
		if (newTrees.equals(getTrees())) {
			return Collections.emptyList();
		}
		for (final Tree tree : getTrees()) {
			if (!newTrees.contains(tree)) {
				tree.setTreeSet(null);
			}
		}
		final List<Tree> removedTrees = newArrayList(getTrees());
		removedTrees.removeAll(newTrees);

		getTrees().clear();
		for (final Tree newTree : newTrees) {
			addTree(newTree);
		}
		setInNeedOfNewPPodVersionInfo();
		return removedTrees;
	}

	public TreeSet addTree(final Tree newTree) {
		checkNotNull(newTree);
		getTrees().add(newTree);
		setInNeedOfNewPPodVersionInfo();
		return this;
	}

	/**
	 * Returns the number of trees in this tree set.
	 * 
	 * @return the number of trees in this tree set
	 */
	public int getTreesSize() {
		return getTrees().size();
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

		retValue.append("TreeSet(").append("label=").append(
						this.label).append(TAB).append("trees=").append(
						this.trees).append(TAB).append(")");

		return retValue.toString();
	}

}
