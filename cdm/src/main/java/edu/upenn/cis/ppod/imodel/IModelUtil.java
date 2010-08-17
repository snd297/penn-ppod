package edu.upenn.cis.ppod.imodel;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class IModelUtil {
	public static Map<Integer, IStandardState> toIntegerStateMap(
			final Set<? extends IStandardState> states) {
		final Map<Integer, IStandardState> integersToStates = newHashMap();
		for (final IStandardState state : states) {
			integersToStates.put(state.getStateNumber(), state);
		}
		return integersToStates;
	}

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
