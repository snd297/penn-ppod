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
