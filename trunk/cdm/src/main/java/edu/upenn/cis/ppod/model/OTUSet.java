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
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.util.PPodIterables.equalTo;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;
import static edu.upenn.cis.ppod.util.UPennCisPPodUtil.nullSafeEquals;

import java.util.Collections;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.annotations.Cascade;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A set of {@link OTU}s. The relationship between {@code OTU} and {@code
 * OTUSet} is many-to-many.
 * 
 * @author Shirley Cohen
 * @author Sam Donnelly
 */
@Entity
@Table(name = OTUSet.TABLE)
public class OTUSet extends UUPPodEntityWXmlId {

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
	@org.hibernate.annotations.Index(name = "IDX_LABEL")
	private String label;

	/** The set of {@code OTU}s that this {@code OTUSet} contains. */
	@ManyToMany
	@JoinTable(joinColumns = { @JoinColumn(name = ID_COLUMN) }, inverseJoinColumns = { @JoinColumn(name = OTU.ID_COLUMN) })
	private final Set<OTU> otus = newHashSet();

	/** The matrices which reference this OTU set. */
	@OneToMany(mappedBy = "otuSet")
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	private final Set<CharacterStateMatrix> matrices = newHashSet();

	/** The tree sets that reference this OTU set. */
	@OneToMany(mappedBy = "otuSet")
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	private final Set<TreeSet> treeSets = newHashSet();

	/** Free-form description. */
	@Column(name = "DESCRIPTION", nullable = true)
	@CheckForNull
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = Study.ID_COLUMN)
	@CheckForNull
	private Study study;

	OTUSet() {}

	@Override
	public OTUSet accept(final IVisitor visitor) {
		visitor.visit(this);
		for (final OTU otu : getOTUs()) {
			otu.accept(visitor);
		}
		for (final CharacterStateMatrix matrix : getMatrices()) {
			matrix.accept(visitor);
		}
		for (final TreeSet treeSet : getTreeSets()) {
			treeSet.accept(visitor);
		}
		super.accept(visitor);
		return this;
	}

	/**
	 * Add <code>matrix</code> to this <code>OTUSet</code>'s matrices.
	 * 
	 * @param matrix to be added
	 * 
	 * @return {@code matrix}
	 */
	public ICharacterStateMatrix addMatrix(final CharacterStateMatrix matrix) {
		checkNotNull(matrix);
		if (matrices.add(matrix)) {
			matrix.setOTUSet(this);
			resetPPodVersionInfo();
		}
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
	private OTU addOTU(final OTU otu) {
		checkNotNull(otu);
		final OTU dupNameOTU = findIf(getOTUs(), equalTo(otu.getLabel(),
				OTU.getLabel));
		if (dupNameOTU == null || otu.equals(dupNameOTU)) {

		} else {
			checkArgument(false, "OTUSet labeled '" + getLabel()
					+ "' already has an OTU labeled '" + otu.getLabel() + "'");
		}
		if (otus.add(otu)) {
			otu.addOTUSet(this);
			resetPPodVersionInfo();
		}
		return otu;
	}

	/**
	 * Add {@code treeSet} to this {@code OTUSet}'s tree sets and add this OTU
	 * set to {@code treeSet}.
	 * <p>
	 * {@code treeSet} must not be in a detached state.
	 * 
	 * @param treeSet to be added
	 * 
	 * @return {@code treeSet}
	 */
	public TreeSet addTreeSet(final TreeSet treeSet) {
		checkNotNull(treeSet);
		if (treeSets.add(treeSet)) {
			treeSet.setOTUSet(this);
			resetPPodVersionInfo();
		}
		return treeSet;
	}

	/**
	 * See {@link Unmarshaller}.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
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
	 * Get an unmodifiable view of the matrices which refer to this OTU set.
	 * 
	 * @return an unmodifiable view of the matrices which refer to this OTU set
	 */
	public Set<CharacterStateMatrix> getMatrices() {
		return Collections.unmodifiableSet(matrices);
	}

	@XmlElement(name = "matrix")
	@SuppressWarnings("unused")
	private Set<CharacterStateMatrix> getMatricesMutable() {
		return matrices;
	}

	/**
	 * Get an unmodifiable view of the <code>OTU</code>s that comprise this
	 * <code>OTUSet</code>.
	 * 
	 * @return an unmodifiable view of the <code>OTU</code>s that comprise this
	 *         <code>OTUSet</code>.
	 */
	public Set<OTU> getOTUs() {
		return Collections.unmodifiableSet(otus);
	}

	@XmlElement(name = "otu")
	private Set<OTU> getOTUsMutable() {
		return otus;
	}

	/**
	 * Get the study to which this OTU set belongs. Will be {@code null} until
	 * this is added to a {@code Study}.
	 * 
	 * @return the study to which this OTU set belongs
	 */
	@CheckForNull
	public Study getStudy() {
		return study;
	}

	/**
	 * Get the tree sets that are composed of this OTU set.
	 * 
	 * @return the tree sets that are composed of this OTU set
	 */
	public Set<TreeSet> getTreeSets() {
		return Collections.unmodifiableSet(treeSets);
	}

	@XmlElement(name = "treeSet")
	@SuppressWarnings("unused")
	private Set<TreeSet> getTreeSetsMutable() {
		return treeSets;
	}

	/**
	 * Remove {@code matrix} from this OTU set's matrices. Also takes care of
	 * the matrix side of the relationship.
	 * 
	 * @param matrix to be removed
	 * 
	 * @return {@code true} if {@code matrix} belonged to this OTU set and was
	 *         removed
	 */
	public boolean removeMatrix(final CharacterStateMatrix matrix) {
		checkNotNull(matrix);
		if (matrices.remove(matrix)) {
			matrix.setOTUSet(null);
			resetPPodVersionInfo();
			return true;
		}
		return false;
	}

	/**
	 * If any of {@link #getOTUs()} are not in {@code newOTUs} then this {@code
	 * OTUSet} is removed from those {@code getOTUs()}. That is, if this method
	 * is effectively <em>removing</em> any of this sets's original OTUs, then
	 * the {@code OTU->OTUSet} relationship is severed.
	 * 
	 * @param newOTUs
	 * 
	 * @return this
	 */
	public OTUSet setOTUs(final Set<OTU> newOTUs) {
		checkNotNull(newOTUs);
		if (newOTUs.equals(this.otus)) {
			return this;
		}
		for (final OTU otu : getOTUs()) {
			if (!newOTUs.contains(otu)) {
				otu.removeOTUSet(this);
			}
		}
		getOTUsMutable().clear();
		for (final OTU otu : newOTUs) {
			addOTU(otu);
		}
		resetPPodVersionInfo();
		return this;
	}

	/**
	 * Remove {@code matrix} from this OTU set's matrices. Also takes care of
	 * the tree set side of the relationship.
	 * 
	 * @param treeSet to be removed
	 * 
	 * @return {@code true} if {@code matrix} belonged to this OTU set and was
	 *         removed
	 */
	public boolean removeTreeSet(final TreeSet treeSet) {
		checkNotNull(treeSet);
		if (treeSets.remove(treeSet)) {
			treeSet.setOTUSet(null);
			resetPPodVersionInfo();
			return true;
		}
		return false;
	}

	/**
	 * Point this {@code OTUSet} and all of its {@code PhyloCharMatrix}'s to a
	 * new {@code pPodVersionInfo}. Only the first call has an effect.
	 * <p>
	 * If {@link #getAllowPersist()} {@code == true} then calls to this method
	 * does nothing and returns.
	 * 
	 * @see #unsetAllowPersistAndResetPPodVersionInfo()
	 * 
	 * @return this {@code OTUSet}
	 * 
	 * @throws IllegalArgumentException if {@code
	 *             pPodVersionInfo.getPPodVersion()} is greater than {@code
	 *             this.getPPodVersion().getPPodVersion()}
	 */
	@Override
	public OTUSet resetPPodVersionInfo() {
		if (getStudy() != null) {
			getStudy().resetPPodVersionInfo();
		}
		super.resetPPodVersionInfo();
		return this;
	}

	/**
	 * Setter.
	 * 
	 * @param description the description
	 * 
	 * @return this {@code OTUSet}
	 */
	public OTUSet setDescription(@Nullable final String description) {
		if (nullSafeEquals(getDescription(), description)) {

		} else {
			this.description = description;
			resetPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Set the label of this <code>OTUSet</code>.
	 * 
	 * @param label the label
	 * 
	 * @return this <code>OTUSet</code>
	 */
	public OTUSet setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(this.label)) {

		} else {
			this.label = label;
			resetPPodVersionInfo();
		}
		return this;
	}

	OTUSet setStudy(final Study study) {
		checkNotNull(study);
		if (study.equals(this.study)) {

		} else {
			this.study = study;
			resetPPodVersionInfo();
		}
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
