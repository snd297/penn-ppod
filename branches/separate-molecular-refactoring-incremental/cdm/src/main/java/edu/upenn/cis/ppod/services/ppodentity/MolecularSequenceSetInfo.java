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
package edu.upenn.cis.ppod.services.ppodentity;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * @author Sam Donnelly
 * 
 */
public class MolecularSequenceSetInfo extends PPodEntityInfoWDocId {

	private Map<String, Long> sequenceVersionsByOTUDocId = newHashMap();

	@XmlElementWrapper(name = "sequenceVersionsByOTUDocId")
	public Map<String, Long> getSequenceVersionsByOTUDocId() {
		return sequenceVersionsByOTUDocId;
	}
}
