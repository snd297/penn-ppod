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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A state of a {@link Character}. Represents things like "absent", "short", and
 * "long" for some abstractCharacter, say "proboscis".
 * <p>
 * A {@code CategoricalState} can belong to exactly one {@code Character}.
 * <p>
 * This is <em>not</em> a {@link UUPPodEntity} because its uniqueness is a
 * function of its {@link Character} + {@link #getStateNumber()}
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = CategoricalState.TABLE)
public class CategoricalState extends CharacterState {

	/**
	 * Orders character states by the natural ordering of
	 * {@link CharacterState#getStateNumber()}.
	 * <p>
	 * We use an external comparator for {@code CharacterState} instead of
	 * implementing {@code CharacterStateComparator} because we don't want to
	 * have to worry (as much) about being incompatible with equals. See
	 * {@link Comparable} and {@link CharacterStateComparator} for information
	 * about that.
	 * <p>
	 * Note that the state number of a {@code CharacterState} is immutable, so
	 * this comparator should be safe to use in a {@code SortedSet}.
	 */
	public static class CharacterStateComparator implements
			java.util.Comparator<CategoricalState>, Serializable {

		private static final long serialVersionUID = 1L;

		public int compare(final CategoricalState o1, final CategoricalState o2) {
			checkNotNull(o1);
			checkArgument(o1.getStateNumber() != null,
					"o1.getStateNumber() == null");

			checkNotNull(o2);
			checkArgument(o2.getStateNumber() != null,
					"o2.getStateNumber() == null");

			return o2.getStateNumber() - o1.getStateNumber();
		}
	}

	/**
	 * For assisted injections.
	 */
	public static interface IFactory {

		/**
		 * Create a character state with the given state number.
		 * 
		 * @param stateNumber the state number for the new state
		 * @return the new state
		 */
		CategoricalState create(Integer stateNumber);
	}

	/**
	 * {@link Function} wrapper of {@link #getStateNumber()}.
	 */
	public static final Function<CategoricalState, Integer> getStateNumber = new Function<CategoricalState, Integer>() {

		public Integer apply(final CategoricalState from) {
			return from.getStateNumber();
		}

	};

	/** The name of this entity's table. */
	protected final static String TABLE = "CHARACTER_STATE";

	/** To be used for foreign key names. */
	protected final static String ID_COLUMN = TABLE + "_ID";

	/**
	 * The column where the stateNumber is stored. Intentionally
	 * package-private.
	 */
	protected final static String STATE_NUMBER_COLUMN = "STATE_NUMBER";

	/**
	 * The {@code Character} of which this is a state.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = Character.ID_COLUMN)
	@CheckForNull
	private Character character;

	/**
	 * Label for this state. Things like <code>"absent"</code>,
	 * <code>"short"</code>, and <code>"long"</code>.
	 */
	@Column(name = LABEL_COLUMN, nullable = false)
	@CheckForNull
	private String label;

	/**
	 * The state number of this {@code CharacterState}. This is the core value
	 * of these objects.
	 */
	@Column(name = STATE_NUMBER_COLUMN, nullable = false, updatable = false)
	@CheckForNull
	private Integer stateNumber;

	CategoricalState() {}

	@Inject
	CategoricalState(@Assisted final Integer stateNumber) {
		checkNotNull(stateNumber);
		this.stateNumber = stateNumber;
	}

	@Override
	public void accept(final IVisitor visitor) {
		super.accept(visitor);
		visitor.visit(this);
	}

	/**
	 * Get this abstractCharacter owning abstractCharacter. Will be {@code null}
	 * when newly constructed.
	 * 
	 * @return this abstractCharacter stateNumber's owning abstractCharacter
	 */
	@Nullable
	public Character getCharacter() {
		return character;
	}

	/**
	 * Get the character state label.
	 * 
	 * @return the character state label
	 */
	@XmlAttribute(required = true)
	@Nullable
	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * See {@link Unmarshaller} javadoc on <em>Unmarshal Event Callbacks</em>.
	 * 
	 * @param u see {@code Unmarshaller} javadoc on
	 *            <em>Unmarshal Event Callbacks</em>
	 * @param parent see {@code Unmarshaller} javadoc on
	 *            <em>Unmarshal Event Callbacks</em>
	 */
	// public void afterUnmarshal(final Unmarshaller u, final Object parent) {
	// We do this in AbstractCharacter - why? Does it have to do with the fact
	// that
	// CharacterState's are stored in AbstractCharacter in a Map?
	//
	// if (parent instanceof AbstractCharacter) {
	// setCharacter((AbstractCharacter) parent);
	// }
	// super.afterUnmarshal(u, parent);
	// }

	/**
	 * Get the integer value of this abstractCharacter stateNumber. The integer
	 * value is the heart of the class.
	 * <p>
	 * {@code null} when the object is created.
	 * 
	 * @return get the integer value of this abstractCharacter stateNumber
	 */
	@XmlAttribute
	@Nullable
	public Integer getStateNumber() {
		return stateNumber;
	}

	/**
	 * Set the <code>character</code> property of this {@code CharacterState} to
	 * <code>character</code>.
	 * <p>
	 * This is intended to be {@code protected} and called from places allowed
	 * to manage the {@code Character<->CharacterState} relationship.
	 * <p>
	 * {@code character} being {@code null} signifies that the relationship, if
	 * it exists, is being severed.
	 * 
	 * @param character see description.
	 * 
	 * @return this
	 */
	protected CategoricalState setCharacter(
			@CheckForNull final Character character) {
		this.character = character;
		return this;
	}

	/**
	 * Mark this object as needed a new pPOD version.
	 * 
	 * @return this {@code CharacterState}
	 */
	@Override
	public CategoricalState setInNeedOfNewPPodVersionInfo() {
		if (character != null) {
			character.setInNeedOfNewPPodVersionInfo();
		}
		super.setInNeedOfNewPPodVersionInfo();
		return this;
	}

	/**
	 * Set the label.
	 * 
	 * @param label the label
	 * 
	 * @return this
	 */
	public CategoricalState setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(getLabel())) {

		} else {
			this.label = label;
			setInNeedOfNewPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Set the integer value of this state.
	 * <p>
	 * {@code stateNumber} must be an {@code Integer} and not an {@code int} to
	 * play nicely with JAXB.
	 * 
	 * @param stateNumber the integer value to use for this state
	 * 
	 * @return this
	 */
	protected CategoricalState setStateNumber(final Integer stateNumber) {
		this.stateNumber = stateNumber;
		return this;
	}

}
