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

import java.util.Set;

import edu.upenn.cis.ppod.model.OTUSet;

/**
 * A collection of OTU sets, matrices and tree sets.
 * 
 * @author Sam Donnelly
 */
public interface IPPodEntities {

	/**
	 * Get the OTU set with the given pPOD id, or {@code null} if there is no
	 * such OTU set, or {@code null} if {@code pPodId} is {@code null}.
	 * 
	 * @param pPodId the pPOD id, or {@code null}
	 * @return the OTU set with the given pPOD id, or {@code null} if there is
	 *         no such OTU set, or {@code null} if {@code pPodId} is {@code
	 *         null}
	 */
	OTUSet getOTUSetByPPodId(String pPodId);

	/**
	 * Add an OTU set to this {@link IPPodEntities}.
	 * 
	 * @param otuSet to be added
	 * @return {@code otuSet}
	 */
	OTUSet addOTUSet(OTUSet otuSet);

	/**
	 * Get an unmodifiable view of the OTU sets.
	 * 
	 * @return the otuSets
	 */
	Set<OTUSet> getOTUSets();

}