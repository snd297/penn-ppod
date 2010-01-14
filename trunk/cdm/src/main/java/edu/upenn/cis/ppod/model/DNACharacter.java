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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collections;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

/**
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNACharacter.TABLE)
public class DNACharacter extends Character {

	// @Id
	// @Column(name = "DNA_CHAR_ID")
	// private Long id = 1L;

	public static final String TABLE = "DNA_CHARACTER";

	public static final DNACharacter DNA_CHARACTER = new DNACharacter();

	@Column(name = "LABEL", nullable = false, unique = true)
	private String label;

	@OneToMany(mappedBy = "character")
	@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@MapKey(name = "stateNumber")
	private final Map<Integer, DNAState> states = newHashMap();

	private DNACharacter() {
		setLabel("DNA Character");
		states.put(0, DNAState.A);
		states.put(1, DNAState.C);
		states.put(2, DNAState.G);
		states.put(3, DNAState.T);
	}

	/**
	 * The states of a DNA character are fixed, so this method is unsupported.
	 * 
	 * @param state ignored
	 * 
	 * @returns nothing
	 * 
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public CharacterState addState(final CharacterState state) {
		throw new UnsupportedOperationException(
				"can't add a state to a DNACharacter");
	}

	@Override
	public Map<Integer, CharacterState> getStates() {

		// We need to jump through these hoops because we
		// can't just return a Map<Integer, DNAState> which is what
		// this.states is.
		final Map<Integer, CharacterState> states = newHashMap();
		for (final Map.Entry<Integer, DNAState> dnaStateEntry : this.states
				.entrySet()) {
			states.put(dnaStateEntry.getKey(), dnaStateEntry.getValue());
		}
		// unmodifiable to be consistent with Character#getStates.
		return Collections.unmodifiableMap(states);
	}

	@Override
	public Character setLabel(final String label) {
		this.label = label;
		return this;
		// Can't do this:
		// throw new UnsupportedOperationException(
		// "can't change the label of a DNACharacter");
		// because Hibernate calls this method
	}

}
