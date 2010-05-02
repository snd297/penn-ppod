package edu.upenn.cis.ppod.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import edu.upenn.cis.ppod.model.DNARow;
import edu.upenn.cis.ppod.model.OTU;

/**
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
public class OTUDNARowPair extends OTUSomethingPair<DNARow> {
	/**
	 * For JAXB.
	 */
	private OTUDNARowPair() {}

	@XmlElement
	@Override
	public DNARow getSecond() {
		return super.getSecond();
	}

	/**
	 * This seemingly redundant setter method added for the sake of JAXB.
	 */
	@Override
	public OTUSomethingPair<DNARow> setSecond(
			final DNARow row) {
		return (OTUSomethingPair<DNARow>) super.setSecond(row);
	}

	public static OTUDNARowPair of(final OTU first,
			final DNARow second) {
		final OTUDNARowPair otuRowPair = new OTUDNARowPair();
		otuRowPair.setFirst(first);
		otuRowPair.setSecond(second);
		return otuRowPair;
	}
}
