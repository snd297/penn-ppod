package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;

public final class PPodStandardState {

	PPodStandardState() {}

	public static Function<PPodStandardState, Integer> getStateNumber = new Function<PPodStandardState, Integer>() {

		public Integer apply(PPodStandardState input) {
			return input.getStateNumber();
		}
	};

	private int stateNumber;

	private String label;

	public PPodStandardState(final int stateNumber, final String label) {
		checkNotNull(stateNumber);
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

}
