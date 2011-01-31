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
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

import edu.upenn.cis.ppod.imodel.IChild;

/**
 * A molecular sequence - DNA, RNA, protein - that is represented by a
 * {@code CharSequence}.
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class Sequence<SS extends SequenceSet<?>>
		extends PPodEntity
		implements IChild<SS> {

	private final static String SEQUENCE_COLUMN = "SEQUENCE";

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

	@Column(name = "NAME", nullable = true)
	@CheckForNull
	private String name;

	Sequence() {}

	/**
	 * Get the accession.
	 * 
	 * @return the accession
	 */
	@Nullable
	public String getAccession() {
		return accession;
	}

	/**
	 * Get the description.
	 * 
	 * @return the description
	 */
	@Nullable
	public String getDescription() {
		return description;
	}

	/**
	 * Get the name.
	 * 
	 * @return the name
	 */
	@Nullable
	public String getName() {
		return name;
	}

	/**
	 * Get the sequence string.
	 * <p>
	 * This will only be {@code null} for newly created objects until
	 * {@link #setSequence(String)} is called.
	 * <p>
	 * This will never be {@code null} for objects in a persistent state.
	 * 
	 * @return the sequence string
	 */
	@Nullable
	public String getSequence() {
		return sequence;
	}

	/**
	 * Is it a legal Sequence character?
	 * 
	 * @param c candidate
	 * 
	 * @return {@code true} if the character is legal, {@code false} otherwise
	 */
	abstract boolean isLegal(char c);

	/**
	 * Set the accession.
	 * 
	 * @param accession the accession to set
	 * 
	 * @return this
	 */
	public Sequence<SS> setAccession(
				@CheckForNull final String accession) {
		if (equal(accession, getAccession())) {
			return this;
		}
		this.accession = accession;
		setInNeedOfNewVersion();
		return this;
	}

	/**
	 * Set the description
	 * 
	 * @param newDescription the new description
	 * 
	 * @return this
	 */
	public Sequence<SS> setDescription(
			@CheckForNull final String newDescription) {
		if (equal(newDescription, getDescription())) {
			return this;
		}
		description = newDescription;
		setInNeedOfNewVersion();
		return this;
	}

	/**
	 * Set the name.
	 * 
	 * @param name the name to set
	 * 
	 * @return this
	 */
	public Sequence<SS> setName(@CheckForNull final String name) {
		if (equal(name, getName())) {
			return this;
		}
		this.name = name;
		setInNeedOfNewVersion();
		return this;
	}

	/**
	 * Set the sequence.
	 * 
	 * @param sequence the sequence
	 * 
	 * @return this
	 * 
	 * @throws IllegalArgumentException if any characters in newSequence are
	 *             such that {@link #isLegal(char)} is false.
	 */
	public Sequence<SS> setSequence(final String sequence) {
		checkNotNull(sequence);
		if (sequence.equals(getSequence())) {
			return this;
		}
		for (int i = 0; i < sequence.length(); i++) {
			checkArgument(
					isLegal(
							sequence.charAt(i)),
						"Position " + i + " is ["
								+ sequence.charAt(i)
								+ "] which is not a legal DNA char");
		}

		this.sequence = sequence;
		setInNeedOfNewVersion();
		return this;
	}

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

		retValue.append("Sequence(").append("sequence=").append(
				this.sequence).append(TAB).append("accession=").append(
				this.accession).append(TAB).append("description=").append(
				this.description).append(TAB).append("name=").append(this.name)
				.append(TAB).append(")");

		return retValue.toString();
	}

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

// public Sequence putStates(final Integer idx,
// final CharacterStates states) {
// checkNotNull(idx);
// checkNotNull(states);
// statesByPosition.put(idx, states);
// return this;
// }
