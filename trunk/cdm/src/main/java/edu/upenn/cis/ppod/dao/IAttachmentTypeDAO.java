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

import edu.upenn.cis.ppod.imodel.IAttachmentType;
import edu.upenn.cis.ppod.thirdparty.dao.IDAO;

/**
 * A {@link AttachmentType} DAO.
 * 
 * @author Sam Donnelly
 */
public interface IAttachmentTypeDAO extends IDAO<IAttachmentType, Long> {

	/**
	 * Get the attachment type with label {@code typeLabel} and in the given
	 * namespace.
	 * 
	 * @param namespaceLabel the label of namespace
	 * @param typeLabel the label of the type we're interested in
	 * @return the attachment type with label {@code typeLabel} and in the given
	 *         namespace
	 */
	IAttachmentType getTypeByNamespaceAndLabel(
			String namespaceLabel,
			String typeLabel);

}
