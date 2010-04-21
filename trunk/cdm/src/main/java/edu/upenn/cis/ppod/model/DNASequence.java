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

import javax.annotation.CheckForNull;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNASequence.TABLE)
public class DNASequence extends MolecularSequence<DNASequenceSet> {

	/**
	 * The name of the {@code DNASequence} table.
	 */
	static final String TABLE = "DNA_SEQUENCE";

	@ManyToOne
	@JoinColumn(name = DNASequenceSet.ID_COLUMN, nullable = false)
	@CheckForNull
	private DNASequenceSet sequenceSet;

	@Override
	public DNASequenceSet getSequenceSet() {
		return sequenceSet;
	}

	@Override
	public void accept(final IVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public boolean isLegal(final char c) {
		if (c == 'A') {
			return true;
		}
		if (c == 'C') {
			return true;
		}
		if (c == 'G') {
			return true;
		}
		if (c == 'T') {
			return true;
		}
		if (c == 'R') {
			return true;
		}
		if (c == 'Y') {
			return true;
		}
		if (c == 'K') {
			return true;
		}
		if (c == 'M') {
			return true;
		}
		if (c == 'S') {
			return true;
		}
		if (c == 'W') {
			return true;
		}
		if (c == 'B') {
			return true;
		}
		if (c == 'D') {
			return true;
		}
		if (c == 'H') {
			return true;
		}
		if (c == 'V') {
			return true;
		}
		if (c == 'N') {
			return true;
		}
		if (c == '-') {
			return true;
		}
		return false;
	}

	@Override
	public DNASequence setSequenceSet(final DNASequenceSet sequenceSet) {
		this.sequenceSet = sequenceSet;
		return this;
	}

}
