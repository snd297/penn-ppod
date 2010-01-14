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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = DNAState.TABLE)
public class DNAState extends CharacterState {
	final static String TABLE = "DNA_STATE";

	// static final String ID_COLUMN = "DNA_STATE_ID";
	static final String STATE_COLUMN = "STATE";

	public static final DNAState A = new DNAState("A");
	public static final DNAState C = new DNAState("C");
	public static final DNAState G = new DNAState("G");
	public static final DNAState T = new DNAState("T");

	// @Column(name = "STATE_NUMBER", unique = true, nullable = false)
	// private Integer stateNumber;

	@Column(name = "LABEL", unique = true, nullable = false)
	private String label;

	/**
	 * Tells us what {@link Character} this is a stateNumber of.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = Character.ID_COLUMN)
	private DNACharacter character;

	DNAState() {}

	private DNAState(final String label) {
		checkNotNull(label);
		this.label = label;
		if ("A".equals(label)) {
			// id = 0L;
			// stateNumber = 0;
		} else if ("C".equals(label)) {
			// id = 0L;
			// stateNumber = 1;
		} else if ("G".equals(label)) {
			// id = 0L;
			// stateNumber = 2;
		} else if ("T".equals(label)) {
			// id = 0L;
			// stateNumber = 3;
		} else {
			throw new IllegalArgumentException("undefined label: [" + label
					+ "]");
		}
	}

	@Override
	public DNACharacter getCharacter() {
		return character;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public Integer getStateNumber() {
		throw new UnsupportedOperationException(
				"DNAState's do not have numbers");
	}

	@Override
	public DNAState setCharacter(final Character character) {
		throw new UnsupportedOperationException(
				"DNAState character is fixed, setCharacter(...) is not supported");
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
	public DNAState setLabel(final String label) {
		throw new UnsupportedOperationException();
	}

	/**
	 * This method is not supported for {@code DNAState} since all instance are
	 * immutable.
	 * 
	 * @param stateNumber ignored
	 * 
	 * @throws UnsupportedOperationException always
	 */

	private DNAState setStateNumber(final Integer state) {
		throw new UnsupportedOperationException();
	}

}
