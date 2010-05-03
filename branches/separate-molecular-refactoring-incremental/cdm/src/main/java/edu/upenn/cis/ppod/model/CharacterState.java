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
 * A stateNumber of a {@link Character}. Represents things like "absent",
 * "short", and "long" for some character, say "proboscis".
 * <p>
 * A {@code CharacterState} can belong to exactly one {@code Character}.
 * <p>
 * This is <em>not</em> a {@link UUPPodEntity} because its uniqueness is a
 * function of its {@link Character} + {@link #getStateNumber()}
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = CharacterState.TABLE)
public class CharacterState extends PPodEntityWXmlId {

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
			java.util.Comparator<CharacterState>, Serializable {

		private static final long serialVersionUID = 1L;

		public int compare(final CharacterState o1, final CharacterState o2) {
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
		CharacterState create(Integer stateNumber);
	}

	/**
	 * {@link Function} wrapper of {@link #getStateNumber()}.
	 */
	public static final Function<CharacterState, Integer> getStateNumber = new Function<CharacterState, Integer>() {

		public Integer apply(final CharacterState from) {
			return from.getStateNumber();
		}

	};

	/** The name of this entity's table. Intentionally package-private. */
	final static String TABLE = "CHARACTER_STATE";

	final static String ID_COLUMN = TABLE + "_ID";

	/**
	 * The column where the stateNumber is stored. Intentionally
	 * package-private.
	 */
	final static String STATE_NUMBER_COLUMN = "STATE_NUMBER";

	/**
	 * The column where the label is stored. Intentionally package-private.
	 */
	final static String LABEL_COLUMN = "LABEL";

	/**
	 * The state number of this {@code CharacterState}. This is the core value
	 * of these objects.
	 */
	@Column(name = STATE_NUMBER_COLUMN, nullable = false, updatable = false)
	@CheckForNull
	private Integer stateNumber;

	/**
	 * Label for this stateNumber. Things like <code>"absent"</code>,
	 * <code>"short"</code>, and <code>"long"</code>
	 */
	@Column(name = LABEL_COLUMN, nullable = false)
	@CheckForNull
	private String label;

	/**
	 * The {@code Character} of which this is a state.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = Character.ID_COLUMN)
	@CheckForNull
	private Character character;

	CharacterState() {}

	@Inject
	CharacterState(@Assisted final Integer stateNumber) {
		checkNotNull(stateNumber);
		this.stateNumber = stateNumber;
	}

	@Override
	public void accept(final IVisitor visitor) {
		super.accept(visitor);
		visitor.visit(this);
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
	// We do this in Character - why? Does it have to do with the fact that
	// CharacterState's are stored in Character in a Map?
	//
	// if (parent instanceof Character) {
	// setCharacter((Character) parent);
	// }
	// super.afterUnmarshal(u, parent);
	// }

	/**
	 * Get this character owning character. Will be {@code null} when newly
	 * constructed.
	 * 
	 * @return this character stateNumber's owning character
	 */
	@Nullable
	public Character getCharacter() {
		return character;
	}

	/**
	 * Get this character stateNumber's label.
	 * 
	 * @return this character stateNumber's label
	 */
	@XmlAttribute(required = true)
	@Nullable
	public String getLabel() {
		return label;
	}

	/**
	 * Get the integer value of this character stateNumber. The integer value is
	 * the heart of the class.
	 * <p>
	 * {@code null} when the object is created.
	 * 
	 * @return get the integer value of this character stateNumber
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
	 * This is intended to be package-private and called from
	 * {@link Character#addState(CharacterState)}.
	 * <p>
	 * {@code character} being {@code null} signifies that the relationship, if
	 * it exists, is being severed.
	 * 
	 * @param character see description.
	 * 
	 * @return this {@code CharacterState}
	 */
	CharacterState setCharacter(@Nullable final Character character) {
		this.character = character;
		return this;
	}

	/**
	 * Mark this object as needed a new pPOD version.
	 * 
	 * @return this {@code CharacterState}
	 */
	@Override
	public CharacterState setInNeedOfNewPPodVersionInfo() {
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
	public CharacterState setLabel(final String label) {
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
	protected CharacterState setStateNumber(final Integer stateNumber) {
		this.stateNumber = stateNumber;
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

		retValue.append("CharacterState(").append("stateNumber=").append(
				this.stateNumber).append(TAB).append("label=").append(
				this.label).append(TAB).append(")");

		return retValue.toString();
	}
}
