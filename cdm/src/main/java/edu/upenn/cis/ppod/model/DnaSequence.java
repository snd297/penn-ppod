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
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.dto.PPodDnaSequence;

/**
 * A DNA sequence.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DnaSequence.TABLE)
public class DnaSequence extends Sequence<DnaSequenceSet> {

	@Access(AccessType.PROPERTY)
	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@CheckForNull
	private Long id;

	@SuppressWarnings("unused")
	@Version
	@Column(name = "OBJ_VERSION")
	@CheckForNull
	private Integer objVersion;

	/**
	 * The name of the {@code DNASequence} table.
	 */
	public static final String TABLE = "DNA_SEQUENCE";

	public static final String ID_COLUMN = TABLE + "_ID";

	@ManyToOne(
			fetch = FetchType.LAZY,
			optional = false)
	@CheckForNull
	private DnaSequenceSet parent;

	@ElementCollection
	@CollectionTable(name = "DNA_SEQUENCE_PHRED_PHRAP_SCORES",
						joinColumns = @JoinColumn(name = ID_COLUMN))
	@Column(name = "ELEMENT")
	@SuppressWarnings("unused")
	private final List<Double> phredPhrapScores = newArrayList();

	/**
	 * Default constructor.
	 */
	public DnaSequence() {}

	@Nullable
	public Long getId() {
		return id;
	}

	@Nullable
	public DnaSequenceSet getParent() {
		return parent;
	}

	@Override
	public boolean isLegal(final char c) {
		return PPodDnaSequence.LEGAL_CHARS.contains(c);
	}

	@SuppressWarnings("unused")
	private void setId(final Long id) {
		this.id = id;
	}

	/** {@inheritDoc} */
	public void setParent(
			@CheckForNull final DnaSequenceSet parent) {
		this.parent = parent;
	}

}
