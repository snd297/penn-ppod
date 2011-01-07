package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

public final class PPodStandardCharacter extends UuPPodDomainObject {

	private final String label;

	private final String mesquiteId;

	private final Set<PPodStandardState> states = newHashSet();

	public PPodStandardCharacter(final String pPodId, final Long version,
			final String label, final String mesquiteId) {
		super(pPodId, version);
		checkNotNull(label);
		checkNotNull(mesquiteId);
		this.label = label;
		this.mesquiteId = mesquiteId;
	}

	public PPodStandardCharacter(final String label,
			final String mesquiteId) {
		this.label = label;
		this.mesquiteId = mesquiteId;
	}

	public String getLabel() {
		return label;
	}

	public String getMesquiteId() {
		return mesquiteId;
	}

	public Set<PPodStandardState> getStates() {
		return states;
	}
}
