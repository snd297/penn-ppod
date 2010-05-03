package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.xml.bind.Unmarshaller;
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

	/**
	 * Tells us that this {@code Cell} has been unmarshalled and still needs to
	 * have {@code states} populated with {@code xmlStates}.
	 */
	@Transient
	private boolean xmlStatesNeedsToBePutIntoStates = false;

	@Override
	public void afterUnmarshal() {
		super.afterUnmarshal();
		if (getXmlStatesNeedsToBePutIntoStates()) {
			setXmlStatesNeedsToBePutIntoStates(false);

			// Let's reset the type to make it consistent with states
			final Type xmlType = getType();
			this.type = null;
			setTypeAndStates(xmlType, getXmlStates());
			unsetXmlStates();
		}
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@Override
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		super.afterUnmarshal(u, parent);
		setXmlStatesNeedsToBePutIntoStates(true);
	}

	/**
	 * Clear the cell out of states. Does not touch the type.
	 */
	protected void clearStates() {
		if (getFirstState() == null) {
			// Should be all clear, but let's check for programming errors
			if (getStatesRaw() != null && getStatesRaw().size() != 0) {
				throw new AssertionError(
						"programming error: firstate == null && states != null && states.size() != 0");
			}
		} else {
			unsetFirstState();

			if (getStatesRaw() != null) {
				getStatesRaw().clear();
			}
			setInNeedOfNewPPodVersionInfo();
		}
	}

	/**
	 * We cache the first state, since this is the most common case.
	 * <p>
	 * Will be {@code null} if this is a {@link Type#INAPPLICABLE} or
	 * {@link Type#UNASSIGNED}.
	 */
	@CheckForNull
	protected abstract S getFirstState();

	@CheckForNull
	protected Integer getPosition() {
		return position;
	}

	protected Set<S> getStates() {
		checkState(getType() != null,
				"type has yet to be assigned for this cell");

		// One may reasonably ask why we don't just do the
		// AfterUnmarshalVisitor's work here. Answer: We don't want to encourage
		// bad habits.
		checkState(
				!getXmlStatesNeedsToBePutIntoStates(),
				"xmlStateNeedsToBePutIntoStates == true, has the afterUnmarshal visitor been dispatched?");
		switch (getType()) {
			// Don't hit states unless we have too
			case INAPPLICABLE:
			case UNASSIGNED:
				return Collections.emptySet();
			case SINGLE:
				final Set<S> firstStateInASet = newHashSet();
				firstStateInASet.add(getFirstState());
				return firstStateInASet;
			case POLYMORPHIC:
			case UNCERTAIN:

				// We have to hit states, which we want to avoid as much as
				// possible since it will trigger a database hit, which in the
				// aggregate
				// is expensive since there're are so many cells.
				final Set<S> states = getStatesRaw();
				if (states == null) {
					return Collections.emptySet();
				}
				return states;

			default:
				throw new AssertionError("Unknown CharacterState.Type: " + type);
		}
	}

	protected abstract Set<S> getStatesRaw();

	/**
	 * Get the number of states in this cell.
	 * 
	 * @return the number of states in this cell
	 */
	public int getStatesSize() {
		checkState(getType() != null,
				"type has yet to be assigned for this cell");
		switch (getType()) {
			case INAPPLICABLE:
			case UNASSIGNED:
				return 0;
			case SINGLE:
				return 1;
			case POLYMORPHIC:
			case UNCERTAIN:
				return getStates().size();
			default:
				throw new AssertionError("Unknown CharacterState.Type: " + type);
		}
	}

	@XmlAttribute
	@Nullable
	public Type getType() {
		return type;
	}

	protected abstract Set<S> getXmlStates();

	/**
	 * Package-private for testing.
	 */
	boolean getXmlStatesNeedsToBePutIntoStates() {
		return xmlStatesNeedsToBePutIntoStates;
	}

	/**
	 * Set this cell's type to {@link Type#INAPPLICABLE} to {@code
	 * Collections.EMPTY_SET}.
	 * 
	 * @return this
	 */
	public Cell<S> setInapplicable() {
		final Set<S> emptyStates = Collections.emptySet();
		setTypeAndStates(Type.INAPPLICABLE, emptyStates);
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

	protected Cell<S> setPosition(@CheckForNull final Integer position) {
		this.position = position;
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
	 * Created for testing purposes.
	 */
	Cell<S> setTypeAndXmlStates(final Type type,
			final Set<? extends S> xmlStates) {
		checkNotNull(type);
		checkNotNull(xmlStates);
		setType(type);
		getXmlStates().clear();
		getXmlStates().addAll(xmlStates);
		return this;
	}

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
	 * Package-private for testing.
	 */
	Cell<S> setXmlStatesNeedsToBePutIntoStates(
			final boolean xmlStatesNeedsToBePutIntoStates) {
		this.xmlStatesNeedsToBePutIntoStates = xmlStatesNeedsToBePutIntoStates;
		return this;
	}

	protected abstract Cell<S> unsetFirstState();

	protected abstract Cell<S> unsetXmlStates();
}
