package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class DnaMatrix {
	@CheckForNull
	private final Long version;
	private final List<DnaRow> rows = newArrayList();

	public DnaMatrix() {
		version = null;
	}

	public DnaMatrix(final Long version) {
		checkNotNull(version);
		this.version = version;
	}

	public List<DnaRow> getRows() {
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
