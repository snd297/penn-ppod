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
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.List;
import java.util.Set;

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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.common.annotations.VisibleForTesting;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.imodel.IChild;
import edu.upenn.cis.ppod.imodel.IDnaMatrix;
import edu.upenn.cis.ppod.imodel.IDnaSequenceSet;
import edu.upenn.cis.ppod.imodel.ILabeled;
import edu.upenn.cis.ppod.imodel.IOtu;
import edu.upenn.cis.ppod.imodel.IOtuSet;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStudy;
import edu.upenn.cis.ppod.imodel.ITreeSet;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A set of {@link OTU}s.
 * <p>
 * The relationship between {@code OTUSet} and {@code OTU} is one-to-many.
 * 
 * @author Shirley Cohen
 * @author Sam Donnelly
 */
@Entity
@Table(name = OtuSet.TABLE)
public class OtuSet
		extends UuPPodEntityWithDocId
		implements IOtuSet {

	public static class Adapter extends XmlAdapter<OtuSet, IOtuSet> {

		@Override
		public OtuSet marshal(final IOtuSet otuSet) {
			return (OtuSet) otuSet;
		}

		@Override
		public IOtuSet unmarshal(final OtuSet otuSet) {
			return otuSet;
		}
	}

	/** The column that stores the description. */
	public static final String DESCRIPTION_COLUMN = "DESCRIPTION";

	/** The table for this entity. */
	public static final String TABLE = "OTU_SET";

	/**
	 * To be used in the names of foreign keys that point at this table.
	 */
	public static final String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	/** The column that stores the label. */
	public static final String LABEL_COLUMN = "LABEL";

	@OneToMany(
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			targetEntity = StandardMatrix.class)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = JOIN_COLUMN, nullable = false)
	private final List<IStandardMatrix> standardMatrices = newArrayList();

	/** Nullable free-form description. */
	@Column(name = DESCRIPTION_COLUMN, nullable = true)
	@CheckForNull
	private String description;

	@OneToMany(
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			targetEntity = DnaMatrix.class)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = JOIN_COLUMN, nullable = false)
	private final List<IDnaMatrix> dnaMatrices = newArrayList();

	@OneToMany(
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			targetEntity = DnaSequenceSet.class)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = JOIN_COLUMN, nullable = false)
	private final List<IDnaSequenceSet> dnaSequenceSets = newArrayList();

	/**
	 * Non-unique label.
	 * <p>
	 * OTU set labels are unique within a particular {@code IStudy}
	 */
	@Column(name = "LABEL", nullable = false)
	@Nullable
	private String label;

	/** The OTUs in this OTU set. */
	@OneToMany(
			orphanRemoval = true,
			cascade = CascadeType.ALL,
			targetEntity = Otu.class)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = JOIN_COLUMN, nullable = false)
	private final List<IOtu> otus = newArrayList();

	@ManyToOne(fetch = FetchType.LAZY, optional = false,
			targetEntity = Study.class)
	@JoinColumn(name = Study.JOIN_COLUMN, insertable = false,
				updatable = false)
	@Nullable
	private IStudy parent;

	/** The tree sets that reference this OTU set. */
	@OneToMany(
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			targetEntity = TreeSet.class)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = JOIN_COLUMN, nullable = false)
	private final List<ITreeSet> treeSets = newArrayList();

	/**
	 * Default constructor.
	 */
	public OtuSet() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitOTUSet(this);
		for (final IChild<IOtuSet> child : getChildren()) {
			child.accept(visitor);
		}
		super.accept(visitor);
	}

	/** {@inheritDoc} */
	public void addDNAMatrix(final IDnaMatrix matrix) {
		checkNotNull(matrix);
		checkArgument(
				!dnaMatrices.contains(matrix),
				"otu set contains the matrix ["
						+ matrix.getLabel() + "]");
		dnaMatrices.add(matrix);
		matrix.setParent(this);
		setInNeedOfNewVersion();
	}

	/** {@inheritDoc} */
	public void addDNAMatrix(
			final int pos,
			final IDnaMatrix matrix) {
		checkNotNull(matrix);
		checkArgument(pos >= 0, "pos < 0");
		checkArgument(
				!dnaMatrices.contains(matrix),
				"otu set already contains the dna matrix ["
						+ matrix.getLabel() + "]");
		dnaMatrices.add(pos, matrix);
		matrix.setParent(this);
		setInNeedOfNewVersion();
	}

	/** {@inheritDoc} */
	public void addDNASequenceSet(
			final IDnaSequenceSet sequenceSet) {
		checkNotNull(sequenceSet);
		checkArgument(
				!dnaSequenceSets.contains(sequenceSet),
				"otu set already contains the standard matrix ["
						+ sequenceSet.getLabel() + "]");
		dnaSequenceSets.add(sequenceSet);
		sequenceSet.setParent(this);
		setInNeedOfNewVersion();
	}

	/** {@inheritDoc} */
	public void addDNASequenceSet(
			final int sequenceSetPos,
			final IDnaSequenceSet sequenceSet) {
		checkNotNull(sequenceSet);
		checkArgument(sequenceSetPos >= 0, "sequenceSetPos < 0");
		checkArgument(
				!dnaSequenceSets.contains(sequenceSet),
				"otu set already contains the matrix ["
						+ sequenceSet.getLabel() + "]");
		dnaSequenceSets.add(sequenceSetPos, sequenceSet);
		sequenceSet.setParent(this);
		setInNeedOfNewVersion();
	}

	/**
	 * {@inheritDoc}
	 */
	public void addOTU(final IOtu otu) {
		checkNotNull(otu);
		addOTUWithoutSetOTUsOnChildren(otu);
		setParentOnChildren();
	}

	private void addOTUWithoutSetOTUsOnChildren(final IOtu otu) {
		checkNotNull(otu);
		final IOtu dupNameOTU =
				find(getOTUs(),
						compose(
								equalTo(
								otu.getLabel()),
								ILabeled.getLabel),
								null);
		if (dupNameOTU == null || otu.equals(dupNameOTU)) {

		} else {
			checkArgument(false, "OTUSet labeled '" + getLabel()
									+ "' already has an OTU labeled '"
									+ otu.getLabel() + "'");
		}
		if (getOTUsModifiable().add(otu)) {
			otu.setParent(this);
			setInNeedOfNewVersion();
		}
	}

	/** {@inheritDoc} */
	public void addStandardMatrix(
			final int pos,
			final IStandardMatrix matrix) {
		checkNotNull(matrix);
		checkArgument(pos >= 0, "pos < 0");
		checkArgument(
				!standardMatrices.contains(matrix),
				"otu set contains the matrix ["
						+ matrix.getLabel() + "]");
		standardMatrices.add(pos, matrix);
		matrix.setParent(this);
		setInNeedOfNewVersion();
	}

	/** {@inheritDoc} */
	public void addStandardMatrix(
			final IStandardMatrix matrix) {
		checkNotNull(matrix);
		checkArgument(
				!standardMatrices.contains(matrix),
				"otu set contains the matrix ["
						+ matrix.getLabel() + "]");
		standardMatrices.add(matrix);
		matrix.setParent(this);
		setInNeedOfNewVersion();
	}

	/** {@inheritDoc} */
	public void addTreeSet(
			final int treeSetPos,
			final ITreeSet treeSet) {
		checkNotNull(treeSet);
		checkArgument(treeSetPos >= 0, "pos < 0");
		checkArgument(
				!treeSets.contains(treeSet),
				"otu set contains the tree set ["
						+ treeSet.getLabel() + "]");
		treeSets.add(treeSetPos, treeSet);
		treeSet.setParent(this);
		setInNeedOfNewVersion();
	}

	/** {@inheritDoc} */
	public void addTreeSet(final ITreeSet treeSet) {
		checkNotNull(treeSet);
		checkArgument(
				!treeSets.contains(treeSet),
				"otu set already contains the tree set ["
						+ treeSet.getLabel() + "]");
		getTreeSetsModifiable().add(treeSet);
		treeSet.setParent(this);
		setInNeedOfNewVersion();
	}

	/**
	 * See {@link Unmarshaller}.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	public void afterUnmarshal(
			@Nullable final Unmarshaller u,
			final Object parent) {
		this.parent = (IStudy) parent;
	}

	@VisibleForTesting
	Set<IChild<IOtuSet>> getChildren() {
		final Set<IChild<IOtuSet>> children = newHashSet();
		children.addAll(getOTUs());
		children.addAll(getStandardMatrices());
		children.addAll(getDNAMatrices());
		children.addAll(getTreeSets());
		children.addAll(getDNASequenceSets());
		return children;
	}

	/**
	 * Getter. {@code null} is a legal value.
	 * 
	 * @return the description
	 */
	@XmlAttribute
	public String getDescription() {
		return description;
	}

	/** {@inheritDoc} */
	public List<IDnaMatrix> getDNAMatrices() {
		return Collections.unmodifiableList(dnaMatrices);
	}

	@XmlElement(name = "dnaMatrix")
	protected List<IDnaMatrix> getDNAMatricesModifiable() {
		return dnaMatrices;
	}

	/** {@inheritDoc} */
	public List<IDnaSequenceSet> getDNASequenceSets() {
		return Collections.unmodifiableList(dnaSequenceSets);
	}

	@XmlElement(name = "dnaSequenceSet")
	protected List<IDnaSequenceSet> getDNASequenceSetsModifiable() {
		return dnaSequenceSets;
	}

	/**
	 * Getter. {@code null} when the object is created.
	 * 
	 * @return the label
	 */
	@XmlAttribute
	public String getLabel() {
		return label;
	}

	/**
	 * Get the {@code OTU}s that make up this {@code OTUSet}.
	 * 
	 * @return the {@code OTU}s that make up this {@code OTUSet}
	 */
	public List<IOtu> getOTUs() {
		return Collections.unmodifiableList(otus);
	}

	/**
	 * Get a modifiable reference to this the otus.
	 * 
	 * @return a modifiable reference to this the otus.
	 */
	@XmlElement(name = "otu")
	protected List<IOtu> getOTUsModifiable() {
		return otus;
	}

	/** {@inheritDoc} */
	public IStudy getParent() {
		return parent;
	}

	/** {@inheritDoc} */
	public List<IStandardMatrix> getStandardMatrices() {
		return Collections.unmodifiableList(standardMatrices);
	}

	@XmlElement(name = "matrix")
	protected List<IStandardMatrix> getStandardMatricesModifiable() {
		return standardMatrices;
	}

	/** {@inheritDoc} */
	public List<ITreeSet> getTreeSets() {
		return Collections.unmodifiableList(treeSets);
	}

	@XmlElement(name = "treeSet")
	protected List<ITreeSet> getTreeSetsModifiable() {
		return treeSets;
	}

	/** {@inheritDoc} */
	public void removeDNAMatrix(final IDnaMatrix matrix) {
		checkNotNull(matrix);
		checkArgument(dnaMatrices.contains(matrix),
				"otu set does not contain the dna matrix [" + matrix.getLabel()
						+ "]");
		getDNAMatricesModifiable().remove(matrix);
		matrix.setParent(null);
		setInNeedOfNewVersion();
	}

	/** {@inheritDoc} */
	public void removeDNASequenceSet(final IDnaSequenceSet sequenceSet) {
		checkNotNull(sequenceSet);
		checkArgument(getDNASequenceSets().contains(sequenceSet),
				"otu does not contain the dna sequence set labeled ["
						+ sequenceSet.getLabel() + "]");
		getDNASequenceSetsModifiable().remove(sequenceSet);
		sequenceSet.setParent(null);
		setInNeedOfNewVersion();
	}

	/** {@inheritDoc} */
	public void removeStandardMatrix(final IStandardMatrix matrix) {
		checkNotNull(matrix);
		checkArgument(standardMatrices.contains(matrix),
				"otu set does not contain the matrix [" + matrix.getLabel()
						+ "]");
		getStandardMatricesModifiable().remove(matrix);
		matrix.setParent(null);
		setInNeedOfNewVersion();
	}

	/** {@inheritDoc} */
	public void removeTreeSet(final ITreeSet treeSet) {
		checkNotNull(treeSet);
		checkArgument(getTreeSets().contains(treeSet),
				"otu set does not contain the given tree set ["
						+ treeSet.getLabel() + "]");
		treeSets.remove(treeSet);
		treeSet.setParent(null);
		setInNeedOfNewVersion();
	}

	/** {@inheritDoc} */
	public void setDescription(@Nullable final String description) {
		if (equal(getDescription(), description)) {

		} else {
			this.description = description;
			setInNeedOfNewVersion();
		}
	}

	@Override
	public void setInNeedOfNewVersion() {
		final IStudy study = getParent();
		if (study != null) {
			study.setInNeedOfNewVersion();
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
	public List<IOtu> setOTUs(final List<? extends IOtu> otus) {
		checkNotNull(otus);
		if (otus.equals(getOTUs())) {
			return Collections.emptyList();
		}

		final List<IOtu> removedOTUs = newArrayList(getOTUs());
		removedOTUs.removeAll(otus);

		for (final IOtu removedOTU : removedOTUs) {
			removedOTU.setParent(null);
		}

		getOTUsModifiable().clear();
		for (final IOtu otu : otus) {
			addOTUWithoutSetOTUsOnChildren(otu);
		}

		setParentOnChildren();

		setInNeedOfNewVersion();

		return removedOTUs;
	}

	/** {@inheritDoc} */
	public void setParent(@Nullable final IStudy parent) {
		this.parent = parent;
	}

	private void setParentOnChildren() {
		// Now let's let everyone know about the new OTUs
		for (final IChild<IOtuSet> otu : getOTUs()) {
			otu.setParent(this);
		}

		for (final IChild<IOtuSet> matrix : getStandardMatrices()) {
			matrix.setParent(this);
		}

		for (final IChild<IOtuSet> matrix : getDNAMatrices()) {
			matrix.setParent(this);
		}

		for (final IChild<IOtuSet> sequenceSet : getDNASequenceSets()) {
			sequenceSet.setParent(this);
		}

		for (final IChild<IOtuSet> treeSet : getTreeSets()) {
			treeSet.setParent(this);
		}
	}

	/**
	 * Constructs a <code>String</code> with all attributes in name=value
	 * format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	@Override
	public String toString() {
		final String TAB = ",";

		final StringBuilder retValue = new StringBuilder();

		retValue.append("OTUSet(").append(super.toString()).append(TAB).append(
				"id=").append(TAB).append("version=").append(TAB).append(
				"label=").append(this.label).append(TAB).append("otus=")
				.append(this.otus).append(TAB).append(")");

		return retValue.toString();
	}
}
