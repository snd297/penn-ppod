package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
abstract class PPodDomainObject {

	@XmlAttribute
	@CheckForNull
	private Long version;

	PPodDomainObject() {
		version = null;
	}

	PPodDomainObject(final Long version) {
		this.version = checkNotNull(version);
	}

	@Nullable
	public final Long getVersion() {
		return version;
	}

	public final void setVersion(@CheckForNull final Long version) {
		this.version = version;
	}

}
