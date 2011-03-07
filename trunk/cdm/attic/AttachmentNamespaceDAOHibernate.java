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

import static com.google.common.base.Preconditions.checkNotNull;

import org.hibernate.Session;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.model.AttachmentNamespace;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;

/**
 * @author Sam Donnelly
 */
final class AttachmentNamespaceDAOHibernate
		extends GenericHibernateDAO<AttachmentNamespace, Long>
		implements IAttachmentNamespaceDAO {

	@Inject
	AttachmentNamespaceDAOHibernate(final Session session) {
		checkNotNull(session);
		setSession(session);
	}

	/** {@inheritDoc} */
	public AttachmentNamespace getNamespaceByLabel(final String namespace) {
		return (AttachmentNamespace) getSession()
				.getNamedQuery(
						AttachmentNamespace.class.getSimpleName()
								+ "-getByNamespace")
				.setParameter("namespace", namespace)
				.uniqueResult();
	}
}
