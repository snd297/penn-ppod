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

import java.util.Set;

import javax.xml.bind.annotation.XmlSeeAlso;

import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.services.ppodentity.IOTUSets;

/**
 * A collection of work - inspired by a Mesquite project - sets of OTU sets and,
 * through the OTU sets, matrices, and tree sets.
 * 
 * @author Sam Donnelly
 */
@XmlSeeAlso(Study.class)
public interface IStudy extends ILabeled, IOTUSets, IUUPPodEntity {

	/** {@inheritDoc} */
	Set<IOTUSet> getOTUSets();

	/**
	 * Remove an OTU set from this Study.
	 * 
	 * @param otuSet to be removed
	 * 
	 * @return {@code true} if the study contained the specified OTU set
	 */
	boolean removeOTUSet(final IOTUSet otuSet);

	/**
	 * Set the label.
	 * 
	 * @param label the label to set
	 * 
	 * @return this
	 */
	IStudy setLabel(final String label);

}