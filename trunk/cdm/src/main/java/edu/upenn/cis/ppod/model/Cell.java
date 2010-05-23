package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
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

		/** Unassigned, usually written as a {@code "?"} in Nexus files. */
		UNASSIGNED,

		/**
		 * The cell has exactly one state.
		 */
		SINGLE,

		/**
		 * The cell is a conjunctions of states: <em>state1</em> and
		 * <em>state2</em> and ... and <em>stateN</em>.
		 */
		POLYMORPHIC,

		/**
		 * The cell is a disjunction of states: <em>state1</em> or
		 * <em>state2</em> or ... or <em>stateN</em>.
		 */
		UNCERTAIN,

		/** Inapplicable, usually written as a {@code "-"} in Nexus files. */
		INAPPLICABLE;

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
	 * Used for serialization so we don't have to hit {@code elements} directly
	 * and thereby cause unwanted database hits. Which are pretty expensive in a
	 * large matrix.
	 */
	@Transient
	@CheckForNull
	private Set<E> elementsXml;

	/**
	 * Tells us that this {@code Cell} has been unmarshalled and still needs to
	 * have {@code states} populated with {@code xmlStates}.
	 */
	@Transient
	private boolean needsAfterMarshal = false;

	Cell() {}

	/**
	 * {@link Marshaller} callback.
	 */
	public void afterMarshal(@CheckForNull final Marshaller marshaller) {
		setElementsXml(null);
	}

	@Override
	public void afterUnmarshal() {
		super.afterUnmarshal();
		if (getNeedsAfterMarshal()) {
			switch (getType()) {
				case UNASSIGNED:
				case SINGLE:
				case INAPPLICABLE:
					break;
				case POLYMORPHIC:
				case UNCERTAIN:
					setElements(getElementsXml());
					break;
				default:
					throw new AssertionError("unknown cell type " + getType());
			}
			setElementsXml(null);
			setNeedsAfterMarshal(false);
		}
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@Override
	public void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			@CheckForNull final Object parent) {
		super.afterUnmarshal(u, parent);
		setNeedsAfterMarshal(true);
		switch (getType()) {
			case UNASSIGNED:
			case SINGLE:
			case INAPPLICABLE:
				// free it up for GC
				setElementsXml(null);
				break;
			case POLYMORPHIC:
			case UNCERTAIN:
				break;
			default:
				throw new AssertionError("unknown cell type " + getType());
		}
	}

	/**
	 * @throws IllegalStateException if the type has not been set
	 */
	@Override
	public boolean beforeMarshal(
			@CheckForNull final Marshaller marshaller) {

		// Let's not marshal it if it's in a bad state
		checkState(getType() != null, "can't marshal a cell without a type");

		// if (getElementsXml() != null) {
		// throw new AssertionError(
		// "getElementsXml() != null in beforeMarshal(...)");
		// }

		switch (getType()) {
			case UNASSIGNED:
			case SINGLE:
			case INAPPLICABLE:
				break;
			case POLYMORPHIC:
			case UNCERTAIN:
				initElementsXml();
				getElementsXml().addAll(getElements());
				break;
			default:
				throw new AssertionError("unknown cell type " + getType());
		}
		return super.beforeMarshal(marshaller);
	}

	/**
	 * {@link Unmarshaller} callback.
	 * <p>
	 * For marshalling, initializing {@code xmlElemnts} is handled in
	 * {@link #beforeMarshal(Marshaller)}.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	public void beforeUnmarshal(
			final Unmarshaller u,
			final Object parent) {
		initElementsXml();
	}

	/**
	 * Will be {@code null} if this is cell is not {@link Type.SINGLE}.
	 */
	@CheckForNull
	protected abstract E getElement();

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
				!getNeedsAfterMarshal(),
				"xmlStateNeedsToBePutIntoStates == true, has the afterUnmarshal visitor been dispatched?");
		switch (getType()) {
			// Don't hit states unless we have too
			case INAPPLICABLE:
			case UNASSIGNED:
				return Collections.emptySet();
			case SINGLE:
				if (getElement() == null) {
					throw new AssertionError(
							"getElement() == null for SINGLE cell!");
				}
				final Set<E> elementInASet = newHashSet();
				elementInASet.add(getElement());
				return elementInASet;
			case POLYMORPHIC:
			case UNCERTAIN:

				// We have to hit states, which we want to avoid as much as
				// possible since it will trigger a database hit, which in the
				// aggregate
				// is expensive since there're are so many cells.
				final Set<E> elements = getElementsRaw();
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

	/**
	 * Get the elements in this cell.
	 * 
	 * @return the elements in this cell
	 */
	protected abstract Set<E> getElementsRaw();

	/**
	 * Used for serialization so we don't have to hit {@code elements} directly
	 * and thereby cause unwanted database hits.
	 * <p>
	 * This is abstract since subclasses may not just want a {@code HashSet}.
	 * <p>
	 * We could make a HashSet here, but we don't want to accidentally call it
	 * from, for example, {@code DNACell}.
	 */
	@CheckForNull
	protected Set<E> getElementsXml() {
		return elementsXml;
	}

	/**
	 * Package-private for testing.
	 */
	boolean getNeedsAfterMarshal() {
		return needsAfterMarshal;
	}

	@CheckForNull
	protected Integer getPosition() {
		return position;
	}

	@Nullable
	protected abstract IRow getRow();

	/**
	 * Get the type of this cell.
	 * <p>
	 * This value will be {@code null} for newly created cells until the
	 * elements are set.
	 * <p>
	 * This value will never be {@code null} for a persistent cell.
	 * 
	 * @return the type of this cell
	 */
	@XmlAttribute
	@Nullable
	public Type getType() {
		return type;
	}

	protected abstract void initElementsXml();

	/**
	 * Does not affect {@link #isInNeedOfNewVersion()}.
	 */
	protected abstract Cell<E> setElement(@CheckForNull E element);

	/**
	 * Does not affect {@link #isInNeedOfNewVersion()}.
	 */
	protected abstract Cell<E> setElements(
			@CheckForNull Set<E> elements);

	protected Cell<E> setElementsXml(final Set<E> elementsXml) {
		this.elementsXml = elementsXml;
		return this;
	}

	/**
	 * Set this cell's type to {@link Type#INAPPLICABLE} to {@code
	 * Collections.EMPTY_SET}.
	 * 
	 * @return this
	 */
	public Cell<E> setInapplicable() {
		return setInapplicableOrUnassigned(Type.INAPPLICABLE);

	}

	private Cell<E> setInapplicableOrUnassigned(final Type type) {
		checkArgument(
				type == Type.INAPPLICABLE
						|| type == Type.UNASSIGNED,
				"type was " + type + " but must be INAPPLICABLE or UNASSIGNED");

		if (getType() == type) {
			return this;
		}
		setType(type);
		setElement(null);
		setElements(null);
		setInNeedOfNewVersion();
		return this;

	}

	@Override
	public Cell<E> setInNeedOfNewVersion() {
		final IRow row = getRow();
		if (row != null) {
			row.setInNeedOfNewVersion();
			final IMatrix matrix = row.getMatrix();
			if (matrix != null) {

				// so FindBugs knows that it's okay
				final Integer position = getPosition();
				checkState(position != null,
						"cell has no position, but is a part of a matrix");
				matrix.resetColumnVersion(position);
			}
		}
		super.setInNeedOfNewVersion();
		return this;
	}

	protected Cell<E> setNeedsAfterMarshal(
			final boolean xmlStatesNeedsToBePutIntoStates) {
		this.needsAfterMarshal = xmlStatesNeedsToBePutIntoStates;
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
			final Set<E> polymorphicElements) {
		checkNotNull(polymorphicElements);
		checkArgument(polymorphicElements.size() > 1,
				"polymorphic states must be > 1");
		setPolymorphicOrUncertain(Type.POLYMORPHIC, polymorphicElements);
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
	protected abstract Cell<E> setPolymorphicOrUncertain(final Type type,
			final Set<E> elements);

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
	public abstract Cell<E> setSingleElement(final E state);

	/**
	 * This method has no affect on {@link #isInNeedOfNewVersion()}.
	 * 
	 * @param type the new type
	 * @return this
	 */
	protected Cell<E> setType(final Type type) {
		checkNotNull(type);
		this.type = type;
		return this;
	}

	/**
	 * Created for testing purposes.
	 */
	Cell<E> setTypeAndXmlElements(final Type type,
			final Set<? extends E> xmlStates) {
		checkNotNull(type);
		checkNotNull(xmlStates);
		setType(type);
		initElementsXml();
		getElementsXml().clear();
		getElementsXml().addAll(xmlStates);
		setNeedsAfterMarshal(true);
		return this;
	}

	/**
	 * Set this cell's type to {@link Type#UNASSIGNED} to {@code
	 * Collections.EMPTY_SET}.
	 * 
	 * @return this
	 */
	public Cell<E> setUnassigned() {
		return setInapplicableOrUnassigned(Type.UNASSIGNED);
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
			final Set<E> uncertainElements) {
		checkNotNull(uncertainElements);
		checkArgument(uncertainElements.size() > 1,
				"uncertain elements must be > 1");
		setPolymorphicOrUncertain(Type.UNCERTAIN, uncertainElements);
		return this;
	}

	protected abstract Cell<E> unsetRow();

}
