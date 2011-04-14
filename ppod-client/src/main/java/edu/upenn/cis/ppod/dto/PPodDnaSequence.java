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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import com.google.common.collect.ImmutableSet;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
final public class PPodDnaSequence {

	private String sequence;

	@XmlAttribute
	@CheckForNull
	private String name;

	@XmlAttribute
	@CheckForNull
	private String description;

	@XmlAttribute
	@CheckForNull
	private String accession;

	public final static Set<java.lang.Character> LEGAL_CHARS =
			ImmutableSet.of(
					'A', 'a',
					'C', 'c',
					'G', 'g',
					'T', 't',
					'R',
					'Y',
					'K',
					'M',
					'S',
					'W',
					'B',
					'D',
					'H',
					'V',
					'N',
					'-');

	/** For JAXB. */
	PPodDnaSequence() {}

	public PPodDnaSequence(
			final String sequence,
			@CheckForNull final String name,
			@CheckForNull final String description,
			@CheckForNull final String accession) {
		this.sequence = sequence;
		this.name = name;
		this.description = description;
		this.accession = accession;
	}

	@Nullable
	public String getAccession() {
		return accession;
	}

	@Nullable
	public String getDescription() {
		return description;
	}

	@Nullable
	public String getName() {
		return name;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(final String sequence) {
		checkNotNull(sequence);
		this.sequence = sequence;
	}
}
