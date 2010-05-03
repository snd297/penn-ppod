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
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model;

import java.util.Set;

import javax.annotation.CheckForNull;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.common.collect.ImmutableSet;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNASequence.TABLE)
public class DNASequence extends Sequence {

	@ManyToOne(fetch = FetchType.LAZY)
	@CheckForNull
	private OTUsToDNASequences otusToSequences;

	/**
	 * The name of the {@code DNASequence} table.
	 */
	static final String TABLE = "DNA_SEQUENCE";

	private final static Set<java.lang.Character> LEGAL_CHARS = ImmutableSet
			.of('A', 'C', 'G', 'T', 'R', 'Y', 'K', 'M', 'S', 'W', 'B', 'D',
					'H', 'V', 'N', '-');

	@Override
	public void accept(final IVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Created for testing.
	 */
	@CheckForNull
	OTUsToDNASequences getOTUsToSequences() {
		return otusToSequences;
	}

	@Override
	public boolean isLegal(final char c) {
		return LEGAL_CHARS.contains(c);
	}

	@Override
	public DNASequence setInNeedOfNewPPodVersionInfo() {
		if (otusToSequences != null) {
			this.otusToSequences.setInNeedOfNewPPodVersion();
		}
		super.setInNeedOfNewPPodVersionInfo();
		return this;
	}

	protected DNASequence setOTUsToSequences(
			@CheckForNull final OTUsToDNASequences otusToSequences) {
		this.otusToSequences = otusToSequences;
		return this;
	}

	@Override
	protected Sequence unsetOTUsToSequences() {
		this.otusToSequences = null;
		return this;
	}

}
