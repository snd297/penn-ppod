package edu.upenn.cis.ppod.util;

import static com.google.common.base.Predicates.compose;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * @author Sam Donnelly
 * 
 */
public class PPodPredicates {

	/**
	 * Functionally equivalent to
	 * 
	 * <pre>
	 * compose(Predicates.equalTo(target), func);
	 * </pre>
	 * 
	 * @param <A>
	 * @param <B>
	 * @param target
	 * @param func
	 * @return
	 */
// public static <A, B> Predicate<A> equalTo(final B target,
// final Function<A, ? extends B> func) {
// return compose(Predicates.equalTo(target), func);
// }

	private PPodPredicates() {
		throw new IllegalStateException("Can't instantiate a PPodIterables");
	}
}
