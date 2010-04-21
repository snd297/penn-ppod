/*
 * Copyright (C) 2010 Trustees of the University of Pennsylvania
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
package edu.upenn.cis.ppod.model;

import javax.annotation.CheckForNull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import edu.upenn.cis.ppod.modelinterfaces.IChromatogram;

/**
 * @author Sam Donnelly
 */
@Entity
@Table(name = "CHROMATOGRAM")
public class Chromatogram extends UUPPodEntity implements IChromatogram {

	@Column(name = "CHROMATOGRAM", nullable = false)
	@CheckForNull
	private byte[] chromatogram;

	@OneToOne(fetch = FetchType.LAZY)
	@CheckForNull
	private DNASequence sequence;

	public byte[] getChromatogram() {
		return chromatogram;
	}

	public IChromatogram setChromatogram(final byte[] chromatogram) {
		this.chromatogram = chromatogram;
		return this;
	}

	public DNASequence getSequence() {
		return sequence;
	}

	public IChromatogram setSequence(DNASequence sequence) {
		this.sequence = sequence;
		return this;
	}

}
