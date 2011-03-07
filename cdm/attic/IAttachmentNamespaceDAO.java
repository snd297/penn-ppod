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

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.model.AttachmentNamespace;
import edu.upenn.cis.ppod.thirdparty.dao.IDAO;

/**
 * {@link AttachmentNamespace} DAO.
 * 
 * @author Sam Donnelly
 */
@ImplementedBy(AttachmentNamespaceDAOHibernate.class)
public interface IAttachmentNamespaceDAO
		extends IDAO<AttachmentNamespace, Long> {

	/**
	 * Get the {@link AttachmentNamespace} with the given namespace.
	 * 
	 * @param label the namespace
	 * @return the {@link AttachmentNamespace} with the given namespace
	 */
	AttachmentNamespace getNamespaceByLabel(String label);
}
