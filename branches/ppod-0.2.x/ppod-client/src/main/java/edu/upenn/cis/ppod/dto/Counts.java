/*
 * Copyright (C) 2011 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkState;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Counts {

	private boolean timedOut;

	private long otuSetCount = -1;
	private long standardMatrixCount = -1;
	private long dnaMatrixCount = -1;
	private long treeSetCount = -1;

	public Counts() {}

	/**
	 * @return the dnaMatrixCount
	 */
	public long getDnaMatrixCount() {
		return dnaMatrixCount;
	}

	/**
	 * @return the otuSetCount
	 */
	public long getOtuSetCount() {
		return otuSetCount;
	}

	/**
	 * @return the standardMatrixCount
	 */
	public long getStandardMatrixCount() {
		return standardMatrixCount;
	}

	public long getTotal() {
		if (timedOut) {
			return -1;
		}

		checkState(otuSetCount != -1);
		checkState(standardMatrixCount != -1);
		checkState(dnaMatrixCount != -1);
		checkState(treeSetCount != -1);
		return otuSetCount + standardMatrixCount + dnaMatrixCount
				+ treeSetCount;
	}

	/**
	 * @return the treeSetCount
	 */
	public long getTreeSetCount() {
		return treeSetCount;
	}

	/**
	 * @param dnaMatrixCount the dnaMatrixCount to set
	 */
	public void setDnaMatrixCount(final long dnaMatrixCount) {
		this.dnaMatrixCount = dnaMatrixCount;
	}

	/**
	 * @param otuSetCount the otuSetCount to set
	 */
	public void setOtuSetCount(final long otuSetCount) {
		this.otuSetCount = otuSetCount;
	}

	/**
	 * @param standardMatrixCount the standardMatrixCount to set
	 */
	public void setStandardMatrixCount(final long standardMatrixCount) {
		this.standardMatrixCount = standardMatrixCount;
	}

	/**
	 * @param treeSetCount the treeSetCount to set
	 */
	public void setTreeSetCount(final long treeSetCount) {
		this.treeSetCount = treeSetCount;
	}

	/**
	 * @return the timedOut
	 */
	public boolean isTimedOut() {
		return timedOut;
	}

	/**
	 * @param timedOut the timedOut to set
	 */
	public void setTimedOut(boolean timedOut) {
		this.timedOut = timedOut;
	}
}
