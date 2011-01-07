package edu.upenn.cis.ppod.domain;

public final class PPodStandardState {

	private int stateNumber;

	private String label;

	public PPodStandardState(final int stateNumber, final String label) {
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

	/**
	 * @param label the label to set
	 */
	@SuppressWarnings("unused")
	private void setLabel(final String label) {
		this.label = label;
	}

	@SuppressWarnings("unused")
	private void setStateNumber(final int stateNumber) {
		this.stateNumber = stateNumber;
	}
}
