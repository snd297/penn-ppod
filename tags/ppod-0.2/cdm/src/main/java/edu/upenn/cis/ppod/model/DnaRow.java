/*
 * Copyright (C) 2010 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.imodel.IChild;

/**
 * A row of {@link DnaCell}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DnaRow.TABLE)
public class DnaRow implements IChild<DnaMatrix> {

	public static final String TABLE = "dna_row";

	public static final String ID_COLUMN = TABLE + "_id";

	@CheckForNull
	private Long id;

	@CheckForNull
	private Integer version;

	@CheckForNull
	private DnaMatrix parent;

	@CheckForNull
	private String sequence;

	public DnaRow() {}

	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@Nullable
	public Long getId() {
		return id;
	}

	/** {@inheritDoc} */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = DnaMatrix.ID_COLUMN)
	public DnaMatrix getParent() {
		return parent;
	}

	@Lob
	@Column(nullable = false)
	@Nullable
	public String getSequence() {
		return sequence;
	}

	/**
	 * @return the version
	 */
	@Version
	@Column(name = "obj_version")
	@Nullable
	public Integer getVersion() {
		return version;
	}

	@SuppressWarnings("unused")
	private void setId(final Long id) {
		this.id = id;
	}

	/** {@inheritDoc} */
	public void setParent(final DnaMatrix parent) {
		this.parent = parent;
	}

	public void setSequence(final String sequence) {
		this.sequence = sequence;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(final Integer version) {
		this.version = version;
	}
}
