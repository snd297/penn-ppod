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
import edu.upenn.cis.ppod.modelinterfaces.IPersistentObject;
import edu.upenn.cis.ppod.modelinterfaces.IVersionedWithOTUSet;
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
public class OTUSet extends UUPPodEntityWXmlId {

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

	/**
	 * Intentionally package-private, to block subclassing outside of this
	 * package.
	 */
	OTUSet() {}

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
		if (characterStateMatrices.add(matrix)) {
			matrix.setOTUSet(this);
			setInNeedOfNewVersion();
		}
		return matrix;
	}

	public DNAMatrix addDNAMatrix(final DNAMatrix matrix) {
		checkNotNull(matrix);
		if (dnaMatrices.add(matrix)) {
			matrix.setOTUSet(this);
			setInNeedOfNewVersion();
		}
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
			final DNASequenceSet sequenceSet) {
		checkNotNull(sequenceSet);
		if (dnaSequenceSets.add(sequenceSet)) {
			sequenceSet.setOTUSet(this);
			setInNeedOfNewVersion();
		}
		return sequenceSet;
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
		final OTU dupNameOTU = findIf(getOTUsModifiable(), compose(
				equalTo(otu.getLabel()), ILabeled.getLabel));
		if (dupNameOTU == null || otu.equals(dupNameOTU)) {

		} else {
			checkArgument(false, "OTUSet labeled '" + getLabel()
									+ "' already has an OTU labeled '"
									+ otu.getLabel() + "'");
		}
		if (getOTUsModifiable().add(otu)) {
			otu.setOTUSet(this);
			setInNeedOfNewVersion();
		}
		return otu;
	}

	public TreeSet addTreeSet(final TreeSet treeSet) {
		checkNotNull(treeSet);
		if (getTreeSetsModifiable().add(treeSet)) {
			treeSet.setOTUSet(this);
			setInNeedOfNewVersion();
		}
		return treeSet;
	}

	/**
	 * See {@link Unmarshaller}.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@Override
	public void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent) {
		super.afterUnmarshal(u, parent);
		if (parent instanceof Study) {
			// We don't call setStudy(...) since that would reset the pPOD
			// version info, which is not appropriate here. (Even though it
			// doesn't make a difference since deserialized OTU sets don't have
			// a reference to a versionInfo. But it seems more proper to do
			// this way.)
			this.study = (Study) parent;
		}
	}

	public Set<CharacterStateMatrix> getCharacterStateMatrices() {
		return Collections.unmodifiableSet(characterStateMatrices);
	}

	@XmlElement(name = "matrix")
	protected Set<CharacterStateMatrix> getCharacterStateMatricesModifiable() {
		return characterStateMatrices;
	}

	protected Set<IVersionedWithOTUSet> getChildren() {
		final Set<IVersionedWithOTUSet> children = newHashSet();
		children.addAll(getOTUs());
		children.addAll(getCharacterStateMatrices());
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
	@CheckForNull
	public String getDescription() {
		return description;
	}

	public Set<DNAMatrix> getDNAMatrices() {
		return Collections.unmodifiableSet(dnaMatrices);
	}

	@XmlElement(name = "dnaMatrix")
	protected Set<DNAMatrix> getDNAMatricesModifiable() {
		return dnaMatrices;
	}

	public Set<DNASequenceSet> getDNASequenceSets() {
		return Collections.unmodifiableSet(dnaSequenceSets);
	}

	@XmlElement(name = "dnaSequenceSet")
	protected Set<DNASequenceSet> getDNASequenceSetsModifiable() {
		return dnaSequenceSets;
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
		return getOTUsModifiable().get(otuPosition);
	}

	public List<OTU> getOTUs() {
		return Collections.unmodifiableList(otus);
	}

	@XmlElement(name = "otu")
	protected List<OTU> getOTUsModifiable() {
		return otus;
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

	public Set<TreeSet> getTreeSets() {
		return Collections.unmodifiableSet(treeSets);
	}

	@XmlElement(name = "treeSet")
	protected Set<TreeSet> getTreeSetsModifiable() {
		return treeSets;
	}

	public boolean removeDNASequenceSet(
			final DNASequenceSet dnaSequenceSet) {
		checkNotNull(dnaSequenceSet);
		if (getDNASequenceSetsModifiable().remove(dnaSequenceSet)) {
			dnaSequenceSet.setOTUSet(null);
			setInNeedOfNewVersion();
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

		if (newMatrices.equals(getCharacterStateMatricesModifiable())) {
			return Collections.emptySet();
		}

		final Set<CharacterStateMatrix> removedMatrices = newHashSet(getCharacterStateMatricesModifiable());
		removedMatrices.removeAll(newMatrices);

		for (final CharacterStateMatrix removedMatrix : removedMatrices) {
			removedMatrix.setOTUSet(null);
		}

		getCharacterStateMatricesModifiable().clear();

		for (final CharacterStateMatrix newMatrix : newMatrices) {
			addCharacterStateMatrix(newMatrix);
		}
		setInNeedOfNewVersion();
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
			setInNeedOfNewVersion();
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

		if (newSequenceSets.equals(getDNASequenceSetsModifiable())) {
			return Collections.emptySet();
		}

		final Set<DNASequenceSet> removedSequenceSets = newHashSet(getDNASequenceSetsModifiable());
		removedSequenceSets.removeAll(newSequenceSets);

		for (final DNASequenceSet removedSequenceSet : removedSequenceSets) {
			removedSequenceSet.setOTUSet(null);
		}

		getDNASequenceSetsModifiable().clear();

		for (final DNASequenceSet newSequenceSet : newSequenceSets) {
			addDNASequenceSet(newSequenceSet);
		}

		setInNeedOfNewVersion();
		return removedSequenceSets;
	}

	/**
	 * Point this {@code OTUSet} and all of its children to a new {@code
	 * versionInfo}. Only the first call has an effect.
	 * 
	 * @return this {@code OTUSet}
	 */
	@Override
	public OTUSet setInNeedOfNewVersion() {
		final Study study = getStudy();
		if (study != null) {
			study.setInNeedOfNewVersion();
		}
		super.setInNeedOfNewVersion();
		return this;
	}

	/**
	 * Set the label of this <code>OTUSet</code>.
	 * 
	 * @param label the label
	 * 
	 * @return this
	 */
	public OTUSet setLabel(final String newLabel) {
		checkNotNull(newLabel);
		if (newLabel.equals(getLabel())) {

		} else {
			this.label = newLabel;
			setInNeedOfNewVersion();
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
		if (newOTUs.equals(getOTUsModifiable())) {
			return Collections.emptyList();
		}

		final List<OTU> removedOTUs = newArrayList(getOTUsModifiable());
		removedOTUs.removeAll(newOTUs);

		for (final OTU removedOTU : removedOTUs) {
			removedOTU.setOTUSet(null);
		}

		getOTUsModifiable().clear();
		for (final OTU otu : newOTUs) {
			addOTUWithoutSetOTUsOnChildren(otu);
		}

		setOTUSetOnChildren();

		setInNeedOfNewVersion();

		return removedOTUs;
	}

	private void setOTUSetOnChildren() {
		// Now let's let everyone know about the new OTUs
		for (final OTU otu : getOTUs()) {
			otu.setOTUSet(this);
		}

		for (final Matrix<?> matrix : getCharacterStateMatrices()) {
			matrix.setOTUSet(this);
		}

		for (final Matrix<?> matrix : getDNAMatrices()) {
			matrix.setOTUSet(this);
		}

		for (final SequenceSet<?> sequenceSet : getDNASequenceSets()) {
			sequenceSet.setOTUSet(this);
		}

		for (final TreeSet treeSet : getTreeSets()) {
			treeSet.setOTUSet(this);
		}
	}

	protected OTUSet setStudy(final Study study) {
		checkNotNull(study);
		if (study.equals(this.study)) {

		} else {
			this.study = study;
			setInNeedOfNewVersion();
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
	 * @return this
	 */
	public Set<TreeSet> setTreeSets(final Set<TreeSet> newTreeSets) {
		checkNotNull(newTreeSets);
		if (newTreeSets.equals(getTreeSetsModifiable())) {
			return Collections.emptySet();
		}
		final Set<TreeSet> removedTreeSets = newHashSet(getTreeSets());
		removedTreeSets.removeAll(newTreeSets);
		for (final TreeSet removedTreeSet : removedTreeSets) {
			removedTreeSet.setOTUSet(null);
		}

		getTreeSetsModifiable().clear();
		for (final TreeSet treeSet : newTreeSets) {
			addTreeSet(treeSet);
		}
		setInNeedOfNewVersion();
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
