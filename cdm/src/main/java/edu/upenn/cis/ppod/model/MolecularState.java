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

import javax.annotation.CheckForNull;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * A state of a {@link MolecularCharacter}.
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class MolecularState extends CharacterState {

	@Column(name = LABEL_COLUMN, unique = true, nullable = false)
	@CheckForNull
	private String label;

	MolecularState() {}

	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * Set the molecular state label and {@code CharacterState.getLabel()}. This
	 * value is only used to prevent multiple rows of a particular molecular
	 * state from being created. For example, we don't want more than one
	 * {@code DNACharacter} in the database.
	 * 
	 * @param label the label.
	 * 
	 * @return this
	 */
	protected MolecularState setLabel(final String label) {
		checkNotNull(label);
		this.label = label;
		return this;
	}
}