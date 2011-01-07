package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class PPodStandardMatrix {
	@CheckForNull
	private final Long version;
	private final List<PPodStandardCharacter> characters = newArrayList();
	private final List<PPodStandardRow> rows = newArrayList();

	public PPodStandardMatrix(final Long version) {
		checkNotNull(version);
		this.version = version;
	}

	public List<PPodStandardCharacter> getCharacters() {
		return characters;
	}

	public List<PPodStandardRow> getRows() {
		return rows;
	}

	@Nullable
	public Long getVersion() {
		return version;
	}

}
