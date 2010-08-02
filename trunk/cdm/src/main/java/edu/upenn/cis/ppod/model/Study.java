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
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import edu.upenn.cis.ppod.services.ppodentity.IOTUSetCentricEntities;
import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.PPodEntitiesUtil;

/**
 * A collection of work - inspired by a Mesquite project - sets of OTU sets and,
 * through the OTU sets, matrices and tree sets.
 * 
 * @author Sam Donnelly
 */
@XmlRootElement
@Entity
@Table(name = Study.TABLE)
public class Study extends UUPPodEntity implements IOTUSetCentricEntities {

	/** The table name for this entity. */
	public static final String TABLE = "STUDY";

	/** To be used when referring to this entity in foreign keys. */
	public static final String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	static final String LABEL_COLUMN = "LABEL";

	@Column(name = LABEL_COLUMN, nullable = false)
	private String label;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL,
			orphanRemoval = true)
	private final Set<OTUSet> otuSets = newHashSet();

	@Transient
	private final Set<AttachmentNamespace> attachmentNamespaces = newHashSet();

	@Transient
	private final Set<AttachmentType> attachmentTypes = newHashSet();

	Study() {}

	@Override
	public void accept(final IVisitor visitor) {
		visitor.visitStudy(this);
		for (final OTUSet otuSet : getOTUSets()) {
			otuSet.accept(visitor);
		}
	}

	public OTUSet addOTUSet(final OTUSet otuSet) {
		checkNotNull(otuSet);
		if (getOTUSets().contains(otuSet)) {

		} else {
			otuSets.add(otuSet);
			otuSet.setParent(this);
			setInNeedOfNewVersion();
		}
		return otuSet;
	}

	/**
	 * We gather up all of the attachment info of this study, its matrices, OTU
	 * sets, and trees and add them to the {@code studyWide*}s.
	 * 
	 * See {@link Marshaller}.
	 * 
	 * @param m see {@code Marshaller}
	 * @return see {@code Marshaller}
	 */
	@Override
	public boolean beforeMarshal(final Marshaller m) {
		super.beforeMarshal(m);
		if (attachmentNamespaces.size() == 0) {
			PPodEntitiesUtil.extractAttachmentInfoFromAttachee(
					attachmentNamespaces, attachmentTypes,
					this);
			PPodEntitiesUtil.extractAttachmentInfoFromPPodEntities(
					attachmentNamespaces, attachmentTypes,
					this);
		}
		return true;
	}

	/**
	 * Get the label.
	 * 
	 * @return the label
	 */
	@XmlAttribute
	public String getLabel() {
		return label;
	}

	public Set<OTUSet> getOTUSets() {
		return Collections.unmodifiableSet(otuSets);
	}

	@XmlElement(name = "otuSet")
	protected Set<OTUSet> getOTUSetsModifiable() {
		return otuSets;
	}

	@XmlElement(name = "studyWideAttachmentNamespace")
	protected Set<AttachmentNamespace> getStudyWideAttachmentNamespacesModifiable() {
		return attachmentNamespaces;
	}

	@XmlElement(name = "studyWideAttachmentType")
	protected Set<AttachmentType> getStudyWideAttachmentTypes() {
		return attachmentTypes;
	}

	/**
	 * Remove an OTU set from this Study.
	 * 
	 * @param otuSet to be removed
	 * 
	 * @return this
	 */
	public Study removeOTUSet(final OTUSet otuSet) {
		if (otuSets.remove(otuSet)) {
			otuSet.setParent(null);
			setInNeedOfNewVersion();
		}
		return this;
	}

	/**
	 * Set the label.
	 * 
	 * @param label the label to set
	 * 
	 * @return this
	 */
	public Study setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(this.label)) {

		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
		return this;
	}

	/**
	 * Constructs a <code>String</code> with all attributes in name = value
	 * format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	@Override
	public String toString() {
		final String TAB = "";

		final StringBuilder retValue = new StringBuilder();

		retValue.append("Study(").append(super.toString()).append(TAB).append(
				"label=").append(this.label).append(TAB).append("otuSets=")
				.append(this.otuSets).append(TAB).append(
						"studyWideAttachmentTypes=").append(
						this.attachmentTypes).append(TAB).append(
						"studyWideAttachmentNamespaces=").append(
						this.attachmentNamespaces).append(TAB).append(
						")");

		return retValue.toString();
	}

}
