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

/**
 * @author Sam Donnelly
 */
public class DNASequence extends MolecularSequence {
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
				putStates(cellIdx, null);
			}
		}
		return this;
	}
}
