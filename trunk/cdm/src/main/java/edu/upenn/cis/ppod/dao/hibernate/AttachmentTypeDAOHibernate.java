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
package edu.upenn.cis.ppod.dao.hibernate;

import edu.upenn.cis.ppod.model.AttachmentType;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;

/**
 * A Hibernate {@link AttachmentType} DAO.
 * 
 * @author Sam Donnelly
 */
final class AttachmentTypeDAOHibernate extends
		GenericHibernateDAO<AttachmentType, Long> implements
		IAttachmentTypeDAOHibernate {

	public AttachmentType getTypeByNamespaceAndLabel(
			final String namespaceLabel, final String typeLabel) {
		return (AttachmentType) getSession()
				.getNamedQuery(
						AttachmentType.class.getSimpleName()
								+ "-getByNamespaceAndType").setParameter(
						"namespaceLabel", namespaceLabel).setParameter(
						"typeLabel", typeLabel).uniqueResult();
	}

}
