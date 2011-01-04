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
import java.util.Set;

import edu.upenn.cis.ppod.util.IPair;

public interface IOtuKeyedMapPlus<V extends IChild<?>, P extends IChild<IOtuSet>, OP extends IPair<IOtu, V>>
		extends IOtuKeyedMap<V> {

	void afterUnmarshal(final P parent);

	void clear();

	/**
	 * For marshalling {@code rows}. Since a {@code Map}'s key and value
	 * couldn't be an interface. And even if we use a concrete classes we,
	 * couldn't get it to work maybe because the key marshalled as a
	 * {@code XmlIDREF}.
	 */
	Set<OP> getOTUKeyedPairs();

	P getParent();

	void setParent(final P parent);

	void setValues(final Map<IOtu, V> values);

}
