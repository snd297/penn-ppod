package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public final class PPodStandardCharacter extends UuPPodDomainObject {

	private String label;

	private String mesquiteId;
	private final Set<PPodStandardState> states = newHashSet();

	PPodStandardCharacter() {}

	public PPodStandardCharacter(final String pPodId,
			final Long version,
			final String label, final String mesquiteId) {
		super(pPodId, version);
		checkNotNull(label);
		checkNotNull(mesquiteId);
		this.label = label;
		this.mesquiteId = mesquiteId;
	}

	public PPodStandardCharacter(@CheckForNull final String pPodId,
			final String label,
			final String mesquiteId) {
		super(pPodId);
		checkNotNull(label);
		checkNotNull(mesquiteId);
		this.label = label;
		this.mesquiteId = mesquiteId;
	}

	public String getLabel() {
		return label;
	}

	public String getMesquiteId() {
		return mesquiteId;
	}

	@Nullable
	public PPodStandardState getState(final int stateNo) {
		return find(states,
				compose(equalTo(stateNo), PPodStandardState.getStateNumber),
				null);
	}

	public Set<PPodStandardState> getStates() {
		return states;
	}
}
