package edu.upenn.cis.ppod.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class PPodStandardMatrix extends UuPPodDomainObject {

	private final List<PPodStandardCharacter> characters = newArrayList();
	private final List<PPodStandardRow> rows = newArrayList();

	public PPodStandardMatrix(final String pPodId, final Long version) {
		super(pPodId, version);
	}

	public List<PPodStandardCharacter> getCharacters() {
		return characters;
	}

	public List<PPodStandardRow> getRows() {
		return rows;
	}
}
