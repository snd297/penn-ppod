package edu.upenn.cis.ppod.modelinterfaces;

import java.util.Map;
import java.util.Set;

import javax.xml.bind.Unmarshaller;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.util.IPair;
import edu.upenn.cis.ppod.util.IVisitor;

public interface IOTUKeyedMap<V extends IOTUKeyedMapValue<?>, P extends IVersionedWithOTUSet, OP extends IPair<OTU, V>> {
	void accept(IVisitor visitor);

	V get(OTU key);

	Map<OTU, V> getValues();

	/**
	 * Associates {@code value} with {@code key} in this map. If the map
	 * previously contained a mapping for {@code key}, the original value is
	 * replaced by the specified value.
	 * <p>
	 * This method calls {@code getParent().setInNeedOfNewVersionInfo()} if this
	 * method changes anything
	 * 
	 * @param key key
	 * @param newValue new value for {@code key}
	 * @param parent the owning object
	 * 
	 * @return the previous value associated with <tt>otu</tt>, or <tt>null</tt>
	 *         if there was no mapping for <tt>otu</tt>
	 * 
	 * @throws IllegalStateException if {@link #getParent() == null}
	 * @throws IllegalStateException if {@code getParent().getOTUSet() == null}
	 * @throws IllegalArgumentException if {@code otu} does not belong to
	 *             {@code parent.getOTUSet()}
	 * @throws IllegalArgumentException if there's already a value
	 *             {@code .equals} to {@code value}
	 */
	V put(OTU key, V value);

	/**
	 * Set the keys of this {@code OTUKeyedMap} to the OTU's in
	 * {@code getParent()}'s OTU set.
	 * <p>
	 * Any newly introduced keys will map to {@code null} values.
	 * 
	 * @return this
	 */
	@CheckForNull
	IOTUKeyedMap<V, P, OP> setOTUs();

	/**
	 * For marshalling {@code rows}. Since a {@code Map}'s key couldn't be an
	 * {@code XmlIDREF} in JAXB - at least not easily.
	 */
	Set<OP> getOTUSomethingPairs();

	P getParent();

	IOTUKeyedMap<V, P, OP> setParent(final P parent);

	IOTUKeyedMap<V, P, OP> clear();

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent);

	IOTUKeyedMap<V, P, OP> setValues(final Map<OTU, V> values);

}
