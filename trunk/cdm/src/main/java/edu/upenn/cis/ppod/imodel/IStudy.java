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

import javax.xml.bind.annotation.XmlSeeAlso;

import edu.upenn.cis.ppod.dto.IOTUSets;
import edu.upenn.cis.ppod.model.OtuSet;
import edu.upenn.cis.ppod.model.Study;

/**
 * A collection of work - inspired by a Mesquite project - sets of OTU sets and,
 * through the OTU sets, matrices, sequences, and tree sets.
 * 
 * @author Sam Donnelly
 */
@XmlSeeAlso(Study.class)
public interface IStudy extends IUuPPodEntity, IOTUSets, ILabeled {

	void addOtuSet(int pos, OtuSet otuSet);

	void removeOTUSet(final OtuSet otuSet);

	void setLabel(final String label);

}