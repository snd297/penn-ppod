package edu.upenn.cis.ppod.modelinterfaces;

import java.util.Map;
import java.util.Set;

import javax.xml.bind.Unmarshaller;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.util.IPair;

public interface IOTUKeyedMapPlus<V extends IOTUKeyedMapValue<?>, P extends IVersionedWithOTUSet, OP extends IPair<OTU, V>>
		extends IOTUKeyedMap<V> {

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent);

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
