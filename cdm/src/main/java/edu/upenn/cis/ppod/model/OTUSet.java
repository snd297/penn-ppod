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
import javax.annotation.Nullable;
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

import edu.upenn.cis.ppod.modelinterfaces.ILabeled;
import edu.upenn.cis.ppod.modelinterfaces.IOTU;
import edu.upenn.cis.ppod.modelinterfaces.IOTUSet;
import edu.upenn.cis.ppod.modelinterfaces.IOTUSetChild;
import edu.upenn.cis.ppod.modelinterfaces.IStudy;
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
@Table(name = OTUSet.TABLE)
public class OTUSet
		extends UUPPodEntityWithXmlId
		implements IOTUSet {

	public static class Adapter extends XmlAdapter<OTUSet, IOTUSet> {

		@Override
		public OTUSet marshal(final IOTUSet otuSet) {
			return (OTUSet) otuSet;
		}

		@Override
		public IOTUSet unmarshal(final OTUSet otuSet) {
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

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL,
			orphanRemoval = true)
	private final Set<StandardMatrix> standardMatrices = newHashSet();

	/** Nullable free-form description. */
	@Column(name = DESCRIPTION_COLUMN, nullable = true)
	@CheckForNull
	private String description;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL,
			orphanRemoval = true, targetEntity = DNAMatrix.class)
	private final Set<IDNAMatrix> dnaMatrices = newHashSet();

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL,
			orphanRemoval = true)
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
	@OneToMany(
			orphanRemoval = true,
			cascade = CascadeType.ALL,
			targetEntity = OTU.class)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = JOIN_COLUMN, nullable = false)
	private final List<IOTU> otus = newArrayList();

	@CheckForNull
	@ManyToOne(
			fetch = FetchType.LAZY,
			optional = false,
			targetEntity = Study.class)
	@JoinColumn(name = Study.JOIN_COLUMN)
	private IStudy parent;

	/** The tree sets that reference this OTU set. */
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL,
			orphanRemoval = true)
	private final Set<TreeSet> treeSets = newHashSet();

	/**
	 * Intentionally package-private, to block subclassing outside of this
	 * package.
	 */
	OTUSet() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitOTUSet(this);
		for (final IOTUSetChild child : getChildren()) {
			child.accept(visitor);
		}
		super.accept(visitor);
	}

	/**
	 * Add a {@code DNAMatrix} to this {@code OTUSet}.
	 * 
	 * @param matrix to be added to this {@code OTUSet}
	 * 
	 * @return {@code matrix}
	 */
	public IDNAMatrix addDNAMatrix(final IDNAMatrix matrix) {
		checkNotNull(matrix);
		if (dnaMatrices.add(matrix)) {
			matrix.setParent(this);
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
			sequenceSet.setParent(this);
			setInNeedOfNewVersion();
		}
		return sequenceSet;
	}

	/**
	 * Scaffolding code that does two things:
	 * <ol>
	 * <li>Adds <code>otu</code> to this {@code OTUSet}'s constituent
	 * {@code OTU}s</li>
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
	public IOTU addOTU(final IOTU otu) {
		checkNotNull(otu);
		addOTUWithoutSetOTUsOnChildren(otu);
		setOTUSetOnChildren();
		return otu;
	}

	private IOTU addOTUWithoutSetOTUsOnChildren(final IOTU otu) {
		checkNotNull(otu);
		final IOTU dupNameOTU = findIf(getOTUs(),
				compose(
						equalTo(
								otu.getLabel()),
								ILabeled.getLabel));
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
		return otu;
	}

	/**
	 * Add {@code matrix} to this {@code OTUSet}.
	 * <p>
	 * Also handles the {@code StandardMatrix->OTUSet} side of the relationship.
	 * 
	 * @param matrix matrix we're adding
	 * 
	 * @return {@code matrix}
	 */
	public StandardMatrix addStandardMatrix(
			final StandardMatrix matrix) {
		checkNotNull(matrix);
		if (standardMatrices.add(matrix)) {
			matrix.setParent(this);
			setInNeedOfNewVersion();
		}
		return matrix;
	}

	/**
	 * Add a tree set to this OTU set.
	 * <p>
	 * Also handles the {@code TreeSet->OTUSet} side of the relationship.
	 * 
	 * @param treeSet to be added
	 * @return {@code treeSet}
	 */
	public TreeSet addTreeSet(final TreeSet treeSet) {
		checkNotNull(treeSet);
		if (getTreeSetsModifiable().add(treeSet)) {
			treeSet.setParent(this);
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
	public void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent) {
		checkNotNull(parent);
		this.parent = (Study) parent;
	}

	@VisibleForTesting
	Set<IOTUSetChild> getChildren() {
		final Set<IOTUSetChild> children = newHashSet();
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
	@CheckForNull
	public String getDescription() {
		return description;
	}

	public Set<IDNAMatrix> getDNAMatrices() {
		return Collections.unmodifiableSet(dnaMatrices);
	}

	@XmlElement(name = "dnaMatrix")
	protected Set<IDNAMatrix> getDNAMatricesModifiable() {
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
	 * Get the {@code OTU}s that make up this {@code OTUSet}.
	 * 
	 * @return the {@code OTU}s that make up this {@code OTUSet}
	 */
	public List<IOTU> getOTUs() {
		return Collections.unmodifiableList(otus);
	}

	/**
	 * Get a modifiable reference to this the otus.
	 * 
	 * @return a modifiable reference to this the otus.
	 */
	@XmlElement(name = "otu")
	protected List<IOTU> getOTUsModifiable() {
		return otus;
	}

	/**
	 * Get the study to which this OTU set belongs. Will be {@code null} when
	 * this OTU set does not belong to a {@code Study}.
	 * 
	 * @return the study to which this OTU set belongs
	 */
	@Nullable
	public IStudy getParent() {
		return parent;
	}

	public Set<StandardMatrix> getStandardMatrices() {
		return Collections.unmodifiableSet(standardMatrices);
	}

	@XmlElement(name = "matrix")
	protected Set<StandardMatrix> getStandardMatricesModifiable() {
		return standardMatrices;
	}

	/**
	 * Get the {@code TreeSet}s contained in this {@code OTUSet}.
	 * 
	 * @return the {@code TreeSet}s contained in this {@code OTUSet}
	 */
	public Set<TreeSet> getTreeSets() {
		return Collections.unmodifiableSet(treeSets);
	}

	@XmlElement(name = "treeSet")
	protected Set<TreeSet> getTreeSetsModifiable() {
		return treeSets;
	}

	/**
	 * Remove {@code matrix} from this OTU set.
	 * 
	 * @param matrix to be removed
	 * 
	 * @return {@code true} if this OTU set contained the specified matrix,
	 *         {@code false} otherwise
	 */
	public boolean removeDNAMatrix(final IDNAMatrix matrix) {
		checkNotNull(matrix);
		if (getDNAMatricesModifiable().remove(matrix)) {
			matrix.setParent(null);
			setInNeedOfNewVersion();
			return true;
		}
		return false;
	}

	/**
	 * Remove {@code sequenceSet} from this OTU set.
	 * 
	 * @param sequenceSet to be removed
	 * 
	 * @return {@code true} if this OTU set contained the specified sequence
	 *         set, {@code false} otherwise
	 */
	public boolean removeDNASequenceSet(final DNASequenceSet sequenceSet) {
		checkNotNull(sequenceSet);
		if (getDNASequenceSetsModifiable().remove(sequenceSet)) {
			sequenceSet.setParent(null);
			setInNeedOfNewVersion();
			return true;
		}
		return false;
	}

	/**
	 * Remove {@code matrix} from this OTU set.
	 * 
	 * @param matrix to be removed
	 * 
	 * @return {@code true} if this OTU set contained the specified matrix,
	 *         {@code false} otherwise
	 */
	public boolean removeStandardMatrix(final StandardMatrix matrix) {
		checkNotNull(matrix);
		if (getStandardMatricesModifiable().remove(matrix)) {
			matrix.setParent(null);
			setInNeedOfNewVersion();
			return true;
		}
		return false;
	}

	public boolean removeTreeSet(final TreeSet treeSet) {
		checkNotNull(treeSet);
		if (getTreeSetsModifiable().remove(treeSet)) {
			treeSet.setParent(null);
			setInNeedOfNewVersion();
			return true;
		}
		return false;
	}

	/**
	 * Setter.
	 * 
	 * @param description the description
	 * 
	 * @return this {@code OTUSet}
	 */
	public OTUSet setDescription(@CheckForNull final String description) {
		if (equal(getDescription(), description)) {

		} else {
			this.description = description;
			setInNeedOfNewVersion();
		}
		return this;
	}

	/**
	 * Point this {@code OTUSet} and all of its children to a new
	 * {@code versionInfo}. Only the first call has an effect.
	 * 
	 * @return this {@code OTUSet}
	 */
	@Override
	public OTUSet setInNeedOfNewVersion() {
		final IStudy study = getParent();
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
	public OTUSet setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(getLabel())) {

		} else {
			this.label = label;
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
	public List<IOTU> setOTUs(final List<? extends IOTU> newOTUs) {
		checkNotNull(newOTUs);
		if (newOTUs.equals(getOTUs())) {
			return Collections.emptyList();
		}

		final List<IOTU> removedOTUs = newArrayList(getOTUs());
		removedOTUs.removeAll(newOTUs);

		for (final IOTU removedOTU : removedOTUs) {
			removedOTU.setParent(null);
		}

		getOTUsModifiable().clear();
		for (final IOTU otu : newOTUs) {
			addOTUWithoutSetOTUsOnChildren(otu);
		}

		setOTUSetOnChildren();

		setInNeedOfNewVersion();

		return removedOTUs;
	}

	private void setOTUSetOnChildren() {
		// Now let's let everyone know about the new OTUs
		for (final IOTU otu : getOTUs()) {
			otu.setParent(this);
		}

		for (final Matrix<?> matrix : getStandardMatrices()) {
			matrix.setParent(this);
		}

		for (final IMatrix<?> matrix : getDNAMatrices()) {
			matrix.setParent(this);
		}

		for (final SequenceSet<?> sequenceSet : getDNASequenceSets()) {
			sequenceSet.setParent(this);
		}

		for (final TreeSet treeSet : getTreeSets()) {
			treeSet.setParent(this);
		}
	}

	/** {@inheritDoc} */
	public OTUSet setParent(@CheckForNull final IStudy parent) {
		this.parent = parent;
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
