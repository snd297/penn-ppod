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
import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
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

import edu.upenn.cis.ppod.modelinterfaces.ILabeled;
import edu.upenn.cis.ppod.modelinterfaces.IPPodVersionedWithOTUSet;
import edu.upenn.cis.ppod.modelinterfaces.IPersistentObject;
import edu.upenn.cis.ppod.modelinterfaces.IWithOTUSet;
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

	/** The column that stores the description. */
	public static final String DESCRIPTION_COLUMN = "DESCRIPTION";

	/** The table for this entity. Intentionally package-private. */
	public static final String TABLE = "OTU_SET";

	/**
	 * To be used in the names of foreign keys that point at this table.
	 */
	public static final String JOIN_COLUMN = TABLE + "_"
												+ PersistentObject.ID_COLUMN;

	/** The column that stores the label. */
	public static final String LABEL_COLUMN = "LABEL";

	@OneToMany(mappedBy = "otuSet", cascade = CascadeType.ALL, orphanRemoval = true)
	private final Set<CharacterStateMatrix> characterStateMatrices = newHashSet();

	/** Free-form description. */
	@Column(name = DESCRIPTION_COLUMN, nullable = true)
	@CheckForNull
	private String description;

	@OneToMany(mappedBy = "otuSet", cascade = CascadeType.ALL, orphanRemoval = true)
	private final Set<DNAMatrix> dnaMatrices = newHashSet();

	@OneToMany(mappedBy = "otuSet", cascade = CascadeType.ALL, orphanRemoval = true)
	private final Set<DNASequenceSet> dnaSequenceSets = newHashSet();

	/**
	 * Non-unique label.
	 * <p>
	 * OTU set labels are unique within a particular <code>Study</code>.
	 */
	@Column(name = "LABEL", nullable = false)
	@CheckForNull
	private String label;

	/** The set of {@code OTU}s that this {@code OTUSet} contains. */
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
	@org.hibernate.annotations.IndexColumn(name = "POSITION")
	@JoinColumn(name = JOIN_COLUMN, nullable = false)
	private final List<OTU> otus = newArrayList();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = Study.JOIN_COLUMN)
	@CheckForNull
	private Study study;

	/** The tree sets that reference this OTU set. */
	@OneToMany(mappedBy = "otuSet", cascade = CascadeType.ALL, orphanRemoval = true)
	private final Set<TreeSet> treeSets = newHashSet();

	protected OTUSet() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		for (final IPersistentObject child : getChildren()) {
			child.accept(visitor);
		}
		super.accept(visitor);
		visitor.visit(this);
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
	public CharacterStateMatrix addCharacterStateMatrix(
			final CharacterStateMatrix matrix) {
		checkNotNull(matrix);
		getCharacterStateMatrices().add(matrix);
		matrix.setOTUSet(this);
		setInNeedOfNewPPodVersionInfo();
		return matrix;
	}

	/**
	 * Add a {@code DNASequenceSet}.
	 * <p>
	 * Also handles the {@code DNASequenceSet->OTUSet} side of the relationship.
	 * 
	 * @param dnaSequenceSet the new {@code DNASequenceSet}
	 * 
	 * @return {@code dnaSequenceSet}
	 */
	public DNASequenceSet addDNASequenceSet(
			final DNASequenceSet dnaSequenceSet) {
		checkNotNull(dnaSequenceSet);
		getDNASequenceSets().add(dnaSequenceSet);
		dnaSequenceSet.setOTUSet(this);
		setInNeedOfNewPPodVersionInfo();
		return dnaSequenceSet;
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
	 * Assumes {@code otu} is in a detached state.
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
				equalTo(otu.getLabel()), ILabeled.getLabel));
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

	public TreeSet addTreeSet(final TreeSet newTreeSet) {
		checkNotNull(newTreeSet);
		getTreeSets().add(newTreeSet);
		newTreeSet.setOTUSet(this);
		setInNeedOfNewPPodVersionInfo();
		return newTreeSet;
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

	@XmlElement(name = "matrix")
	protected Set<CharacterStateMatrix> getCharacterStateMatrices() {
		return characterStateMatrices;
	}

	public Iterator<CharacterStateMatrix> getCharacterStateMatricesIterator() {
		return Collections.unmodifiableSet(getCharacterStateMatrices())
				.iterator();
	}

	/**
	 * Get the number of {@code CharacterStateMatrix}s in this {@code OTUSet}.
	 * 
	 * @return the number of {@code CharacterStateMatrix}s in this {@code
	 *         OTUSet}
	 */
	public int getCharacterStateMatricesSize() {
		return getCharacterStateMatrices().size();
	}

	protected Set<IPPodVersionedWithOTUSet> getChildren() {
		final Set<IPPodVersionedWithOTUSet> children = newHashSet();
		children.addAll(getOTUs());
		children.addAll(getCharacterStateMatrices());
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
	@CheckForNull
	public String getDescription() {
		return description;
	}

	protected Set<DNAMatrix> getDNAMatrices() {
		return dnaMatrices;
	}

	public Iterator<DNAMatrix> getDNAMatricesIterator() {
		return Collections.unmodifiableSet(getDNAMatrices()).iterator();
	}

	@XmlElement(name = "dnaSequenceSet")
	protected Set<DNASequenceSet> getDNASequenceSets() {
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

	/**
	 * Get the OTU at position {@code otuPosition}.
	 * 
	 * @param otuPosition the position we want
	 * 
	 * @return the OTU at position {@code otuPosition}
	 * 
	 * @throws IndexOutOfBoundsException if {@code otuPosition} is out of bounds
	 */
	public OTU getOTU(@Nonnegative final int otuPosition) {
		return getOTUs().get(otuPosition);
	}

	@XmlElement(name = "otu")
	protected List<OTU> getOTUs() {
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
	protected Set<TreeSet> getTreeSets() {
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

	public boolean removeDNASequenceSet(
			final DNASequenceSet dnaSequenceSet) {
		checkNotNull(dnaSequenceSet);
		if (getDNASequenceSets().remove(dnaSequenceSet)) {
			dnaSequenceSet.setOTUSet(null);
			setInNeedOfNewPPodVersionInfo();
			return true;
		}
		return false;
	}

	/**
	 * Set <code>newMatrices</code> to this <code>OTUSet</code>'s matrices.
	 * 
	 * @param newMatrices new matrices
	 * 
	 * @return any matrices that were removed as a result of this operation
	 */
	public Set<CharacterStateMatrix> setCharacterStateMatrices(
			final Set<? extends CharacterStateMatrix> newMatrices) {
		checkNotNull(newMatrices);

		if (newMatrices.equals(getCharacterStateMatrices())) {
			return Collections.emptySet();
		}

		final Set<CharacterStateMatrix> removedMatrices = newHashSet(getCharacterStateMatrices());
		removedMatrices.removeAll(newMatrices);

		for (final CharacterStateMatrix removedMatrix : removedMatrices) {
			removedMatrix.setOTUSet(null);
		}

		getCharacterStateMatrices().clear();

		for (final CharacterStateMatrix newMatrix : newMatrices) {
			addCharacterStateMatrix(newMatrix);
		}
		setInNeedOfNewPPodVersionInfo();
		return removedMatrices;
	}

	/**
	 * Setter.
	 * 
	 * @param description the description
	 * 
	 * @return this {@code OTUSet}
	 */
	public OTUSet setDescription(@CheckForNull final String newDescription) {
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
		for (final IWithOTUSet child : getChildren()) {
			child.setOTUSet(this);
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
