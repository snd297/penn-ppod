package edu.upenn.cis.ppod.dto;

/**
 * The different types of {@code Cell}: single, polymorphic, uncertain,
 * unassigned, or inapplicable.
 * <p>
 * Because we're storing these in the db as ordinals they will be:
 * <ul>
 * <li>{@code UNASSIGNED -> 0}</li>
 * <li>{@code SINGLE -> 1}</li>
 * <li>{@code POLYMORPHIC -> 2}</li>
 * <li>{@code UNCERTAIN -> 3}</li>
 * <li>{@code INAPPLICABLE -> 4}</li>
 * </ul>
 */
public enum PPodCellType {

	/** Unassigned, usually written as a {@code "?"} in Nexus files. */
	UNASSIGNED,

	/**
	 * The cell has exactly one state.
	 */
	SINGLE,

	/**
	 * The cell is a conjunctions of states: <em>state1</em> and <em>state2</em>
	 * and ... and <em>stateN</em>.
	 */
	POLYMORPHIC,

	/**
	 * The cell is a disjunction of states: <em>state1</em> or <em>state2</em>
	 * or ... or <em>stateN</em>.
	 */
	UNCERTAIN,

	/** Inapplicable, usually written as a {@code "-"} in Nexus files. */
	INAPPLICABLE;

}