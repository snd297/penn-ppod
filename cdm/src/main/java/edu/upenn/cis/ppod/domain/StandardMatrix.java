package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class StandardMatrix {
	@CheckForNull
	private final Long version;
	private final List<StandardCharacter> characters = newArrayList();
	private final List<StandardRow> rows = newArrayList();

	public StandardMatrix(final Long version) {
		checkNotNull(version);
		this.version = version;
	}

	public List<StandardCharacter> getCharacters() {
		return characters;
	}

	public List<StandardRow> getRows() {
		return rows;
	}

	@Nullable
	public Long getVersion() {
		return version;
	}

}
