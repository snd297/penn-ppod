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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.annotation.CheckForNull;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
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

	/**
	 * The characters that are legal in a {@code DNASequence}.
	 */
	public final static Set<java.lang.Character> LEGAL_CHARS =
			ImmutableSet.of('A', 'C', 'G', 'T', 'R', 'Y', 'K', 'M', 'S', 'W',
					'B', 'D', 'H', 'V', 'N', '-');

	/**
	 * The name of the {@code DNASequence} table.
	 */
	public static final String TABLE = "DNA_SEQUENCE";

	public static final String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	/** The owning object. */
	@CheckForNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = DNASequences.JOIN_COLUMN)
	private DNASequences sequences;

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visit(this);
	}

	/**
	 * Created for testing.
	 */
	@CheckForNull
	DNASequences getSequences() {
		return sequences;
	}

	@Override
	public boolean isLegal(final char c) {
		return LEGAL_CHARS.contains(c);
	}

	@Override
	public DNASequence setInNeedOfNewVersion() {
		if (sequences != null) {
			sequences.setInNeedOfNewVersion();
		}
		super.setInNeedOfNewVersion();
		return this;
	}

	protected DNASequence setOTUsToSequences(
			@CheckForNull final DNASequences otusToSequences) {
		this.sequences = otusToSequences;
		return this;
	}

	public DNASequence unsetOTUKeyedMap() {
		this.sequences = null;
		return this;
	}

}
