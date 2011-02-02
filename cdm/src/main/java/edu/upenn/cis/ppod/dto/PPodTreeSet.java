package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public final class PPodTreeSet extends UuPPodDomainObjectWithLabel {

	@XmlElement(name = "tree")
	private List<PPodTree> trees = newArrayList();

	PPodTreeSet() {}

	public PPodTreeSet(final String pPodId, final Long version,
			final String label) {
		super(pPodId, version, label);
	}

	public PPodTreeSet(final String pPodId, final String label) {
		super(pPodId, label);
	}

	public List<PPodTree> getTrees() {
		return trees;
	}

	public void setTrees(final List<PPodTree> trees) {
		checkNotNull(trees);
		this.trees = trees;
	}
}
