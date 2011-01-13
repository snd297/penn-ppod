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

import com.google.common.annotations.VisibleForTesting;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.imodel.IChild;
import edu.upenn.cis.ppod.imodel.IDependsOnParentOtus;
import edu.upenn.cis.ppod.imodel.ILabeled;
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
public class OtuSet extends UuPPodEntity {

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
			orphanRemoval = true)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = JOIN_COLUMN, nullable = false)
	private final List<StandardMatrix> standardMatrices = newArrayList();

	/** Nullable free-form description. */
	@Column(name = DESCRIPTION_COLUMN, nullable = true)
	@CheckForNull
	private String description;

	@OneToMany(
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = JOIN_COLUMN, nullable = false)
	private final List<DnaMatrix> dnaMatrices = newArrayList();

	@OneToMany(
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = JOIN_COLUMN, nullable = false)
	private final List<DnaSequenceSet> dnaSequenceSets = newArrayList();

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
			cascade = CascadeType.ALL)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = JOIN_COLUMN, nullable = false)
	private final List<Otu> otus = newArrayList();

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = Study.JOIN_COLUMN, insertable = false,
				updatable = false)
	@Nullable
	private Study parent;

	/** The tree sets that reference this OTU set. */
	@OneToMany(
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = JOIN_COLUMN, nullable = false)
	private final List<TreeSet> treeSets = newArrayList();

	/**
	 * Default constructor.
	 */
	public OtuSet() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitOTUSet(this);
		for (final IChild<OtuSet> child : getChildren()) {
			child.accept(visitor);
		}
		super.accept(visitor);
	}

	/**
	 * Add a DNA matrix to this OTU set.
	 * <p>
	 * Handles the {@code DnaMatrix->IOTUSet} side of the relationship.
	 * 
	 * @param matrix to be added
	 * 
	 * @throws IllegalArgumentException if this otu set already contains the
	 *             matrix
	 */
	public void addDnaMatrix(final DnaMatrix matrix) {
		checkNotNull(matrix);
		checkArgument(
				!dnaMatrices.contains(matrix),
				"otu set contains the matrix ["
						+ matrix.getLabel() + "]");
		dnaMatrices.add(matrix);
		matrix.setParent(this);
		setInNeedOfNewVersion();
	}

	public void addDnaMatrix(
			final int pos,
			final DnaMatrix matrix) {
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

	/**
	 * Add an {@code DnaSequenceSet}.
	 * <p>
	 * Also handles the {@code DnaSequenceSet->IOTUSet} side of the
	 * relationship.
	 * 
	 * @param dnaSequenceSet the new {@code DNASequenceSet}
	 * 
	 * @throws IllegalArgumentException if this otu set already contains the
	 *             matrix
	 */
	public void addDnaSequenceSet(
			final DnaSequenceSet sequenceSet) {
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
	public void addDnaSequenceSet(
			final int sequenceSetPos,
			final DnaSequenceSet sequenceSet) {
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
	 * Scaffolding code that does two things:
	 * <ol>
	 * <li>Adds <code>otu</code> to this {@code IOTUSet}'s constituent
	 * {@code IOTU}s</li>
	 * <li>Adds this {@code IOTUSet} to {@code otu}'s {@code IOTUSet}s</li>
	 * </ol>
	 * So it takes care of both sides of the <code>IOTUSet</code><->
	 * <code>IOTU</code> relationship.
	 * <p>
	 * {@code otu} must have a label that is unique relative to this OTU set.
	 * 
	 * @throws IllegalArgumentException if this OTU set already has an OTU with
	 *             {@code otu}'s label
	 * 
	 * @param otu see description
	 */
	public void addOtu(final Otu otu) {
		checkNotNull(otu);
		addOtuWithoutSetOtusOnChildren(otu);
		updateOtusOnChildren();
	}

	private void addOtuWithoutSetOtusOnChildren(final Otu otu) {
		checkNotNull(otu);
		final Otu dupNameOTU =
				find(getOtus(),
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
		if (getOtusModifiable().add(otu)) {
			otu.setParent(this);
			setInNeedOfNewVersion();
		}
	}

	/**
	 * Add {@code matrix} to this {@code OTUSet} at the specified position.
	 * Shifts the element currently at that position (if any) and any subsequent
	 * elements to the right (adds one to their indices).
	 * <p>
	 * Also handles the {@code StandardMatrix->IOTUSet} side of the
	 * relationship.
	 * 
	 * 
	 * @param matrix matrix we're adding
	 * 
	 * @throw IllegalArgumentException if this OTU set already contains the
	 *        matrix
	 */
	public void addStandardMatrix(
			final int pos,
			final StandardMatrix matrix) {
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
			final StandardMatrix matrix) {
		checkNotNull(matrix);
		checkArgument(
				!standardMatrices.contains(matrix),
				"otu set contains the matrix ["
						+ matrix.getLabel() + "]");
		standardMatrices.add(matrix);
		matrix.setParent(this);
		setInNeedOfNewVersion();
	}

	/**
	 * Add a tree set to this OTU set.
	 * <p>
	 * Also handles the {@code TreeSet->OTUSet} side of the relationship.
	 * 
	 * @param treeSet to be added
	 */
	public void addTreeSet(
			final int treeSetPos,
			final TreeSet treeSet) {
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
	public void addTreeSet(final TreeSet treeSet) {
		checkNotNull(treeSet);
		checkArgument(
				!treeSets.contains(treeSet),
				"otu set already contains the tree set ["
						+ treeSet.getLabel() + "]");
		getTreeSetsModifiable().add(treeSet);
		treeSet.setParent(this);
		setInNeedOfNewVersion();
	}

	@VisibleForTesting
	Set<IChild<OtuSet>> getChildren() {
		final Set<IChild<OtuSet>> children = newHashSet();
		children.addAll(getOtus());
		children.addAll(getDependentChildren());
		return children;
	}

	@VisibleForTesting
	Set<IDependsOnParentOtus> getDependentChildren() {
		final Set<IDependsOnParentOtus> children = newHashSet();
		children.addAll(getStandardMatrices());
		children.addAll(getDnaMatrices());
		children.addAll(getTreeSets());
		children.addAll(getDnaSequenceSets());
		return children;
	}

	/**
	 * Get the description.
	 * 
	 * @return the description
	 */
	@Nullable
	public String getDescription() {
		return description;
	}

	/** {@inheritDoc} */
	public List<DnaMatrix> getDnaMatrices() {
		return Collections.unmodifiableList(dnaMatrices);
	}

	protected List<DnaMatrix> getDnaMatricesModifiable() {
		return dnaMatrices;
	}

	/** {@inheritDoc} */
	public List<DnaSequenceSet> getDnaSequenceSets() {
		return Collections.unmodifiableList(dnaSequenceSets);
	}

	protected List<DnaSequenceSet> getDnaSequenceSetsModifiable() {
		return dnaSequenceSets;
	}

	/**
	 * Getter. {@code null} when the object is created. Once set, it will never
	 * be {@code null}.
	 * 
	 * @return the label
	 */
	@Nullable
	public String getLabel() {
		return label;
	}

	/**
	 * Get the {@code OTU}s that make up this {@code OTUSet}.
	 * 
	 * @return the {@code OTU}s that make up this {@code OTUSet}
	 */
	public List<Otu> getOtus() {
		return Collections.unmodifiableList(otus);
	}

	/**
	 * Get a modifiable reference to this the otus.
	 * 
	 * @return a modifiable reference to this the otus.
	 */
	protected List<Otu> getOtusModifiable() {
		return otus;
	}

	/**
	 * Get the owning study.
	 * 
	 * @return the owning study
	 */
	@Nullable
	public Study getParent() {
		return parent;
	}

	/**
	 * Get the standard matrices contained in this OTU set.
	 * 
	 * @return the standard matrices contained in this OTU set
	 */
	public List<StandardMatrix> getStandardMatrices() {
		return Collections.unmodifiableList(standardMatrices);
	}

	protected List<StandardMatrix> getStandardMatricesModifiable() {
		return standardMatrices;
	}

	/**
	 * Get the tree sets contained in this OTU set.
	 * 
	 * @return the tree sets contained in this OTU set
	 */
	public List<TreeSet> getTreeSets() {
		return Collections.unmodifiableList(treeSets);
	}

	protected List<TreeSet> getTreeSetsModifiable() {
		return treeSets;
	}

	/**
	 * Remove {@code matrix} from this OTU set.
	 * 
	 * @param matrix to be removed
	 * 
	 * @throws IllegalArgumentException if this OTU set does not contain the
	 *             matrix
	 */
	public void removeDnaMatrix(final DnaMatrix matrix) {
		checkNotNull(matrix);
		checkArgument(dnaMatrices.contains(matrix),
				"otu set does not contain the dna matrix [" + matrix.getLabel()
						+ "]");
		getDnaMatricesModifiable().remove(matrix);
		matrix.setParent(null);
		setInNeedOfNewVersion();
	}

	/**
	 * Remove {@code sequenceSet} from this OTU set.
	 * 
	 * @param sequenceSet to be removed
	 * 
	 * @throws IllegalArgumentException if the sequence set is not contained in
	 *             this OTU set
	 */
	public void removeDnaSequenceSet(final DnaSequenceSet sequenceSet) {
		checkNotNull(sequenceSet);
		checkArgument(getDnaSequenceSets().contains(sequenceSet),
				"otu does not contain the dna sequence set labeled ["
						+ sequenceSet.getLabel() + "]");
		getDnaSequenceSetsModifiable().remove(sequenceSet);
		sequenceSet.setParent(null);
		setInNeedOfNewVersion();
	}

	/**
	 * Remove {@code matrix} from this OTU set.
	 * 
	 * @param matrix to be removed
	 * 
	 * @throws IllegalArgumentException if this OTU set does not contain the
	 *             matrix
	 */
	public void removeStandardMatrix(final StandardMatrix matrix) {
		checkNotNull(matrix);
		checkArgument(standardMatrices.contains(matrix),
				"otu set does not contain the matrix [" + matrix.getLabel()
						+ "]");
		getStandardMatricesModifiable().remove(matrix);
		matrix.setParent(null);
		setInNeedOfNewVersion();
	}

	/**
	 * Remove {@code treeSet} from this OTU set.
	 * 
	 * @param matrix to be removed
	 * 
	 * @throws IllegalArgumentException if the tree set does not belong to this
	 *             otu set
	 */
	public void removeTreeSet(final TreeSet treeSet) {
		checkNotNull(treeSet);
		checkArgument(getTreeSets().contains(treeSet),
				"otu set does not contain the given tree set ["
						+ treeSet.getLabel() + "]");
		treeSets.remove(treeSet);
		treeSet.setParent(null);
		setInNeedOfNewVersion();
	}

	/**
	 * Set the descriptino
	 * 
	 * @param description the description
	 */
	public void setDescription(@CheckForNull final String description) {
		if (equal(getDescription(), description)) {

		} else {
			this.description = description;
			setInNeedOfNewVersion();
		}
	}

	@Override
	public void setInNeedOfNewVersion() {
		final Study study = getParent();
		if (study != null) {
			study.setInNeedOfNewVersion();
		}
		super.setInNeedOfNewVersion();
	}

	/**
	 * Set the label of this OTU set
	 * 
	 * @param label the label
	 */
	public void setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(getLabel())) {

		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
	}

	/**
	 * Set this OTU set's OTUs.
	 * <p>
	 * If this method is effectively removing any of this sets's original OTUs,
	 * then the {@code OTU->OTUSet} relationship is severed.
	 * 
	 * @param otus the otus to assign to this OTU set
	 */
	public void setOtus(final List<? extends Otu> otus) {
		checkNotNull(otus);
		if (otus.equals(getOtus())) {

		} else {

			final List<Otu> removedOtus = newArrayList(this.otus);
			removedOtus.removeAll(otus);

			for (final Otu removedOtu : removedOtus) {
				removedOtu.setParent(null);
			}

			this.otus.clear();
			for (final Otu otu : otus) {
				addOtuWithoutSetOtusOnChildren(otu);
			}

			setParentOnChildren();

			setInNeedOfNewVersion();
		}

		return;
	}

	/** {@inheritDoc} */
	public void setParent(@CheckForNull final Study parent) {
		this.parent = parent;
	}

	private void setParentOnChildren() {
		for (final IChild<OtuSet> child : getChildren()) {
			child.setParent(this);
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

	private void updateOtusOnChildren() {
		for (final IDependsOnParentOtus child : getDependentChildren()) {
			child.updateOtus();
		}
	}
}
