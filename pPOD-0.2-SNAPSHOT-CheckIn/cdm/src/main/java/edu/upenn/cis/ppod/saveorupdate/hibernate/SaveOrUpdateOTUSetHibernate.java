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

import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Maps.newHashMap;
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
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateOTUSet;

/**
 * @author Sam Donnelly
 */
public class SaveOrUpdateOTUSetHibernate implements ISaveOrUpdateOTUSet {

	private final IOTUDAO otuDAO;
	private final Provider<OTU> otuProvider;

	@Inject
	SaveOrUpdateOTUSetHibernate(final OTUSetDAOHibernate otuSetDAO,
			final OTUDAOHibernate otuDAO,
			final Provider<OTUSet> otuSetProvider,
			final Provider<OTU> otuProvider, @Assisted Session s) {
		this.otuDAO = (IOTUDAO) otuDAO.setSession(s);
		this.otuProvider = otuProvider;
	}

	public Map<OTU, OTU> saveOrUpdate(final OTUSet incomingOTUSet,
			final OTUSet persistedOTUSet) {
		persistedOTUSet.setLabel(incomingOTUSet.getLabel());
		persistedOTUSet.setDescription(incomingOTUSet.getDescription());

		// This is for a response to the service client.
		persistedOTUSet.setDocId(incomingOTUSet.getDocId());
		final Set<OTU> clearedOTUs = persistedOTUSet.clearOTUs();
		final Map<OTU, OTU> persistentOTUsByIncomingOTU = newHashMap();
		for (final OTU incomingOTU : incomingOTUSet.getOTUs()) {
			OTU persistedOTU;
			if (null == (persistedOTU = findIf(clearedOTUs, compose(
					equalTo(incomingOTU.getPPodId()), IUUPPodEntity.getPPodId)))) {
				if (null == (persistedOTU = otuDAO.getOTUByPPodId(incomingOTU
						.getPPodId()))) {
					persistedOTU = otuProvider.get();
					persistedOTU.setPPodId();
				}
			}
			persistedOTUSet.addOTU(persistedOTU);
			persistedOTU.setLabel(incomingOTU.getLabel());
			persistentOTUsByIncomingOTU.put(incomingOTU, persistedOTU);

			// This is for a response to the service client.
			persistedOTU.setDocId(incomingOTU.getDocId());
		}
		return persistentOTUsByIncomingOTU;
	}
}
