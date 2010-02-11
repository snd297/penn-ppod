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
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * A phylogenetic tree.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = "TREE")
public class Tree extends UUPPodEntity {

	static final String TABLE = "TREE";

	@Column(name = "LABEL", nullable = false)
	private String label;

	@Lob
	@Column(name = "NEWICK", nullable = false)
	private String newick;

	@ManyToMany(mappedBy = "trees")
	private final Set<TreeSet> treeSets = newHashSet();

	Tree() {}

	/**
	 * Add <code>treeSet</code> to this <code>Tree</code>'s associated {@code
	 * TreeSet}s.
	 * <p>
	 * Intended to be package-private and used in conjunction with
	 * {@link TreeSet#addTree(Tree)}.
	 * 
	 * @param treeSet to be added to this {@code Tree}
	 * 
	 * @throws IllegalArgumentException if {@code treeSet.getOTUSet()} is not
	 *             equal to (@code
	 *             this.getTreeSets().iterator().next().getOTUSet()}
	 */
	void addTreeSet(final TreeSet treeSet) {
		checkNotNull(treeSet);
		if ((getTreeSets().size() == 0)
				|| getTreeSets().iterator().next().getOTUSet().equals(
						treeSet.getOTUSet())) {
			if (treeSets.add(treeSet)) {
				resetPPodVersionInfo();
			}
		} else {
			throw new IllegalArgumentException(
					"treeSet doesn't point to this tree's OTUSet: "
							+ getTreeSets().iterator().next().getOTUSet()
									.getLabel());
		}
	}

	/**
	 * See {@link Unmarshaller}.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see see {@code Unmarshaller}
	 * 
	 */
	@Override
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		super.afterUnmarshal(u, parent);
		if (parent instanceof TreeSet) {
			addTreeSet((TreeSet) parent);
		}
	}

	/**
	 * Return the label.
	 * 
	 * @return the label
	 */
	@XmlAttribute
	public String getLabel() {
		return label;
	}

	/**
	 * Get the newick string.
	 * 
	 * @return the newick string
	 */
	@XmlElement
	public String getNewick() {
		return newick;
	}

	/**
	 * Get all of the tree sets to which this tree belongs.
	 * 
	 * @return all of the tree sets to which this tree belongs
	 */
	public Set<TreeSet> getTreeSets() {
		return Collections.unmodifiableSet(treeSets);
	}

	/**
	 * Remove {@code treeSet} form this {@code Tree}s associated {@code TreeSet}
	 * s.
	 * <p>
	 * Intended to be package-private and used in conjunction with
	 * {@link TreeSet#removeTree(Tree)}.
	 * 
	 * @param treeSet the {@code TreeSet} that we're removing
	 */
	void removeTreeSet(final TreeSet treeSet) {
		if (treeSets.remove(treeSet)) {
			resetPPodVersionInfo();
		}
	}

	@Override
	public Tree resetPPodVersionInfo() {
		if (getAllowResetPPodVersionInfo()) {
			if (getPPodVersionInfo() == null) {
				// nothing to do
			} else {
				for (final TreeSet treeSet : getTreeSets()) {
					treeSet.resetPPodVersionInfo();
				}
				super.resetPPodVersionInfo();
			}
		}
		return this;
	}

	/**
	 * Set the label of this <code>Tree</code>.
	 * 
	 * @param label the label
	 * 
	 * @return this <code>Tree</code>
	 */
	public Tree setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(this.label)) {
			// nothing to do
		} else {
			this.label = label;
			resetPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Setter.
	 * 
	 * @param newick the Newick tree, with name - not integer descriptions
	 * 
	 * @return this {@code Tree}
	 */
	public Tree setNewick(final String newick) {
		checkNotNull(newick);
		if (newick.equals(this.newick)) {

		} else {
			this.newick = newick;
			resetPPodVersionInfo();
		}
		return this;
	}
}
