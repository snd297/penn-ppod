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
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public final class PPodStandardCharacter extends UuPPodDomainObjectWithLabel {

	@XmlAttribute
	private String mesquiteId;

	@XmlElement(name = "state")
	private final Set<PPodStandardState> states = newHashSet();

	/** For JAXB. */
	@SuppressWarnings("unused")
	private PPodStandardCharacter() {}

	public PPodStandardCharacter(
			@CheckForNull final String pPodId,
			final String label,
			@CheckForNull final String mesquiteId) {
		super(pPodId, label);
		checkNotNull(mesquiteId);
		this.mesquiteId = mesquiteId;
	}

	@Nullable
	public String getMesquiteId() {
		return mesquiteId;
	}

	@Nullable
	public PPodStandardState getState(final int stateNo) {
		return find(states,
				compose(equalTo(stateNo), PPodStandardState.getStateNumber),
				null);
	}

	public Set<PPodStandardState> getStates() {
		return states;
	}

	public void setMesquiteId(@CheckForNull final String mesquiteId) {
		this.mesquiteId = mesquiteId;
	}
}
