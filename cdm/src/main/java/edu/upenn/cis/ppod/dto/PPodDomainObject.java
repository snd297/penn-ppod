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

	protected PPodDomainObject() {
		version = null;
	}

	protected PPodDomainObject(final Long version) {
		checkNotNull(version);
		this.version = version;
	}

	@Nullable
	public Long getVersion() {
		return version;
	}

	public void setVersion(@CheckForNull final Long version) {
		this.version = version;
	}

}
