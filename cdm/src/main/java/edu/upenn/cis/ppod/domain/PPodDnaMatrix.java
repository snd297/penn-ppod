package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class PPodDnaMatrix {
	@CheckForNull
	private final Long version;
	private final List<PPodDnaRow> rows = newArrayList();

	public PPodDnaMatrix() {
		version = null;
	}

	public PPodDnaMatrix(final Long version) {
		checkNotNull(version);
		this.version = version;
	}

	public List<PPodDnaRow> getRows() {
		return rows;
	}

	/**
	 * @return the version
	 */
	@Nullable
	public Long getVersion() {
		return version;
	}
}
