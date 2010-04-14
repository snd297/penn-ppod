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
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.annotations.Cascade;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A set of {@link OTU}s.
 *<p>
 * The relationship between {@code OTU} and {@code OTUSet} is one-to-many.
 * 
 * @author Shirley Cohen
 * @author Sam Donnelly
 */
@Entity
@Table(name = OTUSet.TABLE)
public class OTUSet extends UUPPodEntityWXmlId implements Iterable<OTU> {

	/** The table for this entity. Intentionally package-private. */
	static final String TABLE = "OTU_SET";

	/**
	 * The column where an {@code OTUSet}'s {@link javax.persistence.Id} gets
	 * stored. Intentionally package-private.
	 */
	static final String ID_COLUMN = TABLE + "_ID";

	/** The column that stores the label. */
	static final String LABEL_COLUMN = "LABEL";

	/** The column that stores the description. */
	static final String DESCRIPTION_COLUMN = "DESCRIPTION";

	/** The {@code OTUSet}-{@code OTU} join table. */
	static final String OTU_SET_OTU_JOIN_TABLE = TABLE + "_" + OTU.TABLE;

	/**
	 * Non-unique label.
	 * <p>
	 * OTU set labels are unique within a particular <code>Study</code>.
	 */
	@Column(name = "LABEL", nullable = false)
	@CheckForNull
	private String label;

	/** The set of {@code OTU}s that this {@code OTUSet} contains. */
	@OneToMany(orphanRemoval = true)
	@org.hibernate.annotations.IndexColumn(name = "POSITION")
	@JoinColumn(name = ID_COLUMN, nullable = false)
	@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	private final List<OTU> otus = newArrayList();

	/** The matrices which reference this OTU set. */
	@OneToMany(mappedBy = "otuSet", orphanRemoval = true)
	@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	private final Set<CharacterStateMatrix> matrices = newHashSet();

	@OneToMany(mappedBy = "otuSet", orphanRemoval = true)
	@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	private final Set<DNASequenceSet> dnaSequenceSets = newHashSet();

	/** The tree sets that reference this OTU set. */
	@OneToMany(mappedBy = "otuSet", orphanRemoval = true)
	@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	private final Set<TreeSet> treeSets = newHashSet();

	/** Free-form description. */
	@Column(name = DESCRIPTION_COLUMN, nullable = true)
	@CheckForNull
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = Study.ID_COLUMN)
	@CheckForNull
	private Study study;

	OTUSet() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		for (final OTU otu : getOTUs()) {
			otu.accept(visitor);
		}
		for (final CharacterStateMatrix matrix : getMatrices()) {
			matrix.accept(visitor);
		}
		for (final TreeSet treeSet : getTreeSets()) {
			treeSet.accept(visitor);
		}
		for (final DNASequenceSet dnaSequenceSet : getDNASequenceSets()) {
			dnaSequenceSet.accept(visitor);
		}
		super.accept(visitor);
		visitor.visit(this);
	}

	/**
	 * Add a {@code DNASequenceSet}.
	 * <p>
	 * Also handles the {@code DNASequenceSet->OTUSet} side of the relationship.
	 * 
	 * @param pPodDNASequenceSet the new {@code DNASequenceSet}
	 * 
	 * @return {@code dnaSequenceSet}
	 */
	public DNASequenceSet addDNASequenceSet(
			final DNASequenceSet dnaSequenceSet) {
		getDNASequenceSets().add(dnaSequenceSet);
		dnaSequenceSet.setOTUSet(this);
		setInNeedOfNewPPodVersionInfo();
		return dnaSequenceSet;
	}

	/**
	 * Add {@code matrix} to this {@code OTUSet}.
	 * <p>
	 * Also handles the {@code CharacterStateMatrix->OTUSet} side of the
	 * relationship.
	 * 
	 * @param matrix matrix we're adding
	 * 
	 * @return {@code matrix}
	 */
	public CharacterStateMatrix addMatrix(final CharacterStateMatrix matrix) {
		checkNotNull(matrix);
		getMatrices().add(matrix);
		matrix.setOTUSet(this);
		setInNeedOfNewPPodVersionInfo();
		return matrix;
	}

	/**
	 * Scaffolding code that does two things:
	 * <ol>
	 * <li>Adds <code>otu</code> to this {@code OTUSet}'s constituent {@code
	 * OTU}s</li>
	 * <li>Adds this {@code OTUSet} to {@code otu}'s {@code OTUSet}s</li>
	 * </ol>
	 * So it takes care of both sides of the <code>OTUSet</code><->
	 * <code>OTU</code> relationship.
	 * <p>
	 * {@code otu} must not be in a detached state.
	 * <p>
	 * {@code otu} must have a label that is unique relative to this OTU set.
	 * 
	 * @param otu see description
	 * 
	 * @return {@code otu}
	 * 
	 * @throws IllegalArgumentException if this OTU set already has an OTU with
	 *             {@code otu}'s label
	 */
	public OTU addOTU(final OTU otu) {
		checkNotNull(otu);
		addOTUWithoutSetOTUsOnChildren(otu);
		setOTUSetOnChildren();
		return otu;
	}

	private OTU addOTUWithoutSetOTUsOnChildren(final OTU otu) {
		checkNotNull(otu);
		final OTU dupNameOTU = findIf(getOTUs(), compose(
				equalTo(otu.getLabel()), OTU.getLabel));
		if (dupNameOTU == null || otu.equals(dupNameOTU)) {

		} else {
			checkArgument(false, "OTUSet labeled '" + getLabel()
									+ "' already has an OTU labeled '"
									+ otu.getLabel() + "'");
		}
		if (getOTUs().add(otu)) {
			otu.setOTUSet(this);
			setInNeedOfNewPPodVersionInfo();
		}
		return otu;
	}

	/**
	 * See {@link Unmarshaller}.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@Override
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		super.afterUnmarshal(u, parent);
		if (parent instanceof Study) {
			// We don't call setStudy(...) since that would reset the pPOD
			// version info, which is not appropriate here. (Even though it
			// doesn't at present
			// make a difference since deserialized OTU sets don't have a
			// reference to a pPodVersionInfo.)
			this.study = (Study) parent;
		}
	}

	/**
	 * Getter.
	 * 
	 * @return the description
	 */
	@XmlAttribute
	@CheckForNull
	public String getDescription() {
		return description;
	}

	@XmlElement(name = "dnaSequenceSet")
	private Set<DNASequenceSet> getDNASequenceSets() {
		return dnaSequenceSets;
	}

	/**
	 * Get an iterator over this {@code OTUSet}'s {@code DNASequenceSets}s.
	 */
	public Iterator<DNASequenceSet> getDNASequenceSetsIterator() {
		return Collections.unmodifiableSet(getDNASequenceSets()).iterator();
	}

	/**
	 * Get the number of {@code DNASequenceSet}s in this {@code OTUSet}.
	 * 
	 * @return the number of {@code DNASequenceSet}s in this {@code OTUSet}
	 */
	public int getDNASequenceSetsSize() {
		return getDNASequenceSets().size();
	}

	/**
	 * Getter. {@code null} when the object is created.
	 * 
	 * @return the label
	 */
	@XmlAttribute
	@Nullable
	public String getLabel() {
		return label;
	}

	@XmlElement(name = "matrix")
	private Set<CharacterStateMatrix> getMatrices() {
		return matrices;
	}

	public Iterator<CharacterStateMatrix> getMatricesIterator() {
		return Collections.unmodifiableSet(getMatrices()).iterator();
	}

	/**
	 * Get the number of {@code CharacterStateMatrix}s in this {@code OTUSet}.
	 * 
	 * @return the number of {@code CharacterStateMatrix}s in this {@code
	 *         OTUSet}
	 */
	public int getMatricesSize() {
		return getMatrices().size();
	}

	/**
	 * Get the OTU at position {@code otuPosition}.
	 * 
	 * @param otuPosition the position we want
	 * 
	 * @return the OTU at position {@code otuPosition}
	 * 
	 * @throws IndexOutOfBoundsException if {@code otuPosition} is out of bounds
	 */
	public OTU getOTU(final int otuPosition) {
		return getOTUs().get(otuPosition);
	}

	@XmlElement(name = "otu")
	private List<OTU> getOTUs() {
		return otus;
	}

	/**
	 * Get the number of OTU's in this {@code OTUSet}.
	 * 
	 * @return the number of OTU's in this {@code OTUSet}
	 */
	public int getOTUsSize() {
		return getOTUs().size();
	}

	/**
	 * Get the study to which this OTU set belongs. Will be {@code null} until
	 * this is added to a {@code Study}.
	 * 
	 * @return the study to which this OTU set belongs
	 */
	@Nullable
	public Study getStudy() {
		return study;
	}

	@XmlElement(name = "treeSet")
	private Set<TreeSet> getTreeSets() {
		return treeSets;
	}

	/**
	 * Get an iterator over this {@code OTUSet}'s {@code TreeSet}s.
	 * 
	 * @return an iterator over this {@code OTUSet}'s {@code TreeSet}s
	 */
	public Iterator<TreeSet> getTreeSetsIterator() {
		return Collections.unmodifiableSet(getTreeSets()).iterator();
	}

	/**
	 * Get the number of {@code TreeSet}s in this {@code OTUSet}.
	 * 
	 * @return the number of {@code TreeSet}s in this {@code OTUSet}
	 */
	public int getTreeSetsSize() {
		return getTreeSets().size();
	}

	public Iterator<OTU> iterator() {
		return Collections.unmodifiableList(getOTUs()).iterator();
	}

	/**
	 * Setter.
	 * 
	 * @param description the description
	 * 
	 * @return this {@code OTUSet}
	 */
	public OTUSet setDescription(@Nullable final String newDescription) {
		if (equal(getDescription(), newDescription)) {

		} else {
			this.description = newDescription;
			setInNeedOfNewPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Set the {@code DNASequenceSet}s. Any sequence sets which are removed as a
	 * result of this operation are returned.
	 * <p>
	 * This method handles both sides of the {@code OTUSet<->DNASequenceSet}
	 * relationship: if a sequence set is added, then this {@code OTUSet} is set
	 * as its {@code OTUSet}, and if a set is deleted, the relationship is
	 * severed.
	 * 
	 * @param newSequenceSets the new sequence sets
	 * 
	 * @return any sequence sets which are removed as a result of this operation
	 *         are returned
	 */
	public Set<DNASequenceSet> setDNASequenceSets(
			final Set<? extends DNASequenceSet> newSequenceSets) {
		checkNotNull(newSequenceSets);

		if (newSequenceSets.equals(getDNASequenceSets())) {
			return Collections.emptySet();
		}

		final Set<DNASequenceSet> removedSequenceSets = newHashSet(getDNASequenceSets());
		removedSequenceSets.removeAll(newSequenceSets);

		for (final DNASequenceSet removedSequenceSet : removedSequenceSets) {
			removedSequenceSet.setOTUSet(null);
		}

		getDNASequenceSets().clear();

		for (final DNASequenceSet newSequenceSet : newSequenceSets) {
			addDNASequenceSet(newSequenceSet);
		}

		setInNeedOfNewPPodVersionInfo();
		return removedSequenceSets;
	}

	/**
	 * Point this {@code OTUSet} and all of its children to a new {@code
	 * pPodVersionInfo}. Only the first call has an effect.
	 * 
	 * @see #unsetAllowPersistAndResetPPodVersionInfo()
	 * 
	 * @return this {@code OTUSet}
	 */
	@Override
	public OTUSet setInNeedOfNewPPodVersionInfo() {
		final Study study = getStudy();
		if (study != null) {
			study.setInNeedOfNewPPodVersionInfo();
		}
		super.setInNeedOfNewPPodVersionInfo();
		return this;
	}

	/**
	 * Set the label of this <code>OTUSet</code>.
	 * 
	 * @param label the label
	 * 
	 * @return this <code>OTUSet</code>
	 */
	public OTUSet setLabel(final String newLabel) {
		checkNotNull(newLabel);
		if (newLabel.equals(getLabel())) {

		} else {
			this.label = newLabel;
			setInNeedOfNewPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Set <code>newMatrices</code> to this <code>OTUSet</code>'s matrices.
	 * 
	 * @param newMatrices new matrices
	 * 
	 * @return any matrices that were removed as a result of this operation
	 */
	public Set<CharacterStateMatrix> setMatrices(
			final Set<? extends CharacterStateMatrix> newMatrices) {
		checkNotNull(newMatrices);

		if (newMatrices.equals(getMatrices())) {
			return Collections.emptySet();
		}

		final Set<CharacterStateMatrix> removedMatrices = newHashSet(getMatrices());
		removedMatrices.removeAll(newMatrices);

		for (final CharacterStateMatrix removedMatrix : removedMatrices) {
			removedMatrix.setOTUSet(null);
		}

		getMatrices().clear();

		for (final CharacterStateMatrix newMatrix : newMatrices) {
			addMatrix(newMatrix);
		}
		setInNeedOfNewPPodVersionInfo();
		return removedMatrices;
	}

	/**
	 * Set this {@code OTUSet}'s {@code OTU}s.
	 * <p>
	 * This {@code OTUSet} makes a copy of {@code newOTUs}.
	 * <p>
	 * If this method is effectively removing any of this sets's original OTUs,
	 * then the {@code OTU->OTUSet} relationship is severed.
	 * 
	 * @param newOTUs the otus to assign to this OTU set
	 * 
	 * @return any {@code OTU}s that were removed as a result of this operation,
	 *         in their original order
	 */
	public List<OTU> setOTUs(final List<? extends OTU> newOTUs) {
		checkNotNull(newOTUs);
		if (newOTUs.equals(getOTUs())) {
			return Collections.emptyList();
		}

		final List<OTU> removedOTUs = newArrayList(getOTUs());
		removedOTUs.removeAll(newOTUs);

		getOTUs().clear();
		for (final OTU otu : newOTUs) {
			addOTUWithoutSetOTUsOnChildren(otu);
		}

		for (final OTU removedOTU : removedOTUs) {
			removedOTU.setOTUSet(null);
		}

		setOTUSetOnChildren();

		setInNeedOfNewPPodVersionInfo();

		return removedOTUs;
	}

	private void setOTUSetOnChildren() {
		// Now let's let everyone know about the new OTUs
		for (final CharacterStateMatrix matrix : getMatrices()) {
			matrix.setOTUSet(this);
		}

		for (final DNASequenceSet dnaSequenceSet : getDNASequenceSets()) {
			dnaSequenceSet.setOTUSet(this);
		}

	}

	protected OTUSet setStudy(final Study study) {
		checkNotNull(study);
		if (study.equals(this.study)) {

		} else {
			this.study = study;
			setInNeedOfNewPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Add {@code treeSet} to this {@code OTUSet}'s tree sets and add this OTU
	 * set to {@code treeSet}.
	 * <p>
	 * All of {@code newTreeSets} must not be in a detached state.
	 * <p>
	 * The {@code TreeSet->OTUSet} relationship will be taken care of, including
	 * severing removed tree sets.
	 * 
	 * @param newTreeSets new tree sets
	 * 
	 * @return any tree sets which were removed as a result of this operation
	 */
	public Set<TreeSet> setTreeSets(final Set<TreeSet> newTreeSets) {
		checkNotNull(newTreeSets);
		if (newTreeSets.equals(getTreeSets())) {
			return Collections.emptySet();
		}
		final Set<TreeSet> removedTreeSets = newHashSet(getTreeSets());
		removedTreeSets.removeAll(newTreeSets);
		for (final TreeSet removedTreeSet : removedTreeSets) {
			removedTreeSet.setOTUSet(null);
		}
		getTreeSets().clear();
		for (final TreeSet treeSet : newTreeSets) {
			addTreeSet(treeSet);
		}
		setInNeedOfNewPPodVersionInfo();
		return removedTreeSets;
	}

	public OTUSet addTreeSet(final TreeSet newTreeSet) {
		checkNotNull(newTreeSet);
		getTreeSets().add(newTreeSet);
		newTreeSet.setOTUSet(this);
		setInNeedOfNewPPodVersionInfo();
		return this;
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
