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
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.imodel.IAttachmentNamespace;
import edu.upenn.cis.ppod.imodel.IAttachmentType;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.IStudy;
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
public class Study
		extends UUPPodEntity
		implements IStudy {

	/** The table name for this entity. */
	public static final String TABLE = "STUDY";

	/** To be used when referring to this entity in foreign keys. */
	public static final String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	static final String LABEL_COLUMN = "LABEL";

	@Column(name = LABEL_COLUMN, nullable = false)
	private String label;

	@OneToMany(
			mappedBy = "parent",
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			targetEntity = OTUSet.class)
	@OrderColumn(name = "POSITION")
	private final List<IOTUSet> otuSets = newArrayList();

	@Transient
	private final Set<IAttachmentNamespace> attachmentNamespaces = newHashSet();

	@Transient
	private final Set<IAttachmentType> attachmentTypes = newHashSet();

	Study() {}

	@Override
	public void accept(final IVisitor visitor) {
		visitor.visitStudy(this);
		for (final IOTUSet otuSet : getOTUSets()) {
			otuSet.accept(visitor);
		}
	}

	/** {@inheritDoc} */
	public void addOTUSet(final int pos, final IOTUSet otuSet) {
		checkNotNull(otuSet);
		checkArgument(pos >= 0, "pos< 0");
		checkArgument(!getOTUSets().contains(otuSet),
				"this study already contains otu set [" + otuSet.getLabel()
						+ "]");
		otuSets.add(pos, otuSet);
		otuSet.setParent(this);
		ModelUtil.adjustPositions(otuSets);
		setInNeedOfNewVersion();
	}

	/** {@inheritDoc} */
	public void addOTUSet(final IOTUSet otuSet) {
		checkNotNull(otuSet);
		checkArgument(!getOTUSets().contains(otuSet),
				"this study already contains otu set [" + otuSet.getLabel()
						+ "]");
		otuSets.add(otuSet);
		otuSet.setParent(this);
		otuSet.setPosition(otuSets.size() - 1);
		setInNeedOfNewVersion();
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	protected void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			@CheckForNull final Object parent) {
		int otuSetPosition = -1;
		for (final IOTUSet otuSet : getOTUSets()) {
			otuSetPosition++;
			otuSet.setPosition(otuSetPosition);
		}
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
	protected boolean beforeMarshal(final Marshaller m) {
		if (attachmentNamespaces.size() == 0) {
			PPodEntitiesUtil.extractAttachmentInfoFromAttachee(
					attachmentNamespaces,
					attachmentTypes,
					this);
			PPodEntitiesUtil.extractAttachmentInfoFromPPodEntities(
					attachmentNamespaces,
					attachmentTypes,
					this);
		}
		return true;
	}

	/** {@inheritDoc} */
	@XmlAttribute
	public String getLabel() {
		return label;
	}

	/** {@inheritDoc} */
	public List<IOTUSet> getOTUSets() {
		return Collections.unmodifiableList(otuSets);
	}

	@XmlElement(name = "otuSet")
	protected List<IOTUSet> getOTUSetsModifiable() {
		return otuSets;
	}

	@XmlElement(name = "studyWideAttachmentNamespace")
	protected Set<IAttachmentNamespace> getStudyWideAttachmentNamespacesModifiable() {
		return attachmentNamespaces;
	}

	@XmlElement(name = "studyWideAttachmentType")
	protected Set<IAttachmentType> getStudyWideAttachmentTypes() {
		return attachmentTypes;
	}

	/** {@inheritDoc} */
	public void removeOTUSet(final IOTUSet otuSet) {
		checkNotNull(otuSet);
		checkArgument(getOTUSets().contains(otuSet),
				"this study does not contain otu set [" + otuSet.getLabel()
						+ "]");
		otuSets.remove(otuSet);
		otuSet.setParent(null);
		otuSet.setPosition(null);
		setInNeedOfNewVersion();
		ModelUtil.adjustPositions(otuSets);
	}

	/** {@inheritDoc} */
	public void setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(this.label)) {

		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
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
