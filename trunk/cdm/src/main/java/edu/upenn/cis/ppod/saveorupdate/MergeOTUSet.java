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

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;
import static edu.upenn.cis.ppod.util.PPodPredicates.equalTo;

import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.IUUPPodEntity;

/**
 * Merge form {@code sourceOTUSet} onto {@code targetOTUSet}.
 * 
 * @author Sam Donnelly
 */
public class MergeOTUSet implements IMergeOTUSet {

	private final Provider<OTU> otuProvider;
	private final INewPPodVersionInfo newPPodVersionInfo;

	@Inject
	MergeOTUSet(final Provider<OTU> otuProvider,
			@Assisted INewPPodVersionInfo newPPodVersionInfo) {
		this.otuProvider = otuProvider;
		this.newPPodVersionInfo = newPPodVersionInfo;
	}

	public Map<OTU, OTU> saveOrUpdate(final OTUSet targetOTUSet,
			final OTUSet sourceOTUSet) {
		targetOTUSet.setLabel(sourceOTUSet.getLabel());
		targetOTUSet.setDescription(sourceOTUSet.getDescription());

		// This is for a response to the service client.
		targetOTUSet.setDocId(sourceOTUSet.getDocId());
		// final Set<OTU> clearedOTUs = targetOTUSet.clearOTUs();
		final Set<OTU> newOTUs = newHashSet();
		final Map<OTU, OTU> source2TargetOTUs = newHashMap();
		for (final OTU sourceOTU : sourceOTUSet.getOTUs()) {
			OTU targetOTU;
			if (null == (targetOTU = findIf(targetOTUSet.getOTUs(), equalTo(
					sourceOTU.getPPodId(), IUUPPodEntity.getPPodId)))) {
				targetOTU = otuProvider.get();
				targetOTU.setPPodVersionInfo(newPPodVersionInfo
						.getNewPPodVersionInfo());
				targetOTU.setPPodId();
			}
			newOTUs.add(targetOTU);
			targetOTU.setLabel(sourceOTU.getLabel());
			source2TargetOTUs.put(sourceOTU, targetOTU);

			// This is for a response to the service client.
			targetOTU.setDocId(sourceOTU.getDocId());
		}
		targetOTUSet.setOTUs(newOTUs);
		return source2TargetOTUs;
	}
}
