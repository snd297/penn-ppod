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

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNASequence.TABLE)
public class DNASequence extends MolecularSequence {

	public static final String TABLE = "DNA_SEQUENCE";

	@Override
	public DNASequence setSequence(final String newSequence) {
		for (int i = 0; i < newSequence.length(); i++) {
			if (DNAState.NucleotideStateNumber.hasOneWithAValueOf(newSequence
					.charAt(i))) {

			} else {
				throw new IllegalArgumentException("Position " + i + " is ["
						+ newSequence.charAt(i)
						+ "] which is not a DNA state");
			}
		}
		super.setSequence(newSequence);
		return this;
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
				checkState(DNAState.NucleotideStateNumber
						.hasOneWithAValueOf(stateLabel), "cell " + cellIdx
						+ " has a state label [" + stateLabel
						+ "] which is not that of a DNAState");
				checkState(DNAState.NucleotideStateNumber
						.hasOneWithAValueOf(stateNumber), "cell " + cellIdx
						+ " has a state number of" + stateNumber
						+ " which is not that of a DNAState");
				sequenceStringBuilder.append(stateLabel);

			} else {
				sequenceStringBuilder.append("N");
				// putStates(cellIdx, null);
			}
		}
		return this;
	}
}
