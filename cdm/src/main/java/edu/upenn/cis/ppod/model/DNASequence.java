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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.getOnlyElement;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;

/**
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNASequence.TABLE)
public class DNASequence extends MolecularSequence<DNASequenceSet> {

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
		return false;
	}

	@ManyToOne
	@JoinColumn(name = DNASequenceSet.ID_COLUMN, nullable = false)
	@CheckForNull
	private DNASequenceSet sequenceSet;

	/**
	 * The name of the {@code DNASequence} table.
	 */

	static final String TABLE = "DNA_SEQUENCE";

	@Override
	public MolecularSequenceSet<DNASequence> getSequenceSet() {
		return sequenceSet;
	}

	@edu.umd.cs.findbugs.annotations.SuppressWarnings
	public DNASequence of(final List<? extends CharacterStateCell> cells) {
		checkNotNull(cells);
		final StringBuilder sequenceStringBuilder = new StringBuilder();
		for (int cellIdx = 0; cellIdx < cells.size(); cellIdx++) {
			if (cells.get(cellIdx).getStates().size() == 1) {
				final String stateLabel = getOnlyElement(
						cells.get(cellIdx).getStates()).getLabel();
				final Integer stateNumber = getOnlyElement(
						cells.get(cellIdx).getStates()).getStateNumber();
				checkState(DNAState.Nucleotide.hasOneWithAValueOf(stateLabel),
						"cell " + cellIdx + " has a state label [" + stateLabel
								+ "] which is not that of a DNAState");
				checkState(DNAState.Nucleotide.hasOneWithAValueOf(stateNumber),
						"cell " + cellIdx + " has a state number of"
								+ stateNumber
								+ " which is not that of a DNAState");
				sequenceStringBuilder.append(stateLabel);

			} else {
				sequenceStringBuilder.append("N");
				// putStates(cellIdx, null);
			}
		}
		return this;
	}

	@Override
	protected DNASequence setSequenceSet(
			@Nullable final DNASequenceSet newSequenceSet) {
		sequenceSet = newSequenceSet;
		return this;
	}

	/**
	 * See {@link Unmarshaller}.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		setSequenceSet((DNASequenceSet) sequenceSet);
	}

}
