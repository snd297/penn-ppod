package edu.upenn.cis.ppod.util;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import edu.upenn.cis.ppod.model.OTU;

/**
 * @author Sam Donnelly
 * 
 */
public class OTUSomethingPair<U> extends Pair<OTU, U> {

	@XmlElement
	@XmlIDREF
	@Override
	public OTU getFirst() {
		return super.getFirst();
	}

	/**
	 * This seemingly redundant setter method added for the sake of JAXB.
	 */
	@Override
	public OTUSomethingPair<U> setFirst(final OTU otu) {
		return (OTUSomethingPair<U>) super.setFirst(otu);
	}

}
