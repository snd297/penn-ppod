package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

abstract class PPodDomainObject {

	@CheckForNull
	private final Long version;

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

}
