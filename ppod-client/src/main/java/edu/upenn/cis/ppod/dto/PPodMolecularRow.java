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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.google.common.collect.ImmutableSet;

@XmlAccessorType(XmlAccessType.FIELD)
abstract class PPodMolecularRow {

	public static final Set<Character> DELIMITERS = ImmutableSet.of(
						'(',
						')',
						'{',
						'}');

	private String sequence;

	PPodMolecularRow() {}

	PPodMolecularRow(final String sequence) {
		this.sequence = checkNotNull(sequence);
	}

	abstract Set<Character> getLegalChars();

	public final String getSequence() {
		return sequence;
	}

	public final void setSequence(final String sequence) {
		for (int i = 0; i < sequence.length(); i++) {
			checkArgument(
					getLegalChars().contains(sequence.charAt(i))
							|| DELIMITERS.contains(sequence.charAt(i)),
					"position " + i + " is " + sequence.charAt(i)
							+ " which is illegal");
		}
		this.sequence = sequence;
	}
}
