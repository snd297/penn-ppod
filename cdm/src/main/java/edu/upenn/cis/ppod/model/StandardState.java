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
import static com.google.common.base.Preconditions.checkState;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.imodel.IStandardCharacter;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A stateNumber of a {@link StandardCharacter}. Represents things like
 * "absent", "short", and "long" for some character, say "proboscis".
 * <p>
 * A {@code StandardState} can belong to exactly one {@code StandardCharacter}.
 * <p>
 * This is <em>not</em> a {@link UUPPodEntity} because its uniqueness is a
 * function of its {@link StandardCharacter} + {@link #getStateNumber()}
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StandardState.TABLE)
public class StandardState extends PPodEntityWDocId implements IStandardState {

	public static class Adapter extends
			XmlAdapter<StandardState, IStandardState> {

		@Override
		public StandardState marshal(final IStandardState state) {
			return (StandardState) state;
		}

		@Override
		public IStandardState unmarshal(final StandardState state) {
			return state;
		}
	}

	/** The name of this entity's table. */
	public final static String TABLE = "STANDARD_STATE";

	/** For foreign keys that point at this table. */
	public final static String JOIN_COLUMN = TABLE + "_ID";

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
	 * of these objects. Write-once-read-many.
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
	@ManyToOne(fetch = FetchType.LAZY, optional = false,
			targetEntity = StandardCharacter.class)
	@JoinColumn(name = StandardCharacter.JOIN_COLUMN)
	@CheckForNull
	private IStandardCharacter parent;

	StandardState() {}

	@Inject
	StandardState(@Assisted final Integer stateNumber) {
		checkNotNull(stateNumber);
		this.stateNumber = stateNumber;
	}

	@Override
	public void accept(final IVisitor visitor) {
		super.accept(visitor);
		visitor.visitStandardState(this);
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
	 * Get this character owning character. Will be {@code null} when newly
	 * constructed.
	 * 
	 * @return this character stateNumber's owning character
	 */
	@Nullable
	public IStandardCharacter getParent() {
		return parent;
	}

	/**
	 * Get the integer value of this character stateNumber. The integer value is
	 * the heart of the class.
	 * <p>
	 * {@code null} when the object is created. Never {@code null} for
	 * persistent objects.
	 * 
	 * @return get the integer value of this character stateNumber
	 */
	@XmlAttribute
	@Nullable
	public Integer getStateNumber() {
		return stateNumber;
	}

	@Override
	public void setInNeedOfNewVersion() {
		if (parent != null) {
			parent.setInNeedOfNewVersion();
		}
		super.setInNeedOfNewVersion();
	}

	/**
	 * Set the label.
	 * 
	 * @param label the label
	 * 
	 * @return this
	 */
	public IStandardState setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(getLabel())) {

		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
		return this;
	}

	/**
	 * Set the parent {@code StandardCharacter}.
	 * <p>
	 * Intentionally package-private and meant to be called from
	 * {@link StandardCharacter}.
	 * <p>
	 * {@code parent} being {@code null} signifies that the relationship, if it
	 * exists, is being severed.
	 * 
	 * @param character see description.
	 */
	public void setParent(
			@CheckForNull final IStandardCharacter parent) {
		this.parent = parent;
	}

	/**
	 * Set the integer value of this state.
	 * <p>
	 * {@code stateNumber} must be an {@code Integer} and not an {@code int} to
	 * play nicely with JAXB.
	 * <p>
	 * This method was created for JAXB - we'd rather if the state number had no
	 * setter.
	 * 
	 * @param stateNumber the integer value to use for this state
	 * 
	 * @return this
	 */
	protected IStandardState setStateNumber(final Integer stateNumber) {
		checkNotNull(stateNumber);
		checkState(this.stateNumber == null,
				"this.stateNumber is non-null: this is a WORM property.");
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
