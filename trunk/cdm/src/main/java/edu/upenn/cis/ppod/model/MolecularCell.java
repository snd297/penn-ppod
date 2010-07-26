package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAttribute;

import edu.umd.cs.findbugs.annotations.CheckForNull;

@MappedSuperclass
abstract public class MolecularCell<E, R extends Row<?, ?>> extends Cell<E, R> {

	@Column(name = "UPPER_CASE", nullable = true)
	@CheckForNull
	private Boolean upperCase;

	/**
	 * So JAXB can have a raw (no arg checking) setter.
	 */
	@Nullable
	@XmlAttribute(name = "upperCase")
	public Boolean isUpperCase() {
		return upperCase;
	}

	@Override
	void setInapplicableOrUnassigned(final Type type) {
		checkNotNull(type);
		super.setInapplicableOrUnassigned(type);
		setUpperCase(null);
	}

	@Override
	void setPolymorphicOrUncertain(
			final Type type,
			final Set<? extends E> elements) {
		checkNotNull(type);
		checkNotNull(elements);
		super.setPolymorphicOrUncertain(type, elements);
		setUpperCase(null);
	}

	/**
	 * Set the cell to have type {@link Type#SINGLE} and the given states.
	 * 
	 * @param state state to assign to this cell
	 * 
	 * @return this
	 */
	public Cell<E, R> setSingleElement(final E element, final Boolean upperCase) {
		checkNotNull(element);
		if (element.equals(getElement())
				&& upperCase.equals(isUpperCase())) {
			if (getType() != Type.SINGLE) {
				throw new AssertionError(
						"element is set, but this cell is not a SINGLE");
			}
			return this;
		}
		setType(Type.SINGLE);
		setElements(null);
		setElement(element);
		setUpperCase(upperCase);
		setInNeedOfNewVersion();
		return this;
	}

	protected void setUpperCase(
			@CheckForNull Boolean upperCase) {
		this.upperCase = upperCase;
	}

	protected void setUpperCaseRaw(@CheckForNull final Boolean upperCase) {
		this.upperCase = upperCase;
	}
}
