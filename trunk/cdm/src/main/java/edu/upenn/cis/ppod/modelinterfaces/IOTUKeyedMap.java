package edu.upenn.cis.ppod.modelinterfaces;

import java.util.Map;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * @author Sam Donnelly
 */
public interface IOTUKeyedMap<V extends IOTUKeyedMapValue<?>> {

	void accept(IVisitor visitor);

	void afterUnmarshal();

	/**
	 * Returns the value to which the specified key is mapped which will be
	 * {@code null} if {@link #setOTUs()} has been called with newly introduced
	 * OTUs.
	 * 
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped which will be
	 *         {@code null} if {@link #setOTUs()} has been called with newly
	 *         introduced OTUs
	 * @throws IllegalArgumentException if {@code key} is not a key in this
	 *             OTU-keyed map
	 * @throws NullPointerException if the specified key is null
	 */
	V get(OTU key);

	/**
	 * Get the map that makes up this OTU-keyed map.
	 * 
	 * @return the map that makes up this OTU-keyed map
	 */
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
	 * Set the keys of this {@code OTUKeyedMap} to the OTU's in its parent's
	 * {@code OTUSet}.
	 * <p>
	 * Any newly introduced keys will map to {@code null} values.
	 * <p>
	 * See {@code IOTUKeyedMapPlus} for a subinterface with a parent-setting
	 * operation
	 * 
	 * @return this
	 * @see IOTUKeyedMapPlus
	 */
	@CheckForNull
	IOTUKeyedMap<V> setOTUs();

}
