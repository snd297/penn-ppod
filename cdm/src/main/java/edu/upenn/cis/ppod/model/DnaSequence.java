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

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

	/**
	 * The name of the {@code DNASequence} table.
	 */
	public static final String TABLE = "DNA_SEQUENCE";

	public static final String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	@ManyToOne(
			fetch = FetchType.LAZY,
			optional = false)
	@CheckForNull
	private DnaSequenceSet parent;

	@ElementCollection
	@CollectionTable(name = "DNA_SEQUENCE_PHRED_PHRAP_SCORES",
						joinColumns = @JoinColumn(name = JOIN_COLUMN))
	@Column(name = "ELEMENT")
	@Enumerated(EnumType.ORDINAL)
	@SuppressWarnings("unused")
	private final List<Double> phredPhrapScores = newArrayList();

	/**
	 * Default constructor.
	 */
	public DnaSequence() {}

	@Nullable
	public DnaSequenceSet getParent() {
		return parent;
	}

	@Override
	public boolean isLegal(final char c) {
		return PPodDnaSequence.LEGAL_CHARS.contains(c);
	}

	/** {@inheritDoc} */
	public void setParent(
			@CheckForNull final DnaSequenceSet parent) {
		this.parent = parent;
	}

}
