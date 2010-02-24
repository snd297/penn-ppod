/*
 * Copyright (C) 2009 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class MolecularSequence extends UUPPodEntity {

	final static String SEQUENCE_COLUMN = "SEQUENCE";

	@Lob
	@Column(name = SEQUENCE_COLUMN, nullable = false)
	private StringBuilder sequence = new StringBuilder();

	@Nullable
	public abstract MolecularSequenceSet getSequenceSet();

	@Column(name = "LOCUS")
	@Nullable
	private String locus;// when read back into matrix it gets put taxon

	@Column(name = "DEFLINE")
	@Nullable
	private String defline;

	@Column(name = "ACCESSION_NUMBER")
	@Nullable
	private String accessionNumber;

	/*
	 * Have an operation upload to server as sequences.
	 * 
	 * Should be able to tell from the name label that it's a sequence.
	 * 
	 * Figure out alignment.
	 * 
	 * Extract OTU from the locus part.
	 * 
	 * Store whole header as an annotation on the row
	 * 
	 * Have a locus field Whole unparsed header field
	 * 
	 * Look around sequence header cleaners
	 * 
	 * A separte header object w/ subclasses for for example GenBank
	 */

// public MolecularSequence putStates(final Integer idx,
// final CharacterStates states) {
	// checkNotNull(idx);
// checkNotNull(states);
// statesByPosition.put(idx, states);
// return this;
// }

	MolecularSequence() {}

	/**
	 * Get the defline.
	 * 
	 * @return the defline
	 */
	@XmlAttribute
	@Nullable
	private String getDefline() {
		return defline;
	}

	@XmlElement
	public String getSequence() {
		return sequence.toString();
	}

	/**
	 * Set the defline.
	 * 
	 * @param defline the defline to set
	 * 
	 * @return this
	 */
	private MolecularSequence setDefline(final String defline) {
		this.defline = defline;
		return this;
	}

	@OverridingMethodsMustInvokeSuper
	public MolecularSequence setSequence(final CharSequence newSequence) {
		checkNotNull(newSequence);
		if (newSequence.equals(getSequence())) {
			return this;
		}
		sequence.setLength(0);
		sequence.append(newSequence);
		resetPPodVersionInfo();
		return this;
	}

	@Override
	public MolecularSequence resetPPodVersionInfo() {
		if (getSequenceSet() != null) {
			getSequenceSet().resetPPodVersionInfo();
		}
		super.resetPPodVersionInfo();
		return this;
	}

	// private final Map<Integer, CharacterStates> statesByPosition =
	// newHashMap();

	// protected Map<Integer, CharacterStates> getStatesByPosition() {
	// return statesByPosition;
	// }

}
