package edu.upenn.cis.ppod.domain;

public class PPodStandardState {

	private int stateNo;

	private String label;

	public PPodStandardState(final int stateNo, final String label) {
		this.stateNo = stateNo;
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
	public int getStateNo() {
		return stateNo;
	}

	/**
	 * @param label the label to set
	 */
	@SuppressWarnings("unused")
	private void setLabel(final String label) {
		this.label = label;
	}

	@SuppressWarnings("unused")
	private void setStateNo(final int stateNo) {
		this.stateNo = stateNo;
	}
}
