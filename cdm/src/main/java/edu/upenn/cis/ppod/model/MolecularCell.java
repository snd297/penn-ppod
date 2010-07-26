package edu.upenn.cis.ppod.model;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

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

	@Nullable
	public Boolean isUpperCase() {
		return upperCase;
	}

	/**
	 * So JAXB can have a raw (no arg checking) setter.
	 */
	@XmlAttribute(name = "upperCase")
	protected Boolean isUpperCaseXml() {
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

	public MolecularCell<E, R> setUpperCase(
			@CheckForNull final Boolean upperCase) {
		checkState(getType() != null,
				"called setUpperCase but getType() == null");
		checkState(getType() == Type.SINGLE
				|| upperCase == null, // if (type is not single) ->
										// upperCase must be null
				"if a cell is not single, it can't have a case");
		if (equal(upperCase, this.upperCase)) {

		} else {
			this.upperCase = upperCase;
			setInNeedOfNewVersion();
		}
		return this;
	}

	protected void setUpperCaseXml(@CheckForNull final Boolean upperCase) {
		this.upperCase = upperCase;
	}
}
