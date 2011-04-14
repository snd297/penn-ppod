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

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Version;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.dto.ILabeled;
import edu.upenn.cis.ppod.imodel.IOtuSets;
import edu.upenn.cis.ppod.imodel.IUuPPodEntity;

/**
 * A collection of work - inspired by a Mesquite project - sets of OTU sets and,
 * through the OTU sets, matrices and tree sets.
 * 
 * @author Sam Donnelly
 */
@NamedQueries({
		@NamedQuery(name = "Study-getByPPodId",
				query = "select s "
						+ "from Study s "
						+ "where s.pPodId=:pPodId"),
		@NamedQuery(
				name = "Study-getPPodIdLabelPairs",
				query = "select s.pPodId, s.label from Study s") })
@Entity
@Table(name = Study.TABLE)
public class Study
		extends UuPPodEntity
		implements ILabeled, IOtuSets, IUuPPodEntity {

	/** The table name for this entity. */
	public static final String TABLE = "STUDY";

	/** To be used when referring to this entity in foreign keys. */
	public static final String ID_COLUMN =
			TABLE + "_ID";

	@CheckForNull
	private Long id;

	@CheckForNull
	private Integer version;

	@CheckForNull
	private String label;

	private List<OtuSet> otuSets = newArrayList();

	/**
	 * No-arg constructor.
	 */
	public Study() {}

	/**
	 * Insert an OTU set at the given position.
	 * 
	 * @param pos where the OTU set should be inserted
	 * @param otuSet to be inserted
	 * 
	 * @throws IllegalArgumentException if this study already contains the OTU
	 *             set
	 * @throws IllegalArgumentException if this study already contains an OTU
	 *             with the {@code otuSet}'s label
	 */
	public void addOtuSet(final int pos, final OtuSet otuSet) {
		checkNotNull(otuSet);
		checkArgument(pos >= 0, "pos < 0");
		checkArgument(
				!getOtuSets().contains(otuSet),
				"this study already contains otu set ["
						+ otuSet.getLabel()
						+ "]");
		otuSets.add(pos, otuSet);
		otuSet.setParent(this);
	}

	/**
	 * Equivalent to {@code #addOtuSet(getOtuSets.size(), otuSet)};
	 */
	public void addOtuSet(final OtuSet otuSet) {
		addOtuSet(otuSets.size(), otuSet);
	}

	@Nullable
	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	public Long getId() {
		return id;
	}

	/**
	 * Get the label.
	 * 
	 * @return the label
	 */
	@Column(nullable = false, unique = true)
	@Nullable
	public String getLabel() {
		return label;
	}

	/** {@inheritDoc} */
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = ID_COLUMN, nullable = false)
	public List<OtuSet> getOtuSets() {
		return otuSets;
	}

	/**
	 * @return the objVersion
	 */
	@Version
	@Column(name = "OBJ_VERSION")
	@CheckForNull
	public Integer getVersion() {
		return version;
	}

	/**
	 * Remove an OTU set from this Study.
	 * 
	 * @param otuSet to be removed
	 * 
	 * @throw IllegalArgumentException if this study does not contain the Otu
	 *        set
	 */
	public void removeOtuSet(final OtuSet otuSet) {
		checkNotNull(otuSet);
		checkArgument(getOtuSets().contains(otuSet),
				"this study does not contain otu set [" + otuSet.getLabel()
						+ "]");
		otuSets.remove(otuSet);
		otuSet.setParent(null);
	}

	@SuppressWarnings("unused")
	private void setId(final Long id) {
		this.id = id;
	}

	/**
	 * Set the label.
	 * 
	 * @param label the label to set
	 */
	public void setLabel(final String label) {
		this.label = checkNotNull(label);
	}

	/**
	 * @param otuSets the otuSets to set
	 */
	@SuppressWarnings("unused")
	private void setOtuSets(final List<OtuSet> otuSets) {
		this.otuSets = otuSets;
	}

	/**
	 * @param objVersion the objVersion to set
	 */
	@SuppressWarnings("unused")
	private void setVersion(final Integer objVersion) {
		this.version = objVersion;
	}
}
