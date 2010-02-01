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

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * A state of a {@link MolecularCharacter}.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@MappedSuperclass
public abstract class MolecularState extends CharacterState {

	/**
	 * This column should be the same as {@link CharacterState#getLabel()} and
	 * is only here to prevent duplicate {@code DNAState}s form being added to
	 * the table.
	 * <p>
	 * We were just calling this column {@code "LABEL"}, but that seemed to
	 * break {@link Character#getLabel()} - it would return {@code null} after
	 * db retrieval. Because {@code CharacterState} has a column called {@code
	 * "LABEL"}?
	 */
	@Column(name = "MOLECULAR_STATE_LABEL", nullable = false, unique = true)
	@SuppressWarnings("unused")
	private String molecularStateLabel;

	/**
	 * Set the molecular state label and {@code CharacterState.getLabel()}. This
	 * value is only used to prevent multiple rows of a particular molecular
	 * state from being created. For example, we don't want more than one
	 * {@code DNACharacter} in the database.
	 * 
	 * @param molecularStateLabel the label.
	 * 
	 * @return this
	 */
	protected MolecularState setMolecularStateLabel(
			final String molecularStateLabel) {
		super.setLabel(molecularStateLabel);
		this.molecularStateLabel = molecularStateLabel;
		return this;
	}

	/**
	 * This method is not supported for {@code DNAState} since all instances are
	 * immutable.
	 * 
	 * @param label ignored
	 * 
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public MolecularState setLabel(final String label) {
		throw new UnsupportedOperationException(
				"the label of a MolecularState is fixed");
	}
}