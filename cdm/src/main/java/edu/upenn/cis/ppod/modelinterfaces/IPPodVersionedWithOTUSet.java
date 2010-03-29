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

import edu.upenn.cis.ppod.model.OTUSet;

/**
 * {@code IPPodVersioned}s that contain an {@code OTUSet}.
 * <p>
 * This is an artificial interface cooked up for {@link OTUKeyedBimap}.
 * 
 * @author Sam Donnelly
 */
public interface IPPodVersionedWithOTUSet extends IPPodVersioned {
	/**
	 * Get the {@code OTUSet} associated with this object.
	 * 
	 * @return the {@code OTUSet} associated with this object
	 */
	OTUSet getOTUSet();
}
