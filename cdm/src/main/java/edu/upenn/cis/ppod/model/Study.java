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

import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.dto.IOtuSets;
import edu.upenn.cis.ppod.imodel.ILabeled;
import edu.upenn.cis.ppod.util.IVisitor;

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
						// + "join fetch s.otuSets o "
						// + "join fetch o.dnaMatrices d "
						// + "join fetch d.rows r "
						// + "join fetch r.values v "
						// + "join fetch v.cells "
						+ "where s.pPodId=:pPodId"),
		@NamedQuery(
				name = "Study-getVersionByPPodId",
				query = "select s.versionInfo.version "
						+ "from Study s where s.pPodId=:pPodId"),
		@NamedQuery(
				name = "Study-getOTUSetInfosByStudyPPodIdAndMinVersion",
				query = "select os.id, os.pPodId, os.versionInfo.version "
						+ "from Study s join s.otuSets os "
						+ "where s.pPodId=:studyPPodId "
						+ "and os.versionInfo.version >= :minVersion"),
		@NamedQuery(
				name = "Study-getPPodIdLabelPairs",
				query = "select s.pPodId, s.label from Study s") })
@Entity
@Table(name = Study.TABLE)
public class Study
		extends UuPPodEntity
		implements ILabeled, IOtuSets {

	/** The table name for this entity. */
	public static final String TABLE = "STUDY";

	/** To be used when referring to this entity in foreign keys. */
	public static final String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	static final String LABEL_COLUMN = "LABEL";

	@Column(name = LABEL_COLUMN, nullable = false)
	@CheckForNull
	private String label;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderColumn(name = "POSITION")
	@JoinColumn(name = JOIN_COLUMN, nullable = false)
	private final List<OtuSet> otuSets = newArrayList();

	/**
	 * No-arg constructor.
	 */
	public Study() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitStudy(this);
		for (final OtuSet otuSet : getOtuSets()) {
			otuSet.accept(visitor);
		}
	}

	/**
	 * Insert an OTU set at the given position.
	 * 
	 * @param pos where the OTU set should be inserted
	 * @param otuSet to be inserted
	 * 
	 * @throws IllegalArgumentException if this study already contains the OTU
	 *             set
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
		setInNeedOfNewVersion();
	}

	/** {@inheritDoc} */
	public void addOtuSet(final OtuSet otuSet) {
		checkNotNull(otuSet);
		checkArgument(!getOtuSets().contains(otuSet),
				"this study already contains otu set [" + otuSet.getLabel()
						+ "]");
		otuSets.add(otuSet);
		otuSet.setParent(this);
		setInNeedOfNewVersion();
	}

	/**
	 * Get the label.
	 * 
	 * @return the label
	 */
	@Nullable
	public String getLabel() {
		return label;
	}

	/** {@inheritDoc} */
	public List<OtuSet> getOtuSets() {
		return Collections.unmodifiableList(otuSets);
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
		setInNeedOfNewVersion();
	}

	/**
	 * Set the label.
	 * 
	 * @param label the label to set
	 */
	public void setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(this.label)) {

		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
	}
}
