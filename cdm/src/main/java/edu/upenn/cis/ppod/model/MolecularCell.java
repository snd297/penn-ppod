package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAttribute;

import edu.umd.cs.findbugs.annotations.CheckForNull;

@MappedSuperclass
public abstract class MolecularCell<E extends Enum<?>, R extends Row<?, ?>>
		extends Cell<E, R> {

	@Column(name = "LOWER_CASE", nullable = true)
	@CheckForNull
	private Boolean lowerCase;

	MolecularCell() {}

	@Nullable
	@XmlAttribute(name = "upperCase")
	public Boolean isLowerCase() {
		return lowerCase;
	}

	@Override
	void setInapplicableOrUnassigned(final Type type) {
		super.setInapplicableOrUnassigned(type);
		setLowerCase(null);
	}

	/**
	 * Protected for JAXB.
	 */
	protected void setLowerCase(
			@CheckForNull final Boolean upperCase) {
		this.lowerCase = upperCase;
	}

	public MolecularCell<E, R> setPolymorphicElements(
			final Set<? extends E> elements, final Boolean lowerCase) {
		checkNotNull(lowerCase);
		super.setPolymorphicOrUncertain(Type.POLYMORPHIC, elements);
		setLowerCase(lowerCase);
		return this;
	}

	/**
	 * Set the cell to have type {@link Type#SINGLE} and the given states.
	 * 
	 * @param state state to assign to this cell
	 * 
	 * @return this
	 */
	public MolecularCell<E, R> setSingleElement(
			final E element,
			final Boolean lowerCase) {
		checkNotNull(element);
		checkNotNull(lowerCase);

		// == is safe since we know E is an Enum
		if (element == getElement()
				&& lowerCase.equals(isLowerCase())) {
			if (getType() != Type.SINGLE) {
				throw new AssertionError(
						"element is set, but this cell is not a SINGLE");
			}
			return this;
		}
		setType(Type.SINGLE);
		setElements(null);
		setElement(element);
		setLowerCase(lowerCase);
		setInNeedOfNewVersion();
		return this;
	}

	@Override
	public MolecularCell<E, R> setUncertainElements(
			final Set<? extends E> elements) {
		super.setUncertainElements(elements);
		setLowerCase(null);
		return this;
	}
}
