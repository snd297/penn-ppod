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
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.ImmutableSet;

/**
 * Represents cell in a standard matrix.
 * <p>
 * Because the type and states of a cell are not independent of one another,
 * this class is set up so that both must be set at the same time.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.FIELD)
public final class PPodStandardCell {

	public static final Set<Integer> EMPTY_STATES = ImmutableSet.of();

	@XmlAttribute
	private PPodCellType type;

	@XmlElement(name = "state")
	private Set<Integer> states = newHashSet();

	PPodStandardCell() {}

	public PPodStandardCell(final PPodCellType type,
			final Set<Integer> states) {
		setTypeAndStates(type, states);
	}

	/**
	 * Get an unmodifiable view of the states.
	 * 
	 * @return an unmodifiable view of the states
	 */
	public Set<Integer> getStates() {
		return Collections.unmodifiableSet(states);
	}

	/**
	 * @return the type
	 */
	public PPodCellType getType() {
		return type;
	}

	public void setInapplicable() {
		type = PPodCellType.INAPPLICABLE;
		states.clear();
	}

	public void setPolymorphic(final Set<Integer> states) {
		checkNotNull(states);
		checkArgument(states.size() > 1);
		type = PPodCellType.POLYMORPHIC;
		this.states.clear();
		this.states.addAll(states);
	}

	public void setSingle(final Integer state) {
		checkNotNull(state);
		type = PPodCellType.SINGLE;
		this.states.clear();
		this.states.add(state);
	}

	private void setTypeAndStates(final PPodCellType type,
			final Set<Integer> states) {
		switch (type) {
			case UNASSIGNED:
				checkArgument(states.size() == 0);
				setUnassigned();
				break;
			case SINGLE:
				checkArgument(states.size() == 1);
				setSingle(getOnlyElement(states));
				break;
			case POLYMORPHIC:
				setPolymorphic(states);
				break;
			case UNCERTAIN:
				setUncertain(states);
				break;
			case INAPPLICABLE:
				checkArgument(states.size() == 0);
				setInapplicable();
				break;
			default:
				throw new AssertionError();
		}
	}

	public void setUnassigned() {
		type = PPodCellType.UNASSIGNED;
		states.clear();
	}

	public void setUncertain(final Set<Integer> states) {
		checkNotNull(states);
		checkArgument(states.size() > 1);
		type = PPodCellType.UNCERTAIN;
		this.states.clear();
		this.states.addAll(states);
	}

}
