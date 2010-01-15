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

import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
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

	/**
	 * Even though {@code DNACharacter} inherits a label from {@link Character}
	 * we include this since the label must be unique here but not there. This
	 * is a compromise.
	 */
	@Column(name = "LABEL", nullable = false, unique = true)
	private String label;

	@OneToMany(mappedBy = "character")
	@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private final Set<DNAState> states = newHashSet();

	private DNACharacter() {
		label = "DNA Character";
		states.add(DNAState.A);
		states.add(DNAState.C);
		states.add(DNAState.G);
		states.add(DNAState.T);
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

	/**
	 * A DNA Character has no state numbers, so this method is unsupported.
	 * 
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public Map<Integer, CharacterState> getStates() {
		throw new UnsupportedOperationException(
				"DNA states have no state numbers so no values to return");
		// We need to jump through these hoops because we
		// can't just return a Map<Integer, DNAState> which is what
		// this.states is.
// final Map<Integer, CharacterState> states = newHashMap();
// for (final Map.Entry<Integer, DNAState> dnaStateEntry : this.states
// .entrySet()) {
// states.put(dnaStateEntry.getKey(), dnaStateEntry.getValue());
// }
// // unmodifiable to be consistent with Character#getStates.
// return Collections.unmodifiableMap(states);
	}

	@Override
	public Character setLabel(final String label) {
		if (this.label != null) {
			throw new IllegalStateException("A DNA characecter label is WORM");
		}
		this.label = label;
		return this;
		// Can't do this:
		// throw new UnsupportedOperationException(
		// "can't change the label of a DNACharacter");
		// because Hibernate calls this method
	}
}
