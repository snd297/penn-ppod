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

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import edu.umd.cs.findbugs.annotations.CheckForNull;

final public class PPodDnaSequenceSet extends UuPPodDomainObjectWithLabel {

	@XmlElement(name = "sequence")
	private final List<PPodDnaSequence> sequences = newArrayList();

	PPodDnaSequenceSet() {}

	public PPodDnaSequenceSet(final String label) {
		super(label);
	}

	public PPodDnaSequenceSet(@CheckForNull final String pPodId,
			final String label) {
		super(pPodId, label);
	}

	public List<PPodDnaSequence> getSequences() {
		return sequences;
	}

}
