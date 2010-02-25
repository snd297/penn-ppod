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

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class would be abstract, but JAXB didn't like that.
 * 
 * @author Sam Donnelly
 */
@XmlSeeAlso( { DNASequence.class })
@MappedSuperclass
public abstract class MolecularSequence<SS extends MolecularSequenceSet<?>>
		extends UUPPodEntity {

	final static String SEQUENCE_COLUMN = "SEQUENCE";

	@Lob
	@Column(name = SEQUENCE_COLUMN, nullable = false)
	private final StringBuilder sequence = new StringBuilder();

	@Column(name = "ACCESSION", nullable = true)
	@CheckForNull
	private String accession;

	@Column(name = "DESCRIPTION", nullable = true)
	@CheckForNull
	private String description;

	@Column(name = "NAME", nullable = true)
	@CheckForNull
	private String name;

	MolecularSequence() {}

	/**
	 * Get the accession.
	 * 
	 * @return the accession
	 */
	@XmlAttribute
	@CheckForNull
	public String getAccession() {
		return accession;
	}

	/**
	 * Get the defline.
	 * 
	 * @return the defline
	 */
	@XmlAttribute
	@CheckForNull
	private String getDescription() {
		return description;
	}

	/**
	 * Get the name.
	 * 
	 * @return the name
	 */
	@XmlAttribute
	@Nullable
	public String getName() {
		return name;
	}

	@XmlElement
	@Nullable
	public String getSequence() {
		return sequence.toString();
	}

	@Nullable
	public abstract MolecularSequenceSet getSequenceSet();

// @Column(name = "LOCUS")
// @Nullable
// private String locus;// when read back into matrix it gets put taxon
//
// @Column(name = "DEFLINE")
// @Nullable
// private String defline;
//
// @Column(name = "ACCESSION_NUMBER")
// @Nullable
// private String accessionNumber;

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

	@Override
	public MolecularSequence resetPPodVersionInfo() {
		if (getSequenceSet() != null) {
			getSequenceSet().resetPPodVersionInfo();
		}
		super.resetPPodVersionInfo();
		return this;
	}

	/**
	 * Set the accession.
	 * 
	 * @param accession the accession to set
	 * 
	 * @return this
	 */
	public MolecularSequence setAccession(final String accession) {
		this.accession = accession;
		return this;
	}

	/**
	 * Set the description
	 * 
	 * @param newDescription the new description
	 * 
	 * @return this
	 */
	public MolecularSequence setDescription(final String newDescription) {
		description = newDescription;
		return this;
	}

	/**
	 * Set the name.
	 * 
	 * @param name the name to set
	 * 
	 * @return this
	 */
	public MolecularSequence setName(final String name) {
		this.name = name;
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

	/**
	 * Set the sequence set that contains this sequence.
	 * <p>
	 * A {@code null} value for {@code sequenceSet} indicates that the
	 * relationship is being severed.
	 * 
	 * @param sequenceSet the sequence set that contains this sequence.
	 * 
	 * @return this
	 */
	protected abstract MolecularSequence<SS> setSequenceSet(
			@Nullable final SS sequenceSet);

	/**
	 * Constructs a <code>String</code> with all attributes in name = value
	 * format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	@Override
	public String toString() {
		final String TAB = "";

		final StringBuilder retValue = new StringBuilder();

		retValue.append("MolecularSequence(").append("sequence=").append(
				this.sequence).append(TAB).append("accession=").append(
				this.accession).append(TAB).append("description=").append(
				this.description).append(TAB).append("name=").append(this.name)
				.append(TAB).append(")");

		return retValue.toString();
	}

	// private final Map<Integer, CharacterStates> statesByPosition =
	// newHashMap();

	// protected Map<Integer, CharacterStates> getStatesByPosition() {
	// return statesByPosition;
	// }

}
