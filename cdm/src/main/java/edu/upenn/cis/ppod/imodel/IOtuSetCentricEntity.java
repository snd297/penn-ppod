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
package edu.upenn.cis.ppod.imodel;

import java.util.Set;

import edu.upenn.cis.ppod.model.OtuSet;

/**
 * A collection of OTU sets, matrices and tree sets.
 * 
 * @author Sam Donnelly
 */
public interface IOtuSetCentricEntity {

	/**
	 * Add an OTU set to this {@link IOTUSets}.
	 * 
	 * @param otuSet to be added
	 * @return <tt>true</tt> if this set did not already contain the specified
	 *         OTU set, {@code false} otherwise
	 */
	OtuSet addOTUSet(OtuSet otuSet);

	Set<OtuSet> getOTUSets();

}