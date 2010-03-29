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

import org.hibernate.Session;

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.dao.IAttachmentNamespaceDAO;
import edu.upenn.cis.ppod.dao.IAttachmentTypeDAO;
import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.dao.IDNACharacterDAO;
import edu.upenn.cis.ppod.dao.IOTUSetDAO;
import edu.upenn.cis.ppod.dao.IStudyDAO;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;

/**
 * If study is new, make it persistent. If it was already persisted, update it.
 * 
 * @author Sam Donnelly
 */
@ImplementedBy(SaveOrUpdateStudy.class)
public interface ISaveOrUpdateStudy {

	Study save(Study study);

	Study update(Study study);

	/**
	 * If study is new, make it persistent. If it was already persisted, update
	 * it.
	 * 
	 * @param incomingStudy to be made persistent or updated
	 */
	void saveOrUpdate(Study dbStudy, Study incomingStudy);

	static interface IFactory {

		/**
		 * Create {@link ISaveOrUpdateStudy}s that depend on a {@link Session}.
		 * 
		 * @param session dependency
		 * @param newPPodVersionInfo
		 * 
		 * @return a new {@link ISaveOrUpdateStudy}
		 */
		ISaveOrUpdateStudy create(Session session,
				IStudyDAO studyDAO,
				IOTUSetDAO otuSetDAO,
				IDNACharacterDAO dnaCharacterDAO,
				IAttachmentNamespaceDAO attachmentNamespaceDAO,
				IAttachmentTypeDAO attachmentTypeDAO,
				IDAO<Object, Long> dao,
				INewPPodVersionInfo newPPodVersionInfo);

	}
}
