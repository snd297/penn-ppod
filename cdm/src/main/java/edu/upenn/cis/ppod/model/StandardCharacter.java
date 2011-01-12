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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Preconditions;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A standard character, aka a morphological character. For example,
 * "length_of_infraorb_canal".
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StandardCharacter.TABLE)
public class StandardCharacter extends UuPPodEntityWithDocId {

	public final static String TABLE = "STANDARD_CHARACTER";

	public final static String JOIN_COLUMN = TABLE + "_ID";

	final static String LABEL_COLUMN = "LABEL";

	/**
	 * The non-unique label of this {@code Character}.
	 */
	@Column(name = LABEL_COLUMN, nullable = false)
	private String label;

	/**
	 * The matrix that owns this {@code StandardCharacter}.
	 */
	@ManyToOne(optional = false)
	@JoinColumn(
			name = StandardMatrix.JOIN_COLUMN,
			insertable = false,
			updatable = false)
	@CheckForNull
	private StandardMatrix parent;

	@Column(name = "MESQUITE_ID", nullable = false, unique = true)
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

	@Transient
	@Nullable
	private Set<StandardState> statesXml;

	/**
	 * Default constructor for (at least) Hibernate.
	 */
	public StandardCharacter() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitStandardCharacter(this);
		for (final StandardState state : getStates()) {
			state.accept(visitor);
		}
		super.accept(visitor);
	}

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
	@CheckForNull
	public StandardState addState(final StandardState state) {
		Preconditions.checkNotNull(state);
		final StandardState originalState =
				states.put(state.getStateNumber(), state);
		if (state == originalState) {
			return originalState;
		}

		if (originalState != null) {
			originalState.setParent(null);
		}
		state.setParent(this);
		setInNeedOfNewVersion();
		return originalState;
	}

	protected boolean afterMarshal(@CheckForNull final Marshaller marshaller) {
		statesXml = null;
		return true;
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	protected void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent) {
		setParent((StandardMatrix) parent);
		for (final StandardState stateXml : statesXml) {
			stateXml.setParent(this);
			states.put(stateXml.getStateNumber(), stateXml);
		}
		statesXml = null;
	}

	/**
	 * Get the label of this {@code Character}.
	 * 
	 * @return the label of this {@code Character}
	 */
	@XmlAttribute
	@Nullable
	public String getLabel() {
		return label;
	}

	@XmlAttribute
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

	@XmlElement(name = "state")
	protected Set<StandardState> getStatesXml() {
		return statesXml;
	}

	@Override
	public void setInNeedOfNewVersion() {
		if (parent != null) {
			parent.setInNeedOfNewVersion();
		}
		super.setInNeedOfNewVersion();
	}

	/** {@inheritDoc} */
	public void setLabel(final String label) {
		Preconditions.checkNotNull(label);
		if (label.equals(this.label)) {
			// they're the same, nothing to do.
		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
	}

	public void setMesquiteId(final String mesquiteId) {
		checkNotNull(mesquiteId);
		if (mesquiteId.equals(getMesquiteId())) {

		} else {
			this.mesquiteId = mesquiteId;
			setInNeedOfNewVersion();
		}
	}

	/** {@inheritDoc} */
	public void setParent(
			@CheckForNull final StandardMatrix parent) {
		this.parent = parent;
	}

	/**
	 * Constructs a <code>String</code> with all attributes in name = value
	 * format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	@Override
	public String toString() {
		final String TAB = "";

		final StringBuilder retValue = new StringBuilder();

		retValue.append("Character(").append("label=").append(this.label)
				.append(TAB).append(")");

		return retValue.toString();
	}
}
