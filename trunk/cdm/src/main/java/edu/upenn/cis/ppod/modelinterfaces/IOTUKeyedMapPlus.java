package edu.upenn.cis.ppod.modelinterfaces;

import java.util.Map;
import java.util.Set;

import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.util.IPair;

public interface IOTUKeyedMapPlus<V extends IOTUKeyedMapValue<?>, P extends IOTUSetChild, OP extends IPair<OTU, V>>
		extends IOTUKeyedMap<V> {

	void afterUnmarshal(final P parent);

	IOTUKeyedMapPlus<V, P, OP> clear();

	/**
	 * For marshalling {@code rows}. Since a {@code Map}'s key couldn't be an
	 * {@code XmlIDREF} in JAXB - at least not easily.
	 */
	Set<OP> getOTUSomethingPairs();

	P getParent();

	IOTUKeyedMapPlus<V, P, OP> setParent(final P parent);

	IOTUKeyedMapPlus<V, P, OP> setValues(final Map<OTU, V> values);

}
