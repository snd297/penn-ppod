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

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;

/**
 * @author Sam Donnelly
 */
@Entity
public class DNACharacter extends Character {

// @Id
// @Column(name = "DNA_CHAR_ID")
// private Long id = 1L;

	public static final DNACharacter DNA_CHARACTER = new DNACharacter();

	@Column(nullable = false, unique = true)
	private String label;

	@OneToMany
	@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@MapKey(name = "state")
	private final Map<Integer, DNAState> states = newHashMap();

	private DNACharacter() {
		setLabel("DNA Character");
		states.put(0, DNAState.A);
		states.put(1, DNAState.C);
		states.put(2, DNAState.G);
		states.put(3, DNAState.T);
	}

	@Override
	public CharacterState addState(final CharacterState phyloCharState) {
// checkNotNull(phyloCharState);
// states.put(phyloCharState.getStateNumber(), (DNAState) phyloCharState);
// phyloCharState.setCharacter(this);
// resetPPodVersionInfo();
		return phyloCharState;
	}

	@Override
	public Map<Integer, CharacterState> getStates() {
		final Map<Integer, CharacterState> charStates = newHashMap();
// for (final Map.Entry<Integer, DNAState> dnaStateEntry : states
// .entrySet()) {
// charStates.put(dnaStateEntry.getKey(), dnaStateEntry.getValue());
// }
		return charStates;
	}

	@Override
	public Character setLabel(final String label) {
		this.label = label;
		return this;
	}

}
