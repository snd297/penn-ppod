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

	public final static String TABLE = "standard_character";

	public final static String ID_COLUMN = TABLE + "_id";

	@CheckForNull
	private Long id;

	@CheckForNull
	private Integer version;

	/**
	 * The non-unique label of this {@code Character}.
	 */
	@CheckForNull
	private String label;

	/**
	 * The matrix that owns this {@code StandardCharacter}.
	 */
	@CheckForNull
	private StandardMatrix parent;

	@CheckForNull
	private String mesquiteId;

	/**
	 * The states that this character can have. For example, 0->"absent",
	 * 1->"present", 2->"one", or 3->"two". NOTE: it is legal to have
	 * non-contiguous integers in the keys - so, for example, you might have 0,
	 * 2, and 3.
	 */
	private Map<Integer, StandardState> states = newHashMap();

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
	 */
	public void addState(final StandardState state) {
		checkNotNull(state);
		final StandardState originalState =
				states.put(state.getStateNumber(), state);

		if (state != originalState && originalState != null) {
			originalState.setParent(null);
		}
		state.setParent(this);
	}

	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@Nullable
	public Long getId() {
		return id;
	}

	/**
	 * Get the label of this {@code Character}.
	 * 
	 * @return the label of this {@code Character}
	 */
	@Column(name = "label", nullable = false)
	@Index(name = "IDX_LABEL")
	@Nullable
	public String getLabel() {
		return label;
	}

	@Column(name = "mesquite_id", nullable = false, unique = true)
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
	@ManyToOne(optional = false)
	@JoinColumn(
			name = StandardMatrix.ID_COLUMN,
			insertable = false,
			updatable = false)
	@Nullable
	public StandardMatrix getParent() {
		return parent;
	}

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL,
			orphanRemoval = true)
	@MapKey(name = "stateNumber")
	public Map<Integer, StandardState> getStates() {
		return states;
	}

	/**
	 * @return the version
	 */
	@Version
	@Column(name = "obj_version")
	@Nullable
	public Integer getVersion() {
		return version;
	}

	@SuppressWarnings("unused")
	private void setId(final Long id) {
		this.id = id;
	}

	/** {@inheritDoc} */
	public void setLabel(final String label) {
		this.label = label;
	}

	public void setMesquiteId(final String mesquiteId) {
		this.mesquiteId = mesquiteId;
	}

	/** {@inheritDoc} */
	public void setParent(
			@CheckForNull final StandardMatrix parent) {
		this.parent = parent;
	}

	@SuppressWarnings("unused")
	private void setStates(final Map<Integer, StandardState> states) {
		this.states = states;
	}

	/**
	 * @param version the version to set
	 */
	@SuppressWarnings("unused")
	private void setVersion(final Integer version) {
		this.version = version;
	}
}
