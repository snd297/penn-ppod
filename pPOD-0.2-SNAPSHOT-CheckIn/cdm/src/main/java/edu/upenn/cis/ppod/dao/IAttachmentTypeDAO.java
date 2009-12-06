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

import edu.upenn.cis.ppod.model.AttachmentType;

/**
 * A {@link AttachmentType} DAO.
 * 
 * @author Sam Donnelly
 */
public interface IAttachmentTypeDAO extends IDAO<AttachmentType, Long> {

	/**
	 * Get the {@link AttachmentType} of type {@code type} and in the given
	 * namespace.
	 * 
	 * @param attachmentNamespace the namespace
	 * @param type the type we're interested in
	 * @return the {@link AttachmentType} of type {@code type} and in the given
	 *         namespace
	 */
	AttachmentType getAttachmentTypeByNamespaceAndType(String namespaceLabel,
			String typeLabel);

}
