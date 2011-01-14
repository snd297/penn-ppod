package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import com.google.common.base.Function;

@XmlAccessorType(XmlAccessType.FIELD)
public final class PPodStandardState {

	public static Function<PPodStandardState, Integer> getStateNumber = new Function<PPodStandardState, Integer>() {

		public Integer apply(final PPodStandardState input) {
			return input.getStateNumber();
		}
	};

	@XmlAttribute
	private int stateNumber;

	@XmlAttribute
	private String label;

	PPodStandardState() {}

	public PPodStandardState(final int stateNumber, final String label) {
		checkNotNull(label);
		this.stateNumber = stateNumber;
		this.label = label;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the stateNo
	 */
	public int getStateNumber() {
		return stateNumber;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

}
