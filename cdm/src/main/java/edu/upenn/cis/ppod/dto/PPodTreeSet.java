package edu.upenn.cis.ppod.dto;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public final class PPodTreeSet extends UuPPodDomainObjectWithLabel {

	@XmlElement(name = "tree")
	private final List<PPodTree> trees = newArrayList();

	PPodTreeSet() {}

	public PPodTreeSet(final String pPodId, final String label) {
		super(pPodId, label);
	}

	public PPodTreeSet(final String pPodId, final Long version,
			final String label) {
		super(pPodId, version, label);
	}

	public List<PPodTree> getTrees() {
		return trees;
	}
}
