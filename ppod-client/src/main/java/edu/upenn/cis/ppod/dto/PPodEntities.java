package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public final class PPodEntities implements IHasOtuSets {

	private Set<PPodOtu> otus = newHashSet();

	private List<PPodOtuSet> otuSets = newArrayList();

	public String getLabel() {
		return "From HQL";
	}

	/**
	 * @return the otus
	 */
	@XmlElement(name = "otu")
	public Set<PPodOtu> getOtus() {
		return otus;
	}

	@XmlElement(name = "otuSet")
	public List<PPodOtuSet> getOtuSets() {
		return otuSets;
	}

	public Long getVersion() {
		return 0L;
	}

	/**
	 * @param otus the otus to set
	 */
	public void setOtus(final Set<PPodOtu> otus) {
		this.otus = checkNotNull(otus);
	}

	public void setOtuSets(final List<PPodOtuSet> otuSets) {
		this.otuSets = checkNotNull(otuSets);
	}

}
