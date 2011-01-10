package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;

public final class PPodStandardState {

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
