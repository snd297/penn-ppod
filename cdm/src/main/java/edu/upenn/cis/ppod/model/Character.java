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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.hibernate.annotations.Cascade;

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
	final static String TABLE = "PHYLO_CHARACTER";

	final static String ID_COLUMN = TABLE + "_ID";
	final static String LABEL_COLUMN = "LABEL";

	@Override
	public Character accept(final IVisitor visitor) {
		visitor.visit(this);
		for (final CharacterState state : getStates().values()) {
			state.accept(visitor);
		}
		super.accept(visitor);
		return this;
	}

	/**
	 * The states that this character can have. For example, 0->"absent",
	 * 1->"present", 2->"one", or 3->"two". NOTE: it is legal to have
	 * non-contiguous integers in the keys - so, for example, you might have 0,
	 * 2, and 3.
	 */
	@OneToMany(mappedBy = "character")
	@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@MapKey(name = "stateNumber")
	private final Map<Integer, CharacterState> states = newHashMap();

	/**
	 * The matrices that hold a reference to this {@code Character}. This is
	 * really only a many-to-many for Molecular matrices. For standard matrices,
	 * it is many-to-one.
	 */
	@ManyToMany(mappedBy = "characters")
	private final Set<CharacterStateMatrix> matrices = newHashSet();

	/**
	 * The non-unique label of this {@code Character}.
	 */
	@Column(name = LABEL_COLUMN, nullable = false)
	private String label;

	/**
	 * Default constructor for (at least) Hibernate.
	 */
	Character() {}

	/**
	 * Add {@code matrix} to this {@code Character}'s matrices.
	 * <p>
	 * Not public because it's meant to be called from classes who create the
	 * {@code Character}<-> {@code CharacterStateMatrix} relationship.
	 * 
	 * @param matrix to be added.
	 * @return <code>true</code> if <code>matrix</code> was not there before,
	 *         <code>false</code> otherwise
	 */
	protected boolean addMatrix(final CharacterStateMatrix matrix) {
		Preconditions.checkNotNull(matrix);
		return matrices.add(matrix);
	}

	/**
	 * Add <code>state</code> into this <code>Character</code>.
	 * <p>
	 * Calling this handles both sides of the <code>Character</code><->
	 * <code>CharacterState</code>s. relationship.
	 * 
	 * @param state what we're adding
	 * @return <code>state</code>
	 */
	public CharacterState addState(final CharacterState state) {
		Preconditions.checkNotNull(state);
		states.put(state.getStateNumber(), state);
		state.setCharacter(this);
		resetPPodVersionInfo();
		return state;
	}

	/**
	 * See {@link Unmarshaller}.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		if (getStates().size() > 0
				&& get(getStates().values(), 0).getCharacter() == null) {
			for (final CharacterState state : getStates().values()) {
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
	 * Get the matrices in which this character is used.
	 * 
	 * @return the matrices to which this character belongs
	 */
	public Set<CharacterStateMatrix> getMatrices() {
		return matrices;
	}

	/**
	 * Return an unmodifiable view of this <code>Character</code>'s states.
	 * 
	 * @return an unmodifiable view of this {@code Character}'s states
	 */
	public Map<Integer, CharacterState> getStates() {
		return Collections.unmodifiableMap(states);
	}

	/**
	 * Get a mutable reference to the states.
	 * 
	 * @return a mutable reference to the states
	 */
	@XmlElementWrapper(name = "states")
	protected Map<Integer, CharacterState> getStatesMutable() {
		return states;
	}

	/**
	 * Remove {@code matrix} from this {@code Character}'s matrices.
	 * <p>
	 * Not public because it's meant to be called from classes who create the
	 * {@code Character}<-> {@code CharacterStateMatrix} relationship.
	 * 
	 * @param matrix to be removed.
	 * @return <code>true</code> if <code>matrix</code> was there to be removed,
	 *         <code>false</code> otherwise
	 */
	protected boolean removeMatrix(final CharacterStateMatrix matrix) {
		return matrices.remove(matrix);
	}

	@Override
	public Character resetPPodVersionInfo() {
		for (final CharacterStateMatrix matrix : matrices) {
			matrix.resetPPodVersionInfo();
		}
		super.resetPPodVersionInfo();
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
			resetPPodVersionInfo();
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
