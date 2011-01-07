package edu.upenn.cis.ppod.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class StandardRow {

	private final Long version;

	final List<Integer> states = newArrayList();

	public List<Integer> getStates() {
		return states;
	}
}
