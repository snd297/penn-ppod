package edu.upenn.cis.ppod.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IProteinRow;

@XmlAccessorType(XmlAccessType.NONE)
public class OTUProteinRowPair
		extends OTUKeyedPair<IProteinRow> {
	/**
	 * For JAXB.
	 */
	private OTUProteinRowPair() {}

	@XmlElement
	@Override
	public IProteinRow getSecond() {
		return super.getSecond();
	}

	/**
	 * This seemingly redundant setter method added for the sake of JAXB.
	 */
	@Override
	public void setSecond(
			final IProteinRow row) {
		super.setSecond(row);
	}

	public static OTUProteinRowPair of(
			final IOTU first,
			final IProteinRow second) {
		final OTUProteinRowPair otuRowPair = new OTUProteinRowPair();
		otuRowPair.setFirst(first);
		otuRowPair.setSecond(second);
		return otuRowPair;
	}
}
