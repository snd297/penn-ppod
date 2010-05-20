package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.get;
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

import edu.upenn.cis.ppod.modelinterfaces.IMatrix;
import edu.upenn.cis.ppod.modelinterfaces.IRow;

/**
 * A cell.
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class Cell<E> extends PPodEntity {

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
	private Type type;

	/**
	 * Used for serialization so we don't have to hit {@code states} directly
	 * and thereby cause unwanted database hits.
	 */
	@Transient
	@CheckForNull
	private Set<E> xmlElements = null;

	/**
	 * Tells us that this {@code Cell} has been unmarshalled and still needs to
	 * have {@code states} populated with {@code xmlStates}.
	 */
	@Transient
	private boolean xmlElementsNeedsToBePutIntoElements = false;

	Cell() {}

	@Override
	public void afterUnmarshal() {
		super.afterUnmarshal();
		if (getXmlElementsNeedToBePutIntoStates()) {
			setXmlElementsNeedToBePutIntoStates(false);

			getElementsRaw().addAll(getXmlElements());

			if (getElementsRaw().size() > 0) {
				setFirstElement(get(getElementsRaw(), 0));
			}

			setType(type);
			unsetXmlElements();
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
		setXmlElementsNeedToBePutIntoStates(true);
	}

	/**
	 * @throws IllegalStateException if the type has not been set
	 */
	@Override
	public boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {

		// Let's not marshal it if it's in a bad state
		checkState(getType() != null, "can't marshal a cell without a type");

		getXmlElements().addAll(getElements());
		return super.beforeMarshal(marshaller);

	}

	/**
	 * Clear the cell out of elements. Does not touch the type.
	 */
	protected void clearElements() {
		if (getFirstElement() == null) {
			// Should be all clear, but let's check for programming errors
			if (getElementsRaw() != null && getElementsRaw().size() != 0) {
				throw new AssertionError(
						"programming error: firstate == null && states != null && states.size() != 0");
			}
		} else {
			setFirstElement(null);

			if (getElementsRaw() != null) {
				getElementsRaw().clear();
			}
			setInNeedOfNewVersionInfo();
		}
	}

	/**
	 * Get the elements contained in this cell.
	 * 
	 * @return the elements contained in this cell
	 */
	public Set<E> getElements() {
		checkState(getType() != null,
				"type has yet to be assigned for this cell");

		// One may reasonably ask why we don't just do the
		// AfterUnmarshalVisitor's work here. Answer: We don't want to encourage
		// bad habits.
		checkState(
				!getXmlElementsNeedToBePutIntoStates(),
				"xmlStateNeedsToBePutIntoStates == true, has the afterUnmarshal visitor been dispatched?");
		switch (getType()) {
		// Don't hit states unless we have too
		case INAPPLICABLE:
		case UNASSIGNED:
			return Collections.emptySet();
		case SINGLE:
			final Set<E> firstStateInASet = newHashSet();
			firstStateInASet.add(getFirstElement());
			return firstStateInASet;
		case POLYMORPHIC:
		case UNCERTAIN:

			// We have to hit states, which we want to avoid as much as
			// possible since it will trigger a database hit, which in the
			// aggregate
			// is expensive since there're are so many cells.
			final Set<E> elements = getElementsRaw();
			if (elements == null) {
				throw new AssertionError("getElementsRaw() == null");
			}
			if (elements.size() < 2) {
				throw new AssertionError("type is " + getType()
												+ " and getElementsRaw() has "
												+ elements.size() + " elements");
			}
			return Collections.unmodifiableSet(elements);

		default:
			throw new AssertionError("Unknown Cell.Type: " + type);
		}
	}

	protected abstract Set<E> getElementsRaw();

	/**
	 * We cache the first state, since this is the most common case.
	 * <p>
	 * Will be {@code null} if this is a {@link Type#INAPPLICABLE} or
	 * {@link Type#UNASSIGNED}.
	 */
	@CheckForNull
	protected abstract E getFirstElement();

	@CheckForNull
	protected Integer getPosition() {
		return position;
	}

	@Nullable
	protected abstract IRow getRow();

	@XmlAttribute
	@Nullable
	public Type getType() {
		return type;
	}

	/**
	 * Used for serialization so we don't have to hit {@code states} directly
	 * and thereby cause unwanted database hits.
	 */
	protected Set<E> getXmlElements() {
		if (xmlElements == null) {
			xmlElements = newHashSet();
		}
		return xmlElements;
	}

	/**
	 * Package-private for testing.
	 */
	boolean getXmlElementsNeedToBePutIntoStates() {
		return xmlElementsNeedsToBePutIntoElements;
	}

	protected abstract Cell<E> setFirstElement(E firstElement);

	/**
	 * Set this cell's type to {@link Type#INAPPLICABLE} to {@code
	 * Collections.EMPTY_SET}.
	 * 
	 * @return this
	 */
	public Cell<E> setInapplicable() {
		final Set<E> emptyStates = Collections.emptySet();
		setTypeAndElements(Type.INAPPLICABLE, emptyStates);
		return this;
	}

	@Override
	public Cell<E> setInNeedOfNewVersionInfo() {
		final IRow row = getRow();
		if (row != null) {
			row.setInNeedOfNewVersionInfo();
			final IMatrix matrix = row.getMatrix();
			if (matrix != null) {

				// so FindBugs knows that it's okay
				final Integer position = getPosition();
				checkState(position != null,
						"cell has no position, but is a part of a matrix");
				matrix.resetColumnVersion(position);
			}
		}
		super.setInNeedOfNewVersionInfo();
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
	public Cell<E> setPolymorphicElements(
			final Set<? extends E> polymorphicElements) {
		checkNotNull(polymorphicElements);
		checkArgument(polymorphicElements.size() > 1,
				"polymorphic states must be > 1");
		setTypeAndElements(Type.POLYMORPHIC, polymorphicElements);
		return this;
	}

	protected Cell<E> setPosition(@CheckForNull final Integer position) {
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
	public Cell<E> setSingleElement(final E state) {
		checkNotNull(state);

		// Was getting a warning if we didn't do a create and then add.
		final Set<E> states = newHashSet();
		states.add(state);
		setTypeAndElements(Type.SINGLE, states);
		return this;
	}

	protected Cell<E> setType(final Type type) {
		checkNotNull(type);
		this.type = type;
		return this;
	}

	/**
	 * Add a set of {@code E} to this {@code Cell}.
	 * <p>
	 * Assumes that none of {@code elements} is in a detached state.
	 * <p>
	 * This object makes its own copy of {@code states}.
	 * 
	 * @param states to be added
	 * 
	 * @return this
	 */
	protected abstract Cell<E> setTypeAndElements(final Type type,
			final Set<? extends E> elements);

	/**
	 * Created for testing purposes.
	 */
	Cell<E> setTypeAndXmlElements(final Type type,
			final Set<? extends E> xmlStates) {
		checkNotNull(type);
		checkNotNull(xmlStates);
		setType(type);
		getXmlElements().clear();
		getXmlElements().addAll(xmlStates);
		setXmlElementsNeedToBePutIntoStates(true);
		return this;
	}

	/**
	 * Set this cell's type to {@link Type#UNASSIGNED} to {@code
	 * Collections.EMPTY_SET}.
	 * 
	 * @return this
	 */
	public Cell<E> setUnassigned() {
		final Set<E> emptyStates = Collections.emptySet();
		setTypeAndElements(Type.UNASSIGNED, emptyStates);
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
	public Cell<E> setUncertainElements(
			final Set<? extends E> uncertainElements) {
		checkNotNull(uncertainElements);
		checkArgument(uncertainElements.size() > 1,
				"uncertain elements must be > 1");
		setTypeAndElements(Type.UNCERTAIN, uncertainElements);
		return this;
	}

	protected Cell<E> setXmlElementsNeedToBePutIntoStates(
			final boolean xmlStatesNeedsToBePutIntoStates) {
		this.xmlElementsNeedsToBePutIntoElements = xmlStatesNeedsToBePutIntoStates;
		return this;
	}

	protected abstract Cell<E> unsetRow();

	private Cell<E> unsetXmlElements() {
		xmlElements = null;
		return this;
	}

}
