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
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;

/**
 * Manufactures {@link IDAO}s.
 * 
 * @author Sam Donnelly
 */
public interface IDAOFactory {

	/**
	 * Get an {@link Study} DAO.
	 * 
	 * @return an {@code Study}
	 */
	IStudyDAO getStudyDAO();

	/**
	 * Get a {@link CharacterStateMatrix} DAO.
	 * 
	 * @return a {@code CharacterStateMatrix} DAO
	 */
	ICharacterStateMatrixDAO getCharacterStateMatrixDAO();

	/**
	 * Get an {@link OTU} DAO.
	 * 
	 * @return an <code>OTU</code> DAO.
	 */
	IOTUDAO getOTUDAO();

	/**
	 * Get a {@link IOTUSetDAO}.
	 * 
	 * @return a {@code IOTUSetDAO}
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
	 * @return an {@code ICharacterStateDAO}
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
	 * @return an {@code ICharacterStateRowDAO}
	 */
	ICharacterStateRowDAO getCharacterStateRowDAO();

	/**
	 * Get a {@link Tree} DAO.
	 * 
	 * @return a {@code Tree} DAO
	 */
	ITreeDAO getTreeDAO();

	/**
	 * Get a {@link TreeSet} DAO.
	 * 
	 * @return a {@code TreeSet} DAO
	 */
	ITreeSetDAO getTreeSetDAO();

	/**
	 * Get a {@link AttachmentNamespace} DAO.
	 * 
	 * @return a {@code AttachmentNamespace} DAO
	 */
	IAttachmentNamespaceDAO getAttachmentNamespaceDAO();

	/**
	 * Get a {@link AttachmentType} DAO.
	 * 
	 * @return a {@code AttachmentType} DAO
	 */
	IAttachmentTypeDAO getAttachmentTypeDAO();

	IUserDAO getPPodUserDAO();

	IPPodGroupDAO getPPodGroupDAO();

	IPPodRoleDAO getPPodRoleDAO();
}
