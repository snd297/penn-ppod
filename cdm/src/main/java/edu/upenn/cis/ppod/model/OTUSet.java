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

import edu.upenn.cis.ppod.imodel.IChild;
import edu.upenn.cis.ppod.imodel.IDNAMatrix;
import edu.upenn.cis.ppod.imodel.IDNASequenceSet;
import edu.upenn.cis.ppod.imodel.ILabeled;
import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IOTUSet;
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
@Table(name = OTUSet.TABLE)
public class OTUSet
		extends UUPPodEntityWithDocId
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

	@OneToMany(
			mappedBy = "parent",
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			targetEntity = StandardMatrix.class)
	private final Set<IStandardMatrix> standardMatrices = newHashSet();

	/** Nullable free-form description. */
	@Column(name = DESCRIPTION_COLUMN, nullable = true)
	@CheckForNull
	private String description;

	@OneToMany(
			mappedBy = "parent",
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			targetEntity = DNAMatrix.class)
	private final Set<IDNAMatrix> dnaMatrices = newHashSet();

	@OneToMany(
			mappedBy = "parent",
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			targetEntity = DNASequenceSet.class)
	private final Set<IDNASequenceSet> dnaSequenceSets = newHashSet();

	/**
	 * Non-unique label.
	 * <p>
	 * OTU set labels are unique within a particular {@code IStudy}
	 */
	@Column(name = "LABEL", nullable = false)
	@CheckForNull
	private String label;

	/** The OTUs in this OTU set. */
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
	@OneToMany(
			mappedBy = "parent",
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			targetEntity = TreeSet.class)
	private final Set<ITreeSet> treeSets = newHashSet();

	/**
	 * Intentionally package-private, to block subclassing outside of this
	 * package.
	 */
	OTUSet() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitOTUSet(this);
		for (final IChild<IOTUSet> child : getChildren()) {
			child.accept(visitor);
		}
		super.accept(visitor);
	}

	/** {@inheritDoc} */
	public void addDNAMatrix(final IDNAMatrix matrix) {
		checkNotNull(matrix);
		if (dnaMatrices.add(matrix)) {
			matrix.setParent(this);
			setInNeedOfNewVersion();
		}
	}

	/** {@inheritDoc} */
	public void addDNASequenceSet(
			final IDNASequenceSet sequenceSet) {
		checkNotNull(sequenceSet);
		if (dnaSequenceSets.add(sequenceSet)) {
			sequenceSet.setParent(this);
			setInNeedOfNewVersion();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalArgumentException if this OTU set already has an OTU with
	 *             {@code otu}'s label
	 */
	public void addOTU(final IOTU otu) {
		checkNotNull(otu);
		addOTUWithoutSetOTUsOnChildren(otu);
		setOTUSetOnChildren();
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

	/** {@inheritDoc} */
	public void addStandardMatrix(
			final IStandardMatrix matrix) {
		checkNotNull(matrix);
		if (standardMatrices.add(matrix)) {
			matrix.setParent(this);
			setInNeedOfNewVersion();
		}
	}

	/** {@inheritDoc} */
	public void addTreeSet(final ITreeSet treeSet) {
		checkNotNull(treeSet);
		if (getTreeSetsModifiable().add(treeSet)) {
			treeSet.setParent(this);
			setInNeedOfNewVersion();
		}
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
	Set<IChild<IOTUSet>> getChildren() {
		final Set<IChild<IOTUSet>> children = newHashSet();
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

	/** {@inheritDoc} */
	public Set<IDNAMatrix> getDNAMatrices() {
		return Collections.unmodifiableSet(dnaMatrices);
	}

	@XmlElement(name = "dnaMatrix")
	protected Set<IDNAMatrix> getDNAMatricesModifiable() {
		return dnaMatrices;
	}

	/** {@inheritDoc} */
	public Set<IDNASequenceSet> getDNASequenceSets() {
		return Collections.unmodifiableSet(dnaSequenceSets);
	}

	@XmlElement(name = "dnaSequenceSet")
	protected Set<IDNASequenceSet> getDNASequenceSetsModifiable() {
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

	/** {@inheritDoc} */
	public Set<IStandardMatrix> getStandardMatrices() {
		return Collections.unmodifiableSet(standardMatrices);
	}

	@XmlElement(name = "matrix")
	protected Set<IStandardMatrix> getStandardMatricesModifiable() {
		return standardMatrices;
	}

	/** {@inheritDoc} */
	public Set<ITreeSet> getTreeSets() {
		return Collections.unmodifiableSet(treeSets);
	}

	@XmlElement(name = "treeSet")
	protected Set<ITreeSet> getTreeSetsModifiable() {
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
	public boolean removeDNASequenceSet(final IDNASequenceSet sequenceSet) {
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
	public boolean removeStandardMatrix(final IStandardMatrix matrix) {
		checkNotNull(matrix);
		if (getStandardMatricesModifiable().remove(matrix)) {
			matrix.setParent(null);
			setInNeedOfNewVersion();
			return true;
		}
		return false;
	}

	public boolean removeTreeSet(final ITreeSet treeSet) {
		checkNotNull(treeSet);
		if (getTreeSetsModifiable().remove(treeSet)) {
			treeSet.setParent(null);
			setInNeedOfNewVersion();
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	public OTUSet setDescription(@CheckForNull final String description) {
		if (equal(getDescription(), description)) {

		} else {
			this.description = description;
			setInNeedOfNewVersion();
		}
		return this;
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
	public OTUSet setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(getLabel())) {

		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
		return this;
	}

	/** {@inheritDoc} */
	public List<IOTU> setOTUs(final List<? extends IOTU> otus) {
		checkNotNull(otus);
		if (otus.equals(getOTUs())) {
			return Collections.emptyList();
		}

		final List<IOTU> removedOTUs = newArrayList(getOTUs());
		removedOTUs.removeAll(otus);

		for (final IOTU removedOTU : removedOTUs) {
			removedOTU.setParent(null);
		}

		getOTUsModifiable().clear();
		for (final IOTU otu : otus) {
			addOTUWithoutSetOTUsOnChildren(otu);
		}

		setOTUSetOnChildren();

		setInNeedOfNewVersion();

		return removedOTUs;
	}

	private void setOTUSetOnChildren() {
		// Now let's let everyone know about the new OTUs
		for (final IChild<IOTUSet> otu : getOTUs()) {
			otu.setParent(this);
		}

		for (final IChild<IOTUSet> matrix : getStandardMatrices()) {
			matrix.setParent(this);
		}

		for (final IChild<IOTUSet> matrix : getDNAMatrices()) {
			matrix.setParent(this);
		}

		for (final IChild<IOTUSet> sequenceSet : getDNASequenceSets()) {
			sequenceSet.setParent(this);
		}

		for (final IChild<IOTUSet> treeSet : getTreeSets()) {
			treeSet.setParent(this);
		}
	}

	/** {@inheritDoc} */
	public void setParent(@CheckForNull final IStudy parent) {
		this.parent = parent;
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
