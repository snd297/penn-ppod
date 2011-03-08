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
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A standard character, aka a morphological character. For example,
 * "length_of_infraorb_canal".
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StandardCharacter.TABLE)
public class StandardCharacter extends UuPPodEntity {

	@Access(AccessType.PROPERTY)
	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@CheckForNull
	private Long id;

	@SuppressWarnings("unused")
	@Version
	@Column(name = "OBJ_VERSION")
	@CheckForNull
	private Integer objVersion;

	public final static String TABLE = "STANDARD_CHARACTER";

	public final static String ID_COLUMN = TABLE + "_ID";

	/**
	 * The non-unique label of this {@code Character}.
	 */
	@Column(name = "LABEL", nullable = false)
	@Index(name = "LABEL_IDX")
	@CheckForNull
	private String label;

	/**
	 * The matrix that owns this {@code StandardCharacter}.
	 */
	@ManyToOne(optional = false)
	@JoinColumn(
			name = StandardMatrix.ID_COLUMN,
			insertable = false,
			updatable = false)
	@CheckForNull
	private StandardMatrix parent;

	@Column(name = "MESQUITE_ID", nullable = false, unique = true)
	@CheckForNull
	private String mesquiteId;

	/**
	 * The states that this character can have. For example, 0->"absent",
	 * 1->"present", 2->"one", or 3->"two". NOTE: it is legal to have
	 * non-contiguous integers in the keys - so, for example, you might have 0,
	 * 2, and 3.
	 */
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL,
			orphanRemoval = true)
	@MapKey(name = "stateNumber")
	private final Map<Integer, StandardState> states = newHashMap();

	/**
	 * Default constructor for (at least) Hibernate.
	 */
	public StandardCharacter() {}

	/**
	 * Add <code>state</code> into this <code>Character</code>.
	 * <p>
	 * Calling this handles both sides of the <code>Character</code><->
	 * <code>CharacterState</code>s. relationship.
	 * 
	 * @param state what we're adding
	 * 
	 * @return the previous state that was associated with
	 *         {@code state.getStateNumber()} or {@code null} if there was no
	 *         such state.
	 */
	@Nullable
	public StandardState addState(final StandardState state) {
		checkNotNull(state);
		final StandardState originalState =
				states.put(state.getStateNumber(), state);
		if (state == originalState) {
			return originalState;
		}

		if (originalState != null) {
			originalState.setParent(null);
		}
		state.setParent(this);
		return originalState;
	}

	@Nullable
	public Long getId() {
		return id;
	}

	/**
	 * Get the label of this {@code Character}.
	 * 
	 * @return the label of this {@code Character}
	 */
	@Nullable
	public String getLabel() {
		return label;
	}

	@Nullable
	public String getMesquiteId() {
		return mesquiteId;
	}

	/**
	 * Get the matrix that owns this character.
	 * <p>
	 * Will be {@code null} for newly created characters. Will never be
	 * {@code null} for characters in a persistent state.
	 * 
	 * @return the matrix that owns this character
	 */
	@Nullable
	public StandardMatrix getParent() {
		return parent;
	}

	/**
	 * Get the state with the given state number, or {@code null} if there is no
	 * such state.
	 * 
	 * @param stateNumber the state number of the state we want to retrieve
	 * 
	 * @return the state with the given state number, or {@code null} if there
	 *         is no such state.
	 */
	@Nullable
	public StandardState getState(final Integer stateNumber) {
		checkNotNull(stateNumber);
		return states.get(stateNumber.intValue());
	}

	/**
	 * Get the states of this character.
	 * 
	 * @return the states of this character.
	 */
	public Set<StandardState> getStates() {
		return newHashSet(states.values());
	}

	@SuppressWarnings("unused")
	private void setId(final Long id) {
		this.id = id;
	}

	/** {@inheritDoc} */
	public void setLabel(final String label) {
		this.label = checkNotNull(label);
	}

	public void setMesquiteId(final String mesquiteId) {
		this.mesquiteId = checkNotNull(mesquiteId);
	}

	/** {@inheritDoc} */
	public void setParent(
			@CheckForNull final StandardMatrix parent) {
		this.parent = parent;
	}
}
