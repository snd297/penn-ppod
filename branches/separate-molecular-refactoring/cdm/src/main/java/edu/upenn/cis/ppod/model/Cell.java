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
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;

import edu.upenn.cis.ppod.modelinterfaces.ICell;
import edu.upenn.cis.ppod.modelinterfaces.IMatrix;

/**
 * A cell contains {@code CharacterState}s.
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class Cell<S extends CharacterState> extends
		PPodEntity implements ICell, Iterable<S> {

	/**
	 * The different types of {@code CategoricalCell}: single, polymorphic,
	 * uncertain, unassigned, or inapplicable.
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

	protected static final String TYPE_COLUMN = "TYPE";

	/**
	 * Tells us that this {@code Cell} has been unmarshalled and still needs to
	 * have {@code getStates()} populated with {@code getXmlStates()}.
	 */
	@Transient
	private boolean afterMarshalNeedsToBeCalled = false;

	/** Position in a {@link CharacterStateRow}. */
	@Column(name = "POSITION", nullable = false)
	@CheckForNull
	private Integer position;

	/**
	 * Does this cell have a single state?, multiple states?, is it unassigned?,
	 * or inapplicable?
	 */
	@Column(name = TYPE_COLUMN, nullable = false)
	@Enumerated(EnumType.ORDINAL)
	@CheckForNull
	private Type type;

	/**
	 * Take actions after unmarshalling that need to occur after
	 * {@link #afterUnmarshal(Unmarshaller, Object)} is called - specifically,
	 * after {@code @XmlIDRef} elements are resolved
	 */
	@Override
	public void afterUnmarshal() {
		super.afterUnmarshal();
		if (afterMarshalNeedsToBeCalled) {
			afterMarshalNeedsToBeCalled = false;

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
		setAfterMarshalNeedsToBeCalled(true);
	}

	/**
	 * @throws IllegalStateException if the type has not been set
	 */
	@Override
	public boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {

		// Let's not marshal it if it's in a bad state
		checkState(getType() != null, "can't marshal a cell without a type");

		getXmlStates().addAll(getStatesAvoidingDB());
		return super.beforeMarshal(marshaller);

	}

	/**
	 * Clear all {@link CharacterState} info.
	 */
	protected void clearStates() {
		if (getFirstState() == null) {
			// Should be all clear, but let's check for programming errors
			if (getStates() != null && getStates().size() != 0) {
				throw new AssertionError(
						"programming error: firstate == null && states != null && states.size() != 0");
			}
		} else {
			setFirstState(null);
			if (getStates() != null) {
				getStates().clear();
			}
			setInNeedOfNewPPodVersionInfo();
		}
	}

	/**
	 * Created for testing purposes.
	 */
	boolean getAfterMarshalNeedsToBeCalled() {
		return afterMarshalNeedsToBeCalled;
	}

	/**
	 * Get the cached first state of this cell.
	 * 
	 * @return the cached first state of this cell
	 */
	protected abstract S getFirstState();

	/**
	 * Get this cell's position value.
	 * <p>
	 * Generally, the position of a cell should be obtained through
	 * {@link CharacterStateRow#getCellPosition(CategoricalCell)}.
	 * 
	 * @return this cell's position value
	 */
	@CheckForNull
	protected Integer getPosition() {
		return position;
	}

	/**
	 * The row that owns this cell.
	 * <p>
	 * We'll tolerate a wildcard here.
	 * 
	 * @return the row that owns this cell
	 */
	abstract Row<? extends Cell<? extends S>> getRow();

	protected abstract Set<S> getStates();

	/**
	 * @throws IllegalStateException if the type of this cell has not been
	 *             assigned
	 */
	protected Set<S> getStatesAvoidingDB() {
		checkState(getType() != null,
				"type has yet to be assigned for this cell");

		// One may reasonably ask why we don't just do the
		// afterUnmarshalVisitor's work here. Answer: We don't want to encourage
		// bad habits.
		checkState(
				!afterMarshalNeedsToBeCalled,
				"afterMarshalNeedsToBeCalled == true, has the afterUnmarshal visitor been dispatched?");
		switch (getType()) {
			// Don't hit states unless we have too
			case INAPPLICABLE:
			case UNASSIGNED:
				return Collections.emptySet();
			case SINGLE:
				final Set<S> states = newHashSet();
				states.add(getFirstState());
				return states;
			case POLYMORPHIC:
			case UNCERTAIN:

				// We have to hit states, which we want to avoid as much as
				// possible since it will trigger a database hit, which in the
				// aggregate
				// is expensive since there're are so many cells.
				if (getStates() == null) {
					return Collections.emptySet();
				}
				return getStates();

			default:
				throw new AssertionError("Unknown CharacterState.Type: " + type);
		}
	}

	/**
	 * Get the number of states that this cell contains.
	 * 
	 * @return the number of states that this cell contains
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
				return getStatesAvoidingDB().size();
			default:
				throw new AssertionError("Unknown CharacterState.Type: " + type);
		}
	}

	/**
	 * Get the type of this cell. {@code null} when this object is constructed.
	 * 
	 * @return the {@code Type}
	 */
	@XmlAttribute
	@Nullable
	public Type getType() {
		return type;
	}

	/**
	 * Used for serialization so we don't have to hit {@code states} directly
	 * and thereby cause unwanted database hits.
	 */
	@CheckForNull
	protected abstract Set<S> getXmlStates();

	/**
	 * Created for testing purposes.
	 */
	Cell<S> setAfterMarshalNeedsToBeCalled(
			final boolean xmlStatesNeedsToBePutIntoStates) {
		this.afterMarshalNeedsToBeCalled = xmlStatesNeedsToBePutIntoStates;
		return this;
	}

	protected abstract Cell<S> setFirstState(
			@CheckForNull final S firstState);

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

	@Override
	public Cell<S> setInNeedOfNewPPodVersionInfo() {
		final Row<?> row = getRow();
		if (row != null) {
			row.setInNeedOfNewPPodVersionInfo();
			final IMatrix matrix = row.getMatrix();
			if (matrix != null) {

				// so FindBugs knows that it's okay
				final Integer position = getPosition();
				checkState(position != null,
						"cell has no position, but is a part of a matrix");
				matrix.resetColumnPPodVersion(position);
			}
		}
		super.setInNeedOfNewPPodVersionInfo();
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
	 * Set the position.
	 * 
	 * @param position the position to set, pass in {@code null} if the cell is
	 *            no longer part of a row
	 * 
	 * @return this
	 */
	protected Cell<S> setPosition(
			@CheckForNull final Integer position) {
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
	 * Add a set of {@code CharacterState}s to this {@code CategoricalCell}.
	 * <p>
	 * Assumes that none of {@code states} is in a Hibernate-detached state.
	 * <p>
	 * This object makes its own copy of {@code states}.
	 * 
	 * @param states to be added. Each must not be in a detached state.
	 * 
	 * @return this
	 */
	protected abstract Cell<S> setTypeAndStates(final Type type,
			Set<? extends S> states);

	/**
	 * Created for testing purposes.
	 */
	Cell<S> setTypeAndXmlStates(
			final Cell.Type type,
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
		setTypeAndStates(Cell.Type.UNASSIGNED, emptyStates);
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
		setTypeAndStates(Cell.Type.UNCERTAIN, uncertainStates);
		return this;
	}

	protected abstract Cell<S> unsetXmlStates();

}
