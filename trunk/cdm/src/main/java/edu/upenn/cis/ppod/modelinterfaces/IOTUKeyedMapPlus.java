package edu.upenn.cis.ppod.modelinterfaces;

import java.util.Map;
import java.util.Set;

import edu.upenn.cis.ppod.util.IPair;

public interface IOTUKeyedMapPlus<V extends IOTUKeyedMapValue<?>, P extends IOTUSetChild, OP extends IPair<IOTU, V>>
		extends IOTUKeyedMap<V> {

	void afterUnmarshal(final P parent);

	IOTUKeyedMapPlus<V, P, OP> clear();

	/**
	 * For marshalling {@code rows}. Since a {@code Map}'s key couldn't be an
	 * {@code XmlIDREF} in JAXB as far as we can tell.
	 */
	Set<OP> getOTUSomethingPairs();

	P getParent();

	IOTUKeyedMapPlus<V, P, OP> setParent(final P parent);

	IOTUKeyedMapPlus<V, P, OP> setValues(final Map<IOTU, V> values);

}
