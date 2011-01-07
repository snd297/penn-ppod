package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class StandardCharacter {

	@CheckForNull
	private final Long version;
	private final String label;

	private final Set<StandardState> states = newHashSet();

	public StandardCharacter(final Long version, final String label) {
		checkNotNull(version);
		checkNotNull(label);
		this.version = version;
		this.label = label;
	}

	public StandardCharacter(final String label) {
		this.version = null;
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public Set<StandardState> getStates() {
		return states;
	}

	@Nullable
	public Long getVersion() {
		return version;
	}

}
