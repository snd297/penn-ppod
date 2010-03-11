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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * A molecular sequence - DNA, RNA, protein - that is represented by a {@code
 * CharSequence}.
 * 
 * @author Sam Donnelly
 * @param <SS> the type of {@link MolecularSequenceSet} that contains this
 *            {@code MolecularSequence}
 */
@XmlSeeAlso( { DNASequence.class })
@MappedSuperclass
public abstract class MolecularSequence<SS extends MolecularSequenceSet<?>>
		extends PPodEntity {

	final static String SEQUENCE_COLUMN = "SEQUENCE";

	@Lob
	@Column(name = SEQUENCE_COLUMN, nullable = false)
	@CheckForNull
	private String sequence;

	@Column(name = "ACCESSION", nullable = true)
	@CheckForNull
	private String accession;

	@Column(name = "DESCRIPTION", nullable = true)
	@CheckForNull
	private String description;

	@Column(name = "NAME")
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
	 * Get the description.
	 * 
	 * @return the description
	 */
	@XmlAttribute
	@CheckForNull
	public String getDescription() {
		return description;
	}

	/**
	 * Get the name.
	 * 
	 * @return the name
	 */
	@XmlAttribute
	@CheckForNull
	public String getName() {
		return name;
	}

	@XmlElement
	@Nullable
	public String getSequence() {
		return sequence;
	}

	/**
	 * Get the {@code MolecularSequenceSet} that owns this sequence.
	 * 
	 * @return the {@code MolecularSequenceSet} that owns this sequence
	 */
	@Nullable
	public abstract SS getSequenceSet();

	public abstract boolean isLegal(char c);

	/**
	 * Set the accession.
	 * 
	 * @param accession the accession to set
	 * 
	 * @return this
	 */
	public MolecularSequence<SS> setAccession(@Nullable final String accession) {
		if (equal(accession, getAccession())) {
			return this;
		}
		this.accession = accession;
		setInNeedOfNewPPodVersionInfo();
		return this;
	}

	/**
	 * Set the description
	 * 
	 * @param newDescription the new description
	 * 
	 * @return this
	 */
	public MolecularSequence<SS> setDescription(
			@Nullable final String newDescription) {
		if (equal(newDescription, getDescription())) {
			return this;
		}
		description = newDescription;
		setInNeedOfNewPPodVersionInfo();
		return this;
	}

	@Override
	public MolecularSequence<SS> setInNeedOfNewPPodVersionInfo() {
		if (getSequenceSet() != null) {
			getSequenceSet().setInNeedOfNewPPodVersionInfo();
		}
		super.setInNeedOfNewPPodVersionInfo();
		return this;
	}

	/**
	 * Set the name.
	 * 
	 * @param name the name to set
	 * 
	 * @return this
	 */
	public MolecularSequence<SS> setName(final String name) {
		checkNotNull(name);
		if (name.equals(getName())) {
			return this;
		}
		this.name = name;
		setInNeedOfNewPPodVersionInfo();
		return this;
	}

	public MolecularSequence<SS> setSequence(final String newSequence) {
		checkNotNull(newSequence);
		if (newSequence.equals(getSequence())) {
			return this;
		}
		for (int i = 0; i < newSequence.length(); i++) {
			if (isLegal(newSequence.charAt(i))) {

			} else {
				throw new IllegalArgumentException("Position " + i + " is ["
						+ newSequence.charAt(i) + "] which is not a DNA state");
			}
		}

		sequence = newSequence;
		setInNeedOfNewPPodVersionInfo();
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
