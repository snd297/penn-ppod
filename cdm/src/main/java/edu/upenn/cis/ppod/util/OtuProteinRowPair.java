package edu.upenn.cis.ppod.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.ProteinRow;

@XmlAccessorType(XmlAccessType.NONE)
public final class OtuProteinRowPair
		extends OtuKeyedPair<ProteinRow> {

	protected OtuProteinRowPair() {}

	public OtuProteinRowPair(final Otu first, final ProteinRow second) {
		super(first, second);
	}

	@XmlElement
	@Override
	public ProteinRow getSecond() {
		return super.getSecond();
	}

	/**
	 * This seemingly redundant setter method added for the sake of JAXB.
	 */
	@Override
	protected void setSecond(final ProteinRow row) {
		super.setSecond(row);
	}
}
