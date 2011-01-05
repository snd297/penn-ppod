/*
 * Copyright (C) 2009 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.services.ppodentity;

import java.util.List;

import edu.upenn.cis.ppod.model.OtuSet;

/**
 * A collection of OTU sets.
 * 
 * @author Sam Donnelly
 */
public interface IOTUSets {

	/**
	 * Add an OTU set to this {@link IOTUSets}.
	 * 
	 * @param otuSet to be added
	 * 
	 * @throws IllegalArgumentException if this already contains the OTU set
	 */
	void addOtuSet(OtuSet otuSet);

	/**
	 * Get the OTU sets contained in this {@code IOTUSets}.
	 * 
	 * @return the OTU sets contained in this {@code IOTUSets}
	 */
	List<OtuSet> getOTUSets();

}