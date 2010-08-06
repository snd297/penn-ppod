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
