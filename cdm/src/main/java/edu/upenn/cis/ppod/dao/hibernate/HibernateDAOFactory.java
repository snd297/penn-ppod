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

import java.util.List;

import javax.annotation.Nullable;

import org.hibernate.Session;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.ICharacterDAO;
import edu.upenn.cis.ppod.dao.ICharacterStateDAO;
import edu.upenn.cis.ppod.dao.ICharacterStateMatrixDAO;
import edu.upenn.cis.ppod.dao.ICharacterStateRowDAO;
import edu.upenn.cis.ppod.dao.IDAOFactory;
import edu.upenn.cis.ppod.dao.IOTUDAO;
import edu.upenn.cis.ppod.dao.IOTUSetDAO;
import edu.upenn.cis.ppod.dao.IPPodGroupDAO;
import edu.upenn.cis.ppod.dao.IPPodRoleDAO;
import edu.upenn.cis.ppod.dao.IPPodVersionInfoDAO;
import edu.upenn.cis.ppod.dao.ITreeDAO;
import edu.upenn.cis.ppod.dao.ITreeSetDAO;
import edu.upenn.cis.ppod.dao.IUserDAO;
import edu.upenn.cis.ppod.model.Character;
import edu.upenn.cis.ppod.model.CharacterState;
import edu.upenn.cis.ppod.model.CharacterStateRow;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.model.security.PPodGroup;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;
import edu.upenn.cis.ppod.thirdparty.model.security.Role;

// TODO: Auto-generated Javadoc
/**
 * Manufactures DAO's for talking to the database with straight Hibernate (as
 * opposed to using EJB's).
 * <p>
 * A stand-alone {@link IDAOFactory} that returns unmanaged DAO beans for use in
 * any environment Hibernate has been configured for. Uses
 * HibernateUtil/SessionFactory and Hibernate context propagation
 * (CurrentSessionContext), thread-bound or transaction-bound, and transaction
 * scoped.
 * 
 * @author Sam Donnelly
 */
public class HibernateDAOFactory implements IDAOFactory {

	/**
	 * Makes {@link HibernateDAOFactory}s.
	 */
	public static interface IFactory {

		/**
		 * Create a {@link HibernateDAOFactory}.
		 * 
		 * @param session for manufactured DAO's to use
		 * @return a {@link HibernateDAOFactory}
		 */
		HibernateDAOFactory create(Session session);
	}

	/** The session. */
	private final Session session;

	/**
	 * Instantiates a new hibernate dao factory.
	 * 
	 * @param studyDAOProvider the study dao provider
	 * @param charStateMatrixDAOProvider the char state matrix dao provider
	 * @param session the session
	 */
	@Inject
	public HibernateDAOFactory(
			final Provider<StudyDAOHibernate> studyDAOProvider,
			final Provider<CharacterStateMatrixDAOHibernate> charStateMatrixDAOProvider,
			@Assisted @Nullable final Session session) {
		this.studyDAOProvider = studyDAOProvider;
		this.charStateMatrixProvider = charStateMatrixDAOProvider;
		this.session = session;
	}

	/**
	 * An {@link IOTUDAO} Hibernate DAO.
	 */
	public static class OTUDAOHibernate extends GenericHibernateDAO<OTU, Long>
			implements IOTUDAO {

		/**
		 * Gets the oTU by p pod id.
		 * 
		 * @param pPodId the pod id
		 * @return the oTU by p pod id
		 */
		public OTU getOTUByPPodId(String pPodId) {
			return (OTU) getSession().getNamedQuery(
					OTU.class.getSimpleName() + "-getByPPodId").setParameter(
					"pPodId", pPodId).uniqueResult();
		}
	}

	/**
	 * An {@link OTUSet} Hibernate DAO.
	 */
	public static class OTUSetDAOHibernate extends
			GenericHibernateDAO<OTUSet, Long> implements IOTUSetDAO {

		/**
		 * Gets the oTU set by p pod id.
		 * 
		 * @param pPodId the pod id
		 * @return the oTU set by p pod id
		 */
		public OTUSet getOTUSetByPPodId(final String pPodId) {
			return (OTUSet) getSession().getNamedQuery(
					OTUSet.class.getSimpleName() + "-getByPPodId")
					.setParameter("pPodId", pPodId).uniqueResult();
		}

		/**
		 * Gets the oTU ids versions by otu set id and min p pod version.
		 * 
		 * @param otuId the otu id
		 * @param minPPodVersion the min p pod version
		 * @return the oTU ids versions by otu set id and min p pod version
		 */
		@SuppressWarnings("unchecked")
		public List<Object[]> getOTUIdsVersionsByOTUSetIdAndMinPPodVersion(
				final Long otuId, final Long minPPodVersion) {
			return (List<Object[]>) getSession()
					.getNamedQuery(
							OTUSet.class.getSimpleName()
									+ "-getOTUPPodIdsVersionsByOTUSetIdAndMinPPodVersion")
					.setParameter("otuId", otuId).setParameter(
							"minPPodVersion", minPPodVersion).list();
		}

		/**
		 * Gets the matrix infos by otu set p pod id and min p pod version.
		 * 
		 * @param otuSetPPodId the otu set p pod id
		 * @param minPPodVersion the min p pod version
		 * @return the matrix infos by otu set p pod id and min p pod version
		 */
		@SuppressWarnings("unchecked")
		public List<Object[]> getMatrixInfosByOTUSetPPodIdAndMinPPodVersion(
				final String otuSetPPodId, final Long minPPodVersion) {
			return (List<Object[]>) getSession().getNamedQuery(
					OTUSet.class.getSimpleName()
							+ "-getMatrixInfosByOTUSetPPodIdAndMinPPodVersion")
					.setParameter("otuSetPPodId", otuSetPPodId).setParameter(
							"minPPodVersion", minPPodVersion).list();
		}

	}

	/**
	 * An {@link Character} Hibernate DAO.
	 */
	public static class CharacterDAOHibernate extends
			GenericHibernateDAO<Character, Long> implements ICharacterDAO {

		/**
		 * Gets the character by p pod id.
		 * 
		 * @param pPodId the pod id
		 * @return the character by p pod id
		 */
		public Character getCharacterByPPodId(String pPodId) {
			if (pPodId == null) {
				return null;
			}
			return (Character) getSession().getNamedQuery(
					Character.class.getSimpleName() + "-getByPPodId")
					.setParameter("pPodId", pPodId).uniqueResult();
		}
	}

	/**
	 * A default {@link CharacterState} Hibernate DAO.
	 */
	public static class CharacterStateDAOHibernate extends
			GenericHibernateDAO<CharacterState, Long> implements
			ICharacterStateDAO {}

	/**
	 * A default {@link CharacterStateRow} Hibernate DAO.
	 */
	public static class CharacterStateRowDAOHibernate extends
			GenericHibernateDAO<CharacterStateRow, Long> implements
			ICharacterStateRowDAO {

		/**
		 * Gets the cell idxs versions by row id and min version.
		 * 
		 * @param rowId the row id
		 * @param minVersion the min version
		 * @return the cell idxs versions by row id and min version
		 */
		@SuppressWarnings("unchecked")
		public List<Object[]> getCellIdxsVersionsByRowIdAndMinVersion(
				Long rowId, Long minVersion) {
			return (List<Object[]>) getSession().getNamedQuery(
					CharacterStateRow.class.getSimpleName()
							+ "-getCellIdxsVersionsByRowIdAndMinVersion")
					.setParameter("rowId", rowId).setParameter("minVersion",
							minVersion).list();
		}

	}

	/**
	 * The Class PPodGroupDAOHibernate.
	 */
	public static class PPodGroupDAOHibernate extends
			GenericHibernateDAO<PPodGroup, Long> implements IPPodGroupDAO {}

	/**
	 * The Class PPodRoleDAOHibernate.
	 */
	public static class PPodRoleDAOHibernate extends
			GenericHibernateDAO<Role, Long> implements IPPodRoleDAO {

		/**
		 * Gets the by name.
		 * 
		 * @param name the name
		 * @return the by name
		 */
		public Role getByName(String name) {
			return (Role) getSession().createQuery(
					"select role from Role role where role.name=:name")
					.setParameter("name", name).uniqueResult();
		}
	}

	/**
	 * A Hibernate {@link ITreeDAO}.
	 */
	public static class TreeDAOHibernate extends
			GenericHibernateDAO<Tree, Long> implements ITreeDAO {

		/**
		 * Gets the by p pod id.
		 * 
		 * @param pPodId the pod id
		 * @return the by p pod id
		 */
		public Tree getByPPodId(String pPodId) {
			return (Tree) getSession().getNamedQuery(
					Tree.class.getSimpleName() + "-getByPPodId").setParameter(
					"pPodId", pPodId).uniqueResult();
		}
	}

	/** A default {@code TreeSet} Hibernate DAO. */
	public static class TreeSetDAOHibernate extends
			GenericHibernateDAO<TreeSet, Long> implements ITreeSetDAO {

		/**
		 * Gets the by p pod id.
		 * 
		 * @param pPodId the pod id
		 * @return the by p pod id
		 */
		public TreeSet getByPPodId(final String pPodId) {
			return (TreeSet) getSession().getNamedQuery(
					TreeSet.class.getSimpleName() + "-getByPPodId")
					.setParameter("pPodId", pPodId).uniqueResult();
		}
	}

	/** The study dao provider. */
	private final Provider<StudyDAOHibernate> studyDAOProvider;

	/**
	 * Gets the study dao.
	 * 
	 * @return the study dao
	 */
	public IStudyDAOHibernate getStudyDAO() {
		return (StudyDAOHibernate) studyDAOProvider.get().setSession(session);
	}

	/** The char state matrix provider. */
	private final Provider<CharacterStateMatrixDAOHibernate> charStateMatrixProvider;

	/**
	 * Gets the character state matrix dao.
	 * 
	 * @return the character state matrix dao
	 */
	public ICharacterStateMatrixDAO getCharacterStateMatrixDAO() {
		return (ICharacterStateMatrixDAO) charStateMatrixProvider.get()
				.setSession(session);
	}

	/**
	 * Gets the oTUDAO.
	 * 
	 * @return the oTUDAO
	 */
	public IOTUDAO getOTUDAO() {
		return (IOTUDAO) new OTUDAOHibernate().setSession(session);
	}

	/**
	 * Gets the oTU set dao.
	 * 
	 * @return the oTU set dao
	 */
	public IOTUSetDAO getOTUSetDAO() {
		return (IOTUSetDAO) new OTUSetDAOHibernate().setSession(session);
	}

	/**
	 * Gets the character dao.
	 * 
	 * @return the character dao
	 */
	public ICharacterDAO getCharacterDAO() {
		return (ICharacterDAO) new CharacterDAOHibernate().setSession(session);
	}

	/**
	 * Gets the character state dao.
	 * 
	 * @return the character state dao
	 */
	public ICharacterStateDAO getCharacterStateDAO() {
		return (ICharacterStateDAO) new CharacterStateDAOHibernate()
				.setSession(session);
	}

	/**
	 * Gets the p pod version info dao.
	 * 
	 * @return the p pod version info dao
	 */
	public IPPodVersionInfoDAO getPPodVersionInfoDAO() {
		return (IPPodVersionInfoDAO) new PPodVersionInfoDAOHibernate()
				.setSession(session);
	}

	/**
	 * Gets the character state row dao.
	 * 
	 * @return the character state row dao
	 */
	public ICharacterStateRowDAO getCharacterStateRowDAO() {
		return (ICharacterStateRowDAO) new CharacterStateRowDAOHibernate()
				.setSession(session);
	}

	/**
	 * Gets the tree dao.
	 * 
	 * @return the tree dao
	 */
	public ITreeDAO getTreeDAO() {
		return (ITreeDAO) new TreeDAOHibernate().setSession(session);
	}

	/**
	 * Gets the tree set dao.
	 * 
	 * @return the tree set dao
	 */
	public ITreeSetDAO getTreeSetDAO() {
		return (ITreeSetDAO) new TreeSetDAOHibernate().setSession(session);
	}

	/**
	 * Gets the p pod user dao.
	 * 
	 * @return the p pod user dao
	 */
	public IUserDAO getPPodUserDAO() {
		return (IUserDAO) new UserDAOHibernate().setSession(session);
	}

	/**
	 * Gets the p pod group dao.
	 * 
	 * @return the p pod group dao
	 */
	public IPPodGroupDAO getPPodGroupDAO() {
		return (IPPodGroupDAO) new PPodGroupDAOHibernate().setSession(session);
	}

	/**
	 * Gets the p pod role dao.
	 * 
	 * @return the p pod role dao
	 */
	public IPPodRoleDAO getPPodRoleDAO() {
		return (IPPodRoleDAO) new PPodRoleDAOHibernate().setSession(session);
	}
}
