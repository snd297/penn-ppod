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
package edu.upenn.cis.ppod.dao;

import java.util.List;

import edu.upenn.cis.ppod.model.OTUSet;

/**
 * An <code>OTUSet</code> DAO.
 * 
 * @author Sam Donnelly
 */
public interface IOTUSetDAO extends IDAO<OTUSet, Long> {

	OTUSet getOTUSetByPPodId(String pPodId);

	List<Object[]> getOTUIdsVersionsByOTUSetIdAndMinPPodVersion(
			Long otuSetPPodId,
			Long minPPodVersion);

	List<Object[]> getMatrixInfosByOTUSetPPodIdAndMinPPodVersion(
			String pPodId,
			Long pPodVersion);

}
