package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlID;

public final class PPodStandardMatrix extends UuPPodDomainObject {

	private final String label;
	private final List<PPodStandardCharacter> characters = newArrayList();
	private final List<PPodStandardRow> rows = newArrayList();

	@XmlID
	private final String docId = UUID.randomUUID().toString();

	public PPodStandardMatrix(final String pPodId, final Long version,
			final String label) {
		super(pPodId, version);
		checkNotNull(label);
		this.label = label;
	}

	public List<PPodStandardCharacter> getCharacters() {
		return characters;
	}

	public String getLabel() {
		return label;
	}

	public List<PPodStandardRow> getRows() {
		return rows;
	}

	public String getDocId() {
		return docId;
	}
}
