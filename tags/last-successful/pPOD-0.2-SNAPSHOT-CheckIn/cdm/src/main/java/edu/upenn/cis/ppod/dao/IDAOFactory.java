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

import edu.upenn.cis.ppod.model.AttachmentNamespace;
import edu.upenn.cis.ppod.model.AttachmentType;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;

/**
 * Manufactures <code>IDAO</code>s.
 * 
 * @author Sam Donnelly
 */
public interface IDAOFactory {

	/**
	 * Get a {@Study DAO.}
	 * 
	 * @return a {@link Study DAO}.
	 */
	IStudyDAO getStudyDAO();

	/**
	 * Get a {@link CharacterStateMatrix} DAO.
	 * 
	 * @return a {@link CharacterStateMatrix} DAO
	 */
	ICharacterStateMatrixDAO geCharStateMatrixDAO();

	/**
	 * Get an <code>OTU</code> DAO.
	 * 
	 * @return an <code>OTU</code> DAO.
	 */
	IOTUDAO getOTUDAO();

	/**
	 * Get a {@link IOTUSetDAO}.
	 * 
	 * @return a {@link IOTUSetDAO}
	 */
	IOTUSetDAO getOTUSetDAO();

	/**
	 * Get an <code>ICharacterDAO</code>.
	 * 
	 * @return and <code>ICharacterDAO</code>.
	 */
	ICharacterDAO getCharacterDAO();

	/**
	 * Get an {@link ICharacterStateDAO}.
	 * 
	 * @return an {@link ICharacterStateDAO}
	 */
	ICharacterStateDAO getCharacterStateDAO();

	/**
	 * Get an <code>IPPodVersionInfoDAO</code>.
	 * 
	 * @return an <code>IPPodVersionInfoDAO</code>.
	 */
	IPPodVersionInfoDAO getPPodVersionInfoDAO();

	/**
	 * Get an {@link ICharacterStateRowDAO}.
	 * 
	 * @return an {@link ICharacterStateRowDAO}
	 */
	ICharacterStateRowDAO getCharacterStateRowDAO();

	/**
	 * Get a {@link Tree} DAO.
	 * 
	 * @return a {@link Tree} DAO
	 */
	ITreeDAO getTreeDAO();

	/**
	 * Get a {@link TreeSet} DAO.
	 * 
	 * @return a {@link TreeSet} DAO
	 */
	ITreeSetDAO getTreeSetDAO();

	/**
	 * Get a {@link AttachmentNamespace} DAO.
	 * 
	 * @return a {@link AttachmentNamespace} DAO
	 */
	IAttachmentNamespaceDAO getAttachmentNamespaceDAO();

	/**
	 * Get a {@link AttachmentType} DAO.
	 * 
	 * @return a {@link AttachmentType} DAO
	 */
	IAttachmentTypeDAO getAttachmentTypeDAO();

	IUserDAO getPPodUserDAO();

	IPPodGroupDAO getPPodGroupDAO();

	IPPodRoleDAO getPPodRoleDAO();
}
