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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.imodel;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility methods for the {@code imodel} package.
 * 
 * @author Sam Donnelly
 */
public class IModelUtil {

	/**
	 * Take a set of {@link IStandardState}s and put them into a map with their
	 * state numbers as keys.
	 * 
	 * @param states from which to make the map
	 * 
	 * @return a map where the keys are the state numbers and the values are the
	 *         corresponding states
	 */
	public static Map<Integer, IStandardState> toIntegerStateMap(
			final Set<? extends IStandardState> states) {
		final Map<Integer, IStandardState> integersToStates = newHashMap();
		for (final IStandardState state : states) {
			integersToStates.put(state.getStateNumber(), state);
		}
		return integersToStates;
	}

	/**
	 * Take a set of {@link IStandardState}s and put them into a list ordered by
	 * their state numbers.
	 * 
	 * @param states from which to make the list
	 * 
	 * @return a list ordered by the state numbers
	 */
	public static List<IStandardState> toOrderedStates(
			final Set<? extends IStandardState> states) {
		final List<IStandardState> orderedStates = newArrayList();
		final Map<Integer, IStandardState> integersToStates = toIntegerStateMap(states);
		int integersToStatesPos = -1;
		while (orderedStates.size() < states.size()) {
			integersToStatesPos++;
			if (integersToStates.containsKey(integersToStatesPos)) {
				orderedStates.add(integersToStates.get(integersToStatesPos));
			}
		}
		return orderedStates;
	}
}
