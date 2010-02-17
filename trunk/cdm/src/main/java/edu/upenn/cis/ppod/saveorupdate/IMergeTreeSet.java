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
package edu.upenn.cis.ppod.saveorupdate;

import java.util.Map;

import edu.upenn.cis.ppod.model.LazyPPodVersionInfo;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.TreeSet;

/**
 * @author Sam Donnelly
 */
public interface IMergeTreeSet {
	TreeSet merge(TreeSet targetTreeSet, TreeSet sourceTreeSet,
			OTUSet newTargetTreeSetOTUSet, Map<OTU, OTU> mergedOTUsBySourceOTU,
			LazyPPodVersionInfo lazyPodVersionInfo);
}
