package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class Otu {

	@CheckForNull
	private final Long version;
	private final String label;

	public Otu(final Long version, final String label) {
		checkNotNull(version);
		checkNotNull(label);
		this.version = version;
		this.label = label;
	}

	public Otu(final String label) {
		this.version = null;
		this.label = label;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the version
	 */
	@Nullable
	public Long getVersion() {
		return version;
	}

}
