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

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.google.common.base.Function;

import edu.umd.cs.findbugs.annotations.CheckForNull;

/**
 * A stateNumber of a {@link StandardCharacter}. Represents things like
 * "absent", "short", and "long" for some character, say "proboscis".
 * <p>
 * A {@code StandardState} can belong to exactly one {@code StandardCharacter}.
 * <p>
 * This is <em>not</em> a {@link UUPPodEntity} because its uniqueness is a
 * function of its {@link StandardCharacter} + {@link #getStateNumber()}
 * <p>
 * We keep the state number immutable so that the map of
 * stateno's->StandardState in StandardCharacter doesn't get the values out of
 * sycn with the keys.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StandardState.TABLE)
public class StandardState {

	/** The name of this entity's table. */
	public final static String TABLE = "standard_state";

	/** For foreign keys that point at this table. */
	public final static String ID_COLUMN = TABLE + "_id";

	/**
	 * The column where the stateNumber is stored. Intentionally
	 * package-private.
	 */
	public final static String STATE_NUMBER_COLUMN = "state_number";

	@CheckForNull
	private Long id;

	@CheckForNull
	private Integer version;

	/**
	 * {@link Function} wrapper of {@link #getStateNumber()}.
	 */
	public static final Function<StandardState, Integer> getStateNumber = new Function<StandardState, Integer>() {

		public Integer apply(final StandardState from) {
			return from.getStateNumber();
		}

	};

	/**
	 * The state number of this {@code CharacterState}. This is the core value
	 * of these objects. Write-once-read-many.
	 */
	private Integer stateNumber;

	/**
	 * Label for this stateNumber. Things like <code>"absent"</code>,
	 * <code>"short"</code>, and <code>"long"</code>
	 */
	@CheckForNull
	private String label;

	/**
	 * The {@code Character} of which this is a state.
	 */
	@CheckForNull
	private StandardCharacter parent;

	/**
	 * For Hibernate.
	 */
	StandardState() {}

	public StandardState(final Integer stateNumber) {
		this.stateNumber = checkNotNull(stateNumber);
	}

	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@Nullable
	public Long getId() {
		return id;
	}

	/**
	 * Get this character stateNumber's label.
	 * 
	 * @return this character stateNumber's label
	 */
	@Column(name = "label", nullable = false)
	@Index(name = "IDX_LABEL")
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
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = StandardCharacter.ID_COLUMN)
	@Nullable
	public StandardCharacter getParent() {
		return parent;
	}

	/**
	 * Get the integer value of this character stateNumber. The integer value is
	 * the heart of the class.
	 * <p>
	 * {@code null} when the object is created. Never {@code null} for
	 * persistent objects.
	 * <p>
	 * 
	 * @return get the integer value of this character stateNumber
	 */
	@Column(name = STATE_NUMBER_COLUMN, nullable = false, updatable = false)
	@Nullable
	public Integer getStateNumber() {
		return stateNumber;
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

	/**
	 * Set the label.
	 * 
	 * @param label the label
	 */
	public void setLabel(final String label) {
		this.label = label;
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
			@CheckForNull final StandardCharacter parent) {
		this.parent = parent;
	}

	@SuppressWarnings("unused")
	private void setStateNumber(final Integer stateNumber) {
		this.stateNumber = stateNumber;
	}

	/**
	 * @param version the version to set
	 */
	@SuppressWarnings("unused")
	private void setVersion(final Integer version) {
		this.version = version;
	}
}
