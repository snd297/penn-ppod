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
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author Sam Donnelly
 */
@Entity
@Table(name = "CHROMATOGRAM")
public class Chromatogram extends UuPPodEntity {

	@Lob
	@Column(name = "CHROMATOGRAM", nullable = false)
	@CheckForNull
	private byte[] chromatogram;

	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@CheckForNull
	private DnaSequence sequence;

	@Nullable
	public byte[] getChromatogram() {
		if (chromatogram == null) {
			return null;
		}
		final byte[] chromatogramCopy = new byte[chromatogram.length];
		System.arraycopy(chromatogram, 0, chromatogramCopy, 0,
				chromatogram.length);
		return chromatogramCopy;
	}

	/**
	 * {@code null} is a valid value.
	 * 
	 * @return the {@code DNASequence} that this points to
	 */
	@Nullable
	public DnaSequence getSequence() {
		return sequence;
	}

	public void setChromatogram(final byte[] chromatogram) {
		this.chromatogram = chromatogram;
	}

	public void setSequence(@CheckForNull final DnaSequence sequence) {
		this.sequence = sequence;
	}

}
