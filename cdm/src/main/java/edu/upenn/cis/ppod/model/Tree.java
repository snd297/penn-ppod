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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import edu.upenn.cis.ppod.imodel.ITree;
import edu.upenn.cis.ppod.imodel.ITreeSet;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A phylogenetic tree.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = Tree.TABLE)
public class Tree extends UUPPodEntity implements ITree {

	public static class Adapter extends XmlAdapter<Tree, ITree> {

		@Override
		public Tree marshal(final ITree tree) {
			return (Tree) tree;
		}

		@Override
		public ITree unmarshal(final Tree tree) {
			return tree;
		}
	}

	public static final String TABLE = "TREE";

	@Column(name = "LABEL", nullable = false)
	@CheckForNull
	private String label;

	@Lob
	@Column(name = "NEWICK", nullable = false)
	@CheckForNull
	private String newick;

	@ManyToOne(optional = false, targetEntity = TreeSet.class)
	@JoinColumn(name = TreeSet.JOIN_COLUMN, insertable = false,
				updatable = false)
	@CheckForNull
	private ITreeSet parent;

	Tree() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitTree(this);
		super.accept(visitor);
	}

	/**
	 * See {@link Unmarshaller}.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see see {@code Unmarshaller}
	 * 
	 */
	protected void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent) {
		// don't checkNotNull(parent) since it's a jaxb callback
		setParent((TreeSet) parent);
	}

	/** {@inheritDoc} */
	@XmlAttribute
	@Nullable
	public String getLabel() {
		return label;
	}

	/** {@inheritDoc} */
	@XmlElement
	@Nullable
	public String getNewick() {
		return newick;
	}

	@Nullable
	public ITreeSet getParent() {
		return parent;
	}

	@Override
	public void setInNeedOfNewVersion() {
		if (parent != null) {
			parent.setInNeedOfNewVersion();
		}
		super.setInNeedOfNewVersion();
	}

	/** {@inheritDoc} */
	public void setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(this.label)) {
			// nothing to do
		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
	}

	/** {@inheritDoc} */
	public void setNewick(final String newick) {
		checkNotNull(newick);
		if (newick.equals(getNewick())) {

		} else {
			this.newick = newick;
			setInNeedOfNewVersion();
		}
	}

	/** {@inheritDoc} */
	public void setParent(@CheckForNull final ITreeSet parent) {
		this.parent = parent;
	}
}
