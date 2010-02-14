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
package edu.upenn.cis.ppod.saveorupdate.hibernate;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.util.PPodIterables.equalTo;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IOTUDAO;
import edu.upenn.cis.ppod.dao.hibernate.HibernateDAOFactory.OTUDAOHibernate;
import edu.upenn.cis.ppod.dao.hibernate.HibernateDAOFactory.OTUSetDAOHibernate;
import edu.upenn.cis.ppod.model.IUUPPodEntity;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.saveorupdate.IMergeOTUSet;

/**
 * @author Sam Donnelly
 */
public class MergeOTUSetHibernate implements IMergeOTUSet {

	private final IOTUDAO otuDAO;
	private final Provider<OTU> otuProvider;

	@Inject
	MergeOTUSetHibernate(final OTUSetDAOHibernate otuSetDAO,
			final OTUDAOHibernate otuDAO,
			final Provider<OTUSet> otuSetProvider,
			final Provider<OTU> otuProvider, @Assisted Session s) {
		this.otuDAO = (IOTUDAO) otuDAO.setSession(s);
		this.otuProvider = otuProvider;
	}

	public Map<OTU, OTU> saveOrUpdate(final OTUSet targetOTUSet,
			final OTUSet sourceOTUSet) {
		targetOTUSet.setLabel(sourceOTUSet.getLabel());
		targetOTUSet.setDescription(sourceOTUSet.getDescription());

		// This is for a response to the service client.
		targetOTUSet.setDocId(sourceOTUSet.getDocId());
		// final Set<OTU> clearedOTUs = targetOTUSet.clearOTUs();
		final Set<OTU> newOTUs = newHashSet();
		final Map<OTU, OTU> persistentOTUsByIncomingOTU = newHashMap();
		for (final OTU incomingOTU : sourceOTUSet.getOTUs()) {
			OTU persistedOTU;
			if (null == (persistedOTU = findIf(targetOTUSet.getOTUs(), equalTo(
					incomingOTU.getPPodId(), IUUPPodEntity.getPPodId)))) {

				// See if it's hooked up to another OTUSet
				if (null == (persistedOTU = otuDAO.getOTUByPPodId(incomingOTU
						.getPPodId()))) {
					persistedOTU = otuProvider.get();
					persistedOTU.setPPodId();
				}
			}
			newOTUs.add(persistedOTU);
			persistedOTU.setLabel(incomingOTU.getLabel());
			persistentOTUsByIncomingOTU.put(incomingOTU, persistedOTU);

			// This is for a response to the service client.
			persistedOTU.setDocId(incomingOTU.getDocId());
		}
		targetOTUSet.setOTUs(newOTUs);
		return persistentOTUsByIncomingOTU;
	}
}
