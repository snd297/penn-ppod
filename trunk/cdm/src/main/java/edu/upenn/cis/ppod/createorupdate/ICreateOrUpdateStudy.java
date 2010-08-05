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
package edu.upenn.cis.ppod.createorupdate;

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.dao.IAttachmentNamespaceDAO;
import edu.upenn.cis.ppod.dao.IAttachmentTypeDAO;
import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.dao.IOTUSetDAO;
import edu.upenn.cis.ppod.dao.IStudyDAO;
import edu.upenn.cis.ppod.modelinterfaces.INewVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.IStudy;

/**
 * If study is new, make it persistent. If it was already persisted, update it.
 * 
 * @author Sam Donnelly
 */
@ImplementedBy(CreateOrUpdateStudy.class)
public interface ICreateOrUpdateStudy {

	/**
	 * If study is new, make it persistent. If it was already persisted, update
	 * it.
	 * 
	 * @param incomingStudy to be made persistent or updated
	 */
	void createOrUpdateStudy();

	/**
	 * Return the {@code Study} in a persistent state.
	 * 
	 * @return the {@code Study} in a persistent state.
	 */
	IStudy getDbStudy();

	static interface IFactory {

		/**
		 * Create {@link ISaveOrUpdateStudy}s that depend on a {@link Session}.
		 * 
		 * @param session dependency
		 * @param newVersionInfo
		 * 
		 * @return a new {@link ISaveOrUpdateStudy}
		 */
		ICreateOrUpdateStudy create(IStudy incomingStudy,
				IStudyDAO studyDAO,
				IOTUSetDAO otuSetDAO,
				IAttachmentNamespaceDAO attachmentNamespaceDAO,
				IAttachmentTypeDAO attachmentTypeDAO,
				IDAO<Object, Long> dao,
				INewVersionInfo newVersionInfo);

	}
}
