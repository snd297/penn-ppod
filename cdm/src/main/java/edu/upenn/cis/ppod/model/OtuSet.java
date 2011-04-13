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
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.List;
import java.util.Set;

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
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.google.common.annotations.VisibleForTesting;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.dto.ILabeled;
import edu.upenn.cis.ppod.imodel.IChild;
import edu.upenn.cis.ppod.imodel.IDependsOnParentOtus;

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
public class OtuSet extends UuPPodEntity implements ILabeled {

	/** The column that stores the description. */
	public static final String DESCRIPTION_COLUMN = "DESCRIPTION";

	/** The table for this entity. */
	public static final String TABLE = "OTU_SET";

	/**
	 * To be used in the names of foreign keys that point at this table.
	 */
	public static final String ID_COLUMN =
			TABLE + "_ID";

	/** The column that stores the label. */
	public static final String LABEL_COLUMN = "LABEL";

	@CheckForNull
	private Long id;

	@CheckForNull
	private Integer version;

	private List<StandardMatrix> standardMatrices = newArrayList();

	/** Nullable free-form description. */
	@CheckForNull
	private String description;

	private List<DnaMatrix> dnaMatrices = newArrayList();

	private List<ProteinMatrix> proteinMatrices = newArrayList();

	// private List<DnaSequenceSet> dnaSequenceSets = newArrayList();

	/**
	 * Non-unique label.
	 * <p>
	 * OTU set labels are unique within a particular {@code IStudy}
	 */
	@CheckForNull
	private String label;

	/** The OTUs in this OTU set. */
	private List<Otu> otus = newArrayList();

	@CheckForNull
	private Study parent;

	/** The tree sets that reference this OTU set. */
	private List<TreeSet> treeSets = newArrayList();

	/**
	 * Default constructor.
	 */
	public OtuSet() {}

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
	}

	public void addDnaMatrix(
			final int pos,
			final DnaMatrix matrix) {
		checkArgument(pos >= 0, "pos < 0");
		checkNotNull(matrix);
		checkArgument(
				!dnaMatrices.contains(matrix),
				"otu set already contains the dna matrix ["
						+ matrix.getLabel() + "]");
		dnaMatrices.add(pos, matrix);
		matrix.setParent(this);
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
	// public void addDnaSequenceSet(
	// final DnaSequenceSet sequenceSet) {
	// checkNotNull(sequenceSet);
	// checkArgument(
	// !dnaSequenceSets.contains(sequenceSet),
	// "otu set already contains the standard matrix ["
	// + sequenceSet.getLabel() + "]");
	// dnaSequenceSets.add(sequenceSet);
	// sequenceSet.setParent(this);
	// }

	/** {@inheritDoc} */
	// public void addDnaSequenceSet(
	// final int sequenceSetPos,
	// final DnaSequenceSet sequenceSet) {
	// checkArgument(sequenceSetPos >= 0, "sequenceSetPos < 0");
	// checkNotNull(sequenceSet);
	// checkArgument(
	// !dnaSequenceSets.contains(sequenceSet),
	// "otu set already contains the matrix ["
	// + sequenceSet.getLabel() + "]");
	// dnaSequenceSets.add(sequenceSetPos, sequenceSet);
	// sequenceSet.setParent(this);
	// }

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
		addOtuWithoutUpdateOtusOnChildren(otu);
		updateOtusOnChildren();
	}

	private void addOtuWithoutUpdateOtusOnChildren(final Otu otu) {
		checkForDuplicateOtuLabels(otu, otus);
		otus.add(otu);
		otu.setParent(this);
	}

	public void addProteinMatrix(
			final int pos,
			final ProteinMatrix matrix) {
		checkArgument(pos >= 0, "pos < 0");
		checkNotNull(matrix);
		checkArgument(
				!proteinMatrices.contains(matrix),
				"otu set already contains the protein matrix ["
						+ matrix.getLabel() + "]");
		proteinMatrices.add(pos, matrix);
		matrix.setParent(this);
	}

	public void addProteinMatrix(final ProteinMatrix matrix) {
		checkNotNull(matrix);
		checkArgument(
				!proteinMatrices.contains(matrix),
				"otu set contains the matrix ["
						+ matrix.getLabel() + "]");
		proteinMatrices.add(matrix);
		matrix.setParent(this);
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
	}

	/** {@inheritDoc} */
	public void addTreeSet(final TreeSet treeSet) {
		checkNotNull(treeSet);
		checkArgument(
				!treeSets.contains(treeSet),
				"otu set already contains the tree set ["
						+ treeSet.getLabel() + "]");
		treeSets.add(treeSet);
		treeSet.setParent(this);
	}

	private void checkForDuplicateOtuLabels(
			final Otu otu,
			final Iterable<Otu> otus) {
		final Otu dupNameOtu =
				find(otus,
						compose(
								equalTo(
								otu.getLabel()),
								ILabeled.getLabel),
							null);
		if (dupNameOtu == null || otu.equals(dupNameOtu)) {

		} else {
			checkArgument(false, "OtuSet labeled '" + getLabel()
								+ "' already has an Otu labeled '"
								+ otu.getLabel() + "'");
		}
	}

	/**
	 * Set this OTU set's OTUs, calls updateOtus on dependent children.
	 * <p>
	 * Takes care of both sides of the OTU set <-> OTU relationship.
	 * 
	 * @param otus the otus to assign to this OTU set
	 */
	public void clearAndAddOtus(final List<? extends Otu> otus) {
		checkNotNull(otus);

		final List<Otu> removedOtus = newArrayList(this.otus);
		removedOtus.removeAll(otus);

		for (final Otu removedOtu : removedOtus) {
			removedOtu.setParent(null);
		}

		this.otus.clear();

		for (final Otu otu : otus) {
			addOtuWithoutUpdateOtusOnChildren(otu);
		}
		updateOtusOnChildren();
	}

	@Transient
	@VisibleForTesting
	Set<IChild<OtuSet>> getChildren() {
		final Set<IChild<OtuSet>> children = newHashSet();
		children.addAll(getOtus());
		children.addAll(getDependentChildren());
		return children;
	}

	@Transient
	@VisibleForTesting
	Set<IDependsOnParentOtus> getDependentChildren() {
		final Set<IDependsOnParentOtus> children = newHashSet();
		children.addAll(getStandardMatrices());
		children.addAll(getDnaMatrices());
		children.addAll(getTreeSets());
		// children.addAll(getDnaSequenceSets());
		return children;
	}

	/**
	 * Get the description.
	 * 
	 * @return the description
	 */
	@Column(name = DESCRIPTION_COLUMN, nullable = true)
	@Nullable
	public String getDescription() {
		return description;
	}

	@OneToMany(
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = ID_COLUMN, nullable = false)
	public List<DnaMatrix> getDnaMatrices() {
		return dnaMatrices;
	}

	// @OneToMany(
	// cascade = CascadeType.ALL,
	// orphanRemoval = true)
	// @OrderColumn(name = "POSITION")
	// @JoinColumn(name = ID_COLUMN, nullable = false)
	// public List<DnaSequenceSet> getDnaSequenceSets() {
	// return dnaSequenceSets;
	// }

	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@Nullable
	public Long getId() {
		return id;
	}

	/**
	 * Getter. {@code null} when the object is created. Once set, it will never
	 * be {@code null}.
	 * 
	 * @return the label
	 */
	@Column(name = "LABEL", nullable = false)
	@Index(name = "IDX_LABEL")
	@Nullable
	public String getLabel() {
		return label;
	}

	/**
	 * Get the {@code OTU}s that make up this {@code OTUSet}.
	 * 
	 * @return the {@code OTU}s that make up this {@code OTUSet}
	 */
	@OneToMany(
			orphanRemoval = true,
			cascade = CascadeType.ALL)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = ID_COLUMN, nullable = false)
	public List<Otu> getOtus() {
		return otus;
	}

	/**
	 * Get the owning study.
	 * 
	 * @return the owning study
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = Study.ID_COLUMN, insertable = false,
				updatable = false)
	@Nullable
	public Study getParent() {
		return parent;
	}

	@OneToMany(
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = ID_COLUMN, nullable = false)
	public List<ProteinMatrix> getProteinMatrices() {
		return proteinMatrices;
	}

	/**
	 * Get the standard matrices contained in this OTU set.
	 * 
	 * @return the standard matrices contained in this OTU set
	 */
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = ID_COLUMN, nullable = false)
	public List<StandardMatrix> getStandardMatrices() {
		return standardMatrices;
	}

	/**
	 * Get the tree sets contained in this OTU set.
	 * 
	 * @return the tree sets contained in this OTU set
	 */
	@OneToMany(
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = ID_COLUMN, nullable = false)
	public List<TreeSet> getTreeSets() {
		return treeSets;
	}

	@Version
	@Column(name = "OBJ_VERSION")
	@Nullable
	public Integer getVersion() {
		return version;
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
		dnaMatrices.remove(matrix);
		matrix.setParent(null);
	}

	/**
	 * Remove {@code sequenceSet} from this OTU set.
	 * 
	 * @param sequenceSet to be removed
	 * 
	 * @throws IllegalArgumentException if the sequence set is not contained in
	 *             this OTU set
	 */
	// public void removeDnaSequenceSet(final DnaSequenceSet sequenceSet) {
	// checkNotNull(sequenceSet);
	// checkArgument(getDnaSequenceSets().contains(sequenceSet),
	// "otu does not contain the dna sequence set labeled ["
	// + sequenceSet.getLabel() + "]");
	// dnaSequenceSets.remove(sequenceSet);
	// sequenceSet.setParent(null);
	// }

	/**
	 * Remove {@code matrix} from this OTU set.
	 * 
	 * @param matrix to be removed
	 * 
	 * @throws IllegalArgumentException if this OTU set does not contain the
	 *             matrix
	 */
	public void removeProteinMatrix(final ProteinMatrix matrix) {
		checkNotNull(matrix);
		checkArgument(
				proteinMatrices.contains(matrix),
				"otu set does not contain the protein matrix ["
						+ matrix.getLabel()
						+ "]");
		proteinMatrices.remove(matrix);
		matrix.setParent(null);
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
		standardMatrices.remove(matrix);
		matrix.setParent(null);
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
	}

	/**
	 * Set the description.
	 * 
	 * @param description the description
	 */
	public void setDescription(@CheckForNull final String description) {
		this.description = description;
	}

	@SuppressWarnings("unused")
	private void setDnaMatrices(final List<DnaMatrix> matrices) {
		this.dnaMatrices = matrices;
	}

	// @SuppressWarnings("unused")
	// private void setDnaSequenceSets(final List<DnaSequenceSet> sequenceSets)
	// {
	// this.dnaSequenceSets = sequenceSets;
	// }

	@SuppressWarnings("unused")
	private void setId(final Long id) {
		this.id = id;
	}

	/**
	 * Set the label of this OTU set
	 * 
	 * @param label the label
	 */
	public void setLabel(final String label) {
		this.label = label;
	}

	@SuppressWarnings("unused")
	private void setOtus(final List<Otu> otus) {
		this.otus = otus;
	}

	/** {@inheritDoc} */
	public void setParent(@CheckForNull final Study parent) {
		this.parent = parent;
	}

	@SuppressWarnings("unused")
	private void setProteinMatrices(final List<ProteinMatrix> matrices) {
		this.proteinMatrices = matrices;
	}

	@SuppressWarnings("unused")
	private void setStandardMatrices(final List<StandardMatrix> matrices) {
		this.standardMatrices = matrices;
	}

	@SuppressWarnings("unused")
	private void setTreeSets(final List<TreeSet> treeSets) {
		this.treeSets = treeSets;
	}

	@SuppressWarnings("unused")
	private void setVersion(final Integer version) {
		this.version = version;
	}

	private void updateOtusOnChildren() {
		for (final IDependsOnParentOtus child : getDependentChildren()) {
			child.updateOtus();
		}
	}
}
