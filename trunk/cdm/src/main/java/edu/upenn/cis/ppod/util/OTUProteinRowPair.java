package edu.upenn.cis.ppod.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import edu.upenn.cis.ppod.imodel.IProteinRow;
import edu.upenn.cis.ppod.model.OtuChangeCase;

@XmlAccessorType(XmlAccessType.NONE)
public class OTUProteinRowPair
		extends OTUKeyedPair<IProteinRow> {

	protected OTUProteinRowPair() {}

	public OTUProteinRowPair(final OtuChangeCase first, final IProteinRow second) {
		super(first, second);
	}

	@XmlElement
	@Override
	public IProteinRow getSecond() {
		return super.getSecond();
	}

	/**
	 * This seemingly redundant setter method added for the sake of JAXB.
	 */
	@Override
	protected void setSecond(final IProteinRow row) {
		super.setSecond(row);
	}
}
