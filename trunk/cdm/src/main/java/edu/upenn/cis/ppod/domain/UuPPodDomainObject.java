package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

abstract class UuPPodDomainObject extends PPodDomainObject {
	@CheckForNull
	private final String pPodId;

	protected UuPPodDomainObject() {
		pPodId = null;
	}

	protected UuPPodDomainObject(final String pPodId, final Long version) {
		super(version);
		checkNotNull(pPodId);
		this.pPodId = pPodId;
	}

	/**
	 * @return the pPodId
	 */
	@Nullable
	public String getPPodId() {
		return pPodId;
	}

}
