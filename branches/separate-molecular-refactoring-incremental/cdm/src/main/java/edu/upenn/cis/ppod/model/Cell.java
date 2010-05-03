package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * A cell of states.
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class Cell<S> extends PPodEntity implements Iterable<S> {

	/**
	 * The different types of {@code Cell}: single, polymorphic, uncertain,
	 * unassigned, or inapplicable.
	 * <p>
	 * Because we're storing these in the db as ordinals they will be:
	 * <ul>
	 * <li>{@code UNASSIGNED -> 0}</li>
	 * <li>{@code SINGLE -> 1}</li>
	 * <li>{@code POLYMORPHIC -> 2}</li>
	 * <li>{@code UNCERTAIN -> 3}</li>
	 * <li>{@code INAPPLICABLE -> 4}</li>
	 * </ul>
	 */
	public static enum Type {

		/** Inapplicable, usually written as a {@code "-"} in Nexus files. */
		INAPPLICABLE,

		/**
		 * The cell is a conjunctions of states: <em>state1</em> and
		 * <em>state2</em> and ... and <em>stateN</em>.
		 */
		POLYMORPHIC,

		/**
		 * The cell has exactly one state.
		 */
		SINGLE,

		/** Unassigned, usually written as a {@code "?"} in Nexus files. */
		UNASSIGNED,

		/**
		 * The cell is a disjunction of states: <em>state1</em> or
		 * <em>state2</em> or ... or <em>stateN</em>.
		 */
		UNCERTAIN;
	}

	static final String TYPE_COLUMN = "TYPE";

	@Column(name = "POSITION", nullable = false)
	@CheckForNull
	private Integer position;

	@Column(name = TYPE_COLUMN, nullable = false)
	@Enumerated(EnumType.ORDINAL)
	@CheckForNull
	protected Type type;

	@CheckForNull
	protected Integer getPosition() {
		return position;
	}

	@XmlAttribute
	@Nullable
	public Type getType() {
		return type;
	}

	protected Cell<S> setPosition(@CheckForNull final Integer position) {
		this.position = position;
		return this;
	}

	protected Cell<S> setType(final Type type) {
		checkNotNull(type);
		this.type = type;
		return this;
	}

	/**
	 * Add a set of {@code CharacterState}s to this {@code CharacterStateCell}.
	 * <p>
	 * Assumes that none of {@code states} is in a detached state.
	 * <p>
	 * This object makes its own copy of {@code states}.
	 * 
	 * @param states to be added
	 * 
	 * @return this
	 */
	protected abstract Cell<S> setTypeAndStates(final Type type,
			final Set<? extends S> states);

	/**
	 * Set this cell's type to {@link Type#UNASSIGNED} to {@code
	 * Collections.EMPTY_SET}.
	 * 
	 * @return this
	 */
	public Cell<S> setUnassigned() {
		final Set<S> emptyStates = Collections.emptySet();
		setTypeAndStates(Type.UNASSIGNED, emptyStates);
		return this;
	}

	/**
	 * Set the type to uncertain with the given states.
	 * 
	 * @param uncertainStates the states
	 * 
	 * @return this
	 * 
	 * @throw IllegalArgumentException if {@code uncertainStates.size() < 2}
	 */
	public Cell<S> setUncertainStates(
			final Set<? extends S> uncertainStates) {
		checkNotNull(uncertainStates);
		checkArgument(uncertainStates.size() > 1,
				"uncertain states must be > 1");
		setTypeAndStates(Type.UNCERTAIN, uncertainStates);
		return this;
	}

	/**
	 * Set the type to polymorphic with the given states.
	 * 
	 * @param polymorphicStates the states
	 * 
	 * @return this
	 * 
	 * @throw IllegalArgumentException if {@code polymorphicStates.size() < 2}
	 */
	public Cell<S> setPolymorphicStates(
			final Set<? extends S> polymorphicStates) {
		checkNotNull(polymorphicStates);
		checkArgument(polymorphicStates.size() > 1,
				"polymorphic states must be > 1");
		setTypeAndStates(Type.POLYMORPHIC, polymorphicStates);
		return this;
	}

	/**
	 * Set the cell to have type {@link Type#SINGLE} and the given states.
	 * 
	 * @param state state to assign to this cell
	 * 
	 * @return this
	 */
	public Cell<S> setSingleState(final S state) {
		checkNotNull(state);

		// Was getting a warning if we didn't do a create and then add.
		final Set<S> states = newHashSet();
		states.add(state);
		setTypeAndStates(Type.SINGLE, states);
		return this;
	}

}
