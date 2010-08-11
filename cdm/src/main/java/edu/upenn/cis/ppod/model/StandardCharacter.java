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

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
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
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.common.base.Preconditions;

import edu.upenn.cis.ppod.imodel.IStandardCharacter;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStandardState;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A standard character, aka a morphological character. For example,
 * "length_of_infraorb_canal".
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StandardCharacter.TABLE)
public class StandardCharacter extends UUPPodEntityWithDocId
		implements IStandardCharacter {

	public static class Adapter extends
			XmlAdapter<StandardCharacter, IStandardCharacter> {

		@Override
		public StandardCharacter marshal(final IStandardCharacter character) {
			return (StandardCharacter) character;
		}

		@Override
		public IStandardCharacter unmarshal(final StandardCharacter character) {
			return character;
		}
	}

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
	@ManyToOne(optional = false, targetEntity = StandardMatrix.class)
	@JoinColumn(
			name = StandardMatrix.JOIN_COLUMN,
			insertable = false,
			updatable = false)
	@CheckForNull
	private IStandardMatrix parent;

	/**
	 * The states that this character can have. For example, 0->"absent",
	 * 1->"present", 2->"one", or 3->"two". NOTE: it is legal to have
	 * non-contiguous integers in the keys - so, for example, you might have 0,
	 * 2, and 3.
	 */
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL,
			orphanRemoval = true, targetEntity = StandardState.class)
	@MapKey(name = "stateNumber")
	private final Map<Integer, IStandardState> states = newHashMap();

	@Transient
	private Set<IStandardState> statesXml;

	/**
	 * Default constructor for (at least) Hibernate.
	 */
	StandardCharacter() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitStandardCharacter(this);
		for (final IStandardState state : getStates()) {
			state.accept(visitor);
		}
		super.accept(visitor);
	}

	/** {@inheritDoc} */
	@CheckForNull
	public IStandardState addState(final IStandardState state) {
		Preconditions.checkNotNull(state);
		final IStandardState originalState =
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
			@CheckForNull final Unmarshaller u, final Object parent) {
		setParent((IStandardMatrix) parent);
		for (final IStandardState stateXml : statesXml) {
			stateXml.setParent(this);
			states.put(stateXml.getStateNumber(), stateXml);
		}
		statesXml = null;
	}

	@Override
	protected boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {
		statesXml = newHashSet();
		for (final IStandardState state : states.values()) {
			// Load it if it's a proxy ;-)
			state.getStateNumber();
			statesXml.add(state);
		}
		return true;
	}

	@Override
	protected void beforeUnmarshal(
			@CheckForNull final Unmarshaller u, final Object parent) {
		statesXml = newHashSet();
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

	/**
	 * Get the matrix that owns this character.
	 * <p>
	 * Will be {@code null} for newly created characters. Will never be
	 * {@code null} for characters in a persistent state.
	 * 
	 * @return the matrix that owns this character
	 */
	@Nullable
	public IStandardMatrix getParent() {
		return parent;
	}

	/** {@inheritDoc} */
	@CheckForNull
	public IStandardState getState(final Integer stateNumber) {
		checkNotNull(stateNumber);
		return states.get(stateNumber);
	}

	/** {@inheritDoc} */
	public Set<IStandardState> getStates() {
		return newHashSet(states.values());
	}

	@XmlElement(name = "state")
	protected Set<IStandardState> getStatesXml() {
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
	public StandardCharacter setLabel(final String label) {
		Preconditions.checkNotNull(label);
		if (label.equals(this.label)) {
			// they're the same, nothing to do.
		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
		return this;
	}

	/** {@inheritDoc} */
	public void setParent(
			@CheckForNull final IStandardMatrix parent) {
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
