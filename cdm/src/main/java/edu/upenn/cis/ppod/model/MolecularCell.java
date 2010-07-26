package edu.upenn.cis.ppod.model;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

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

	@XmlAttribute(name = "upperCase")
	@Nullable
	public Boolean isUpperCase() {
		return upperCase;
	}

	@Override
	void setInapplicableOrUnassigned(final Type type) {
		checkNotNull(type);
		super.setInapplicableOrUnassigned(type);
		setUpperCase(null);
	}

	public MolecularCell<E, R> setUpperCase(
			@CheckForNull final Boolean upperCase) {
		if (equal(upperCase, this.upperCase)) {

		} else {
			this.upperCase = upperCase;
			setInNeedOfNewVersion();
		}
		return this;
	}

}
