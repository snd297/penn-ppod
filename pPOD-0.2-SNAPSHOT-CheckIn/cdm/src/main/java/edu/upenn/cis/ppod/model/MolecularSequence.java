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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import javax.persistence.Column;

/**
 * @author Sam Donnelly
 */
abstract class MolecularSequence {

	final static String SEQUENCE_COLUMN = "SEQUENCE";

	@Column(name = SEQUENCE_COLUMN)
	private String sequence;
	private final Map<Integer, CharacterStateCell> cellsByCharStateIndex = newHashMap();

	public String getSequence() {
		return sequence;
	}

	public MolecularSequence putCellByCharStateIndex(final Integer idx,
			final CharacterStateCell cell) {
		checkNotNull(idx, cell);
		cellsByCharStateIndex.put(idx, cell);
		return this;
	}

	public MolecularSequence setSequence(final String sequence) {
		this.sequence = sequence;
		return this;
	}

// public static MolecularSequence of(final List<CharacterStateCell> cells)
	// {
// checkNotNull(cells);
// final MolecularSequence sequence = new MolecularSequence();
// for (final CharacterStateCell cell : cells) {
// if (cell.getStates().size() == 1) {
// sequence.addState(cell.getStates().iterator().next());
// } else {
// sequence.addState(null);
// sequence.putCellByCharStateIndex(sequence.getCharStates()
// .size() - 1, cell);
// }
// }
// return sequence;
// }
}
