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

import static com.google.common.collect.Iterables.get;
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
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.google.common.base.Preconditions;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A standard character. For example, "length_of_infraorb_canal".
 * <p>
 * This class has a non-accessible {@code @XmlId} (only used by the marshaller
 * and unmarshaller) and so doesn't extend from {@code UUPPodEntityWXmlId}.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = Character.TABLE)
public class Character extends UUPPodEntityWXmlId {

	/**
	 * We don't call the table {@code "CHARACTER"} because that causes problems
	 * with the generated SQL: {@code "CHARACTER"} means something to at least
	 * MySQL.
	 */
	public final static String TABLE = "PHYLO_CHARACTER";

	public final static String JOIN_COLUMN = TABLE + "_ID";

	final static String LABEL_COLUMN = "LABEL";

	/**
	 * The non-unique label of this {@code Character}.
	 */
	@Column(name = LABEL_COLUMN, nullable = false)
	private String label;

	/**
	 * The matrices that hold a reference to this {@code Character}. This is
	 * really only a many-to-many for Molecular matrices. For standard matrices,
	 * it is many-to-one.
	 */
	@ManyToOne
	@JoinColumn(name = CharacterStateMatrix.JOIN_COLUMN, insertable = false, updatable = false, nullable = false)
	private CharacterStateMatrix matrix;

	/**
	 * The states that this character can have. For example, 0->"absent",
	 * 1->"present", 2->"one", or 3->"two". NOTE: it is legal to have
	 * non-contiguous integers in the keys - so, for example, you might have 0,
	 * 2, and 3.
	 */
	@OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
	@MapKey(name = "stateNumber")
	private final Map<Integer, CharacterState> states = newHashMap();

	/**
	 * Default constructor for (at least) Hibernate.
	 */
	Character() {}

	@Override
	public void accept(final IVisitor visitor) {
		for (final CharacterState state : getStatesModifiable().values()) {
			state.accept(visitor);
		}
		super.accept(visitor);
		visitor.visit(this);
	}

	/**
	 * Set{@code matrix} as this character's parent.
	 * <p>
	 * Not public because it's meant to be called from classes who create the
	 * {@code Character}<-> {@code CharacterStateMatrix} relationship.
	 * <p>
	 * Unlike {@code MolecularCharacter}s, standard characters can belong to
	 * exactly one matrix.
	 * 
	 * @param matrix to be added.
	 * @return <code>true</code> if <code>matrix</code> was not there before,
	 *         <code>false</code> otherwise
	 * 
	 * @throw IllegalStateException if this character already belongs to a
	 *        different matrix - it's fine to keep calling this method with the
	 *        same matrix
	 */
	protected Character setMatrix(
			@CheckForNull final CharacterStateMatrix matrix) {
		this.matrix = matrix;
		return this;
	}

	/**
	 * See {@link Unmarshaller}.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@Override
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		super.afterUnmarshal(u, parent);
		setMatrix((CharacterStateMatrix) parent);
		if (getStatesModifiable().size() > 0
				&& get(getStatesModifiable().values(), 0).getCharacter() == null) {
			for (final CharacterState state : getStatesModifiable().values()) {
				state.setCharacter(this);
			}
		}
	}

	@Override
	public boolean beforeMarshal(final Marshaller marshaller) {
		super.beforeMarshal(marshaller);
		return true;
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
	 * Get the matrices that refer to this character.
	 * 
	 * @return the matrices that refer to this character
	 */
	public CharacterStateMatrix getMatrix() {
		return matrix;
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
	@CheckForNull
	public CharacterState getState(final Integer stateNumber) {
		return getStatesModifiable().get(stateNumber);
	}

	/**
	 * Get the states of this character.
	 * 
	 * @return the states of this character.
	 */
	public Set<CharacterState> getStates() {
		return newHashSet(states.values());
	}

	/**
	 * Get a modifiable reference to the states.
	 * 
	 * @return a mutable reference to the states
	 */
	@XmlElementWrapper(name = "states")
	protected Map<Integer, CharacterState> getStatesModifiable() {
		return states;
	}

	/**
	 * Add <code>state</code> into this <code>Character</code>.
	 * <p>
	 * Calling this handles both sides of the <code>Character</code><->
	 * <code>CharacterState</code>s. relationship.
	 * 
	 * @param state what we're adding
	 * @return the state that was associated with {@code state.getStateNumber()}
	 *         or {@code null} if there was no such state.
	 */
	@CheckForNull
	public CharacterState addState(final CharacterState state) {
		Preconditions.checkNotNull(state);
		final CharacterState originalState = states.put(state.getStateNumber(),
				state);
		if (state == originalState) {
			return originalState;
		}

		if (originalState != null) {
			originalState.setCharacter(null);
		}
		state.setCharacter(this);
		setInNeedOfNewVersion();
		return originalState;
	}

	@Override
	public Character setInNeedOfNewVersion() {
		if (matrix != null) {
			matrix.setInNeedOfNewVersion();
		}
		super.setInNeedOfNewVersion();
		return this;
	}

	/**
	 * Set the label of this <code>StdChar</code>.
	 * 
	 * @param label the value for the label.
	 * @return this <code>Character</code>.
	 */
	public Character setLabel(final String label) {
		Preconditions.checkNotNull(label);
		if (label.equals(this.label)) {
			// they're the same, nothing to do.
		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
		return this;
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
