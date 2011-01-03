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

import java.util.Map;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public interface IOTUKeyedMap<V extends IChild<?>>
		extends IVisitable {

	/**
	 * Do processing that must occur after JAXB unmarshalling is complete - that
	 * is, after the OTUs' {@code @XmlIDREF}'s have been resolved.
	 */
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
	V get(IOtuChangeCase key);

	/**
	 * Get the map that makes up this OTU-keyed map.
	 * 
	 * @return the map that makes up this OTU-keyed map
	 */
	Map<IOtuChangeCase, V> getValues();

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
	@CheckForNull
	V put(IOtuChangeCase key, V value);

	/**
	 * Set the keys of this {@code OTUKeyedMap} to the OTUs in its parent's
	 * {@code OTUSet}.
	 * <p>
	 * Any newly introduced keys will map to {@code null} values.
	 * <p>
	 * See {@code IOTUKeyedMapPlus} for a subinterface with a parent-setting
	 * operation
	 * 
	 * @see IOTUKeyedMapPlus
	 */
	void updateOTUs();

}
