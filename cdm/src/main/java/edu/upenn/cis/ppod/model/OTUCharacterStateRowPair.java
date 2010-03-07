package edu.upenn.cis.ppod.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import edu.upenn.cis.ppod.util.Pair;

/**
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
public class OTUCharacterStateRowPair extends Pair<OTU, CharacterStateRow> {
	/**
	 * For JAXB.
	 */
	private OTUCharacterStateRowPair() {}

	@XmlElement
	@XmlIDREF
	@Override
	public OTU getFirst() {
		return super.getFirst();
	}

	@XmlElement
	@Override
	public CharacterStateRow getSecond() {
		return super.getSecond();
	}

	/**
	 * This seemingly redundant setter method added for the sake of JAXB.
	 */
	@Override
	public OTUCharacterStateRowPair setFirst(final OTU otu) {
		return (OTUCharacterStateRowPair) super.setFirst(otu);
	}

	/**
	 * This seemingly redundant setter method added for the sake of JAXB.
	 */
	@Override
	public OTUCharacterStateRowPair setSecond(final CharacterStateRow row) {
		return (OTUCharacterStateRowPair) super.setSecond(row);
	}

	public static OTUCharacterStateRowPair of(final OTU first,
			final CharacterStateRow second) {
		final OTUCharacterStateRowPair otuRowPair = new OTUCharacterStateRowPair();
		otuRowPair.setFirst(first);
		otuRowPair.setSecond(second);
		return otuRowPair;
	}
}
