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
import edu.upenn.cis.ppod.dao.ICharacterStateRowDAO;
import edu.upenn.cis.ppod.dao.IDAOFactory;
import edu.upenn.cis.ppod.dao.IOTUDAO;
import edu.upenn.cis.ppod.dao.IOTUSetDAO;
import edu.upenn.cis.ppod.dao.IPPodRoleDAO;
import edu.upenn.cis.ppod.dao.IStandardMatrixDAO;
import edu.upenn.cis.ppod.dao.ITreeDAO;
import edu.upenn.cis.ppod.dao.ITreeSetDAO;
import edu.upenn.cis.ppod.dao.IUserDAO;
import edu.upenn.cis.ppod.dao.IVersionInfoDAO;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardRow;
import edu.upenn.cis.ppod.model.StandardState;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.model.security.Role;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;

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
		 * 
		 * @return a {@link HibernateDAOFactory}
		 */
		HibernateDAOFactory create(Session session);
	}

	private final Session session;

	@Inject
	public HibernateDAOFactory(
			final Provider<StudyDAOHibernate> studyDAOProvider,
			final Provider<StandardMatrixDAOHibernate> charStateMatrixDAOProvider,
			@Assisted @Nullable final Session session) {
		this.studyDAOProvider = studyDAOProvider;
		this.charStateMatrixProvider = charStateMatrixDAOProvider;
		this.session = session;
	}

	/**
	 * An {@link IOTUDAO} Hibernate DAO.
	 */
	public static class OTUDAOHibernate
			extends GenericHibernateDAO<OTU, Long>
			implements IOTUDAO {}

	/**
	 * An {@link OTUSet} Hibernate DAO.
	 */
	public static class OTUSetDAOHibernate extends
			GenericHibernateDAO<OTUSet, Long> implements IOTUSetDAOHibernate {

		public OTUSet getOTUSetByPPodId(final String pPodId) {
			return (OTUSet) getSession().getNamedQuery(
					OTUSet.class.getSimpleName() + "-getByPPodId")
					.setParameter("pPodId", pPodId).uniqueResult();
		}

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
			GenericHibernateDAO<StandardCharacter, Long> implements
			ICharacterDAO {

		public StandardCharacter getCharacterByPPodId(String pPodId) {
			if (pPodId == null) {
				return null;
			}
			return (StandardCharacter) getSession().getNamedQuery(
					StandardCharacter.class.getSimpleName() + "-getByPPodId")
					.setParameter("pPodId", pPodId).uniqueResult();
		}
	}

	/**
	 * A default {@link CharacterState} Hibernate DAO.
	 */
	public static class CharacterStateDAOHibernate extends
			GenericHibernateDAO<StandardState, Long> implements
			ICharacterStateDAO {}

	/**
	 * A default {@link CharacterStateRow} Hibernate DAO.
	 */
	public static class CharacterStateRowDAOHibernate extends
			GenericHibernateDAO<StandardRow, Long> implements
			ICharacterStateRowDAO {

		@SuppressWarnings("unchecked")
		public List<Object[]> getCellIdxsVersionsByRowIdAndMinVersion(
				Long rowId, Long minVersion) {
			return (List<Object[]>) getSession().getNamedQuery(
					StandardRow.class.getSimpleName()
							+ "-getCellIdxsVersionsByRowIdAndMinVersion")
					.setParameter("rowId", rowId).setParameter("minVersion",
							minVersion).list();
		}

	}

	/**
	 * The Class PPodRoleDAOHibernate.
	 */
	public static class PPodRoleDAOHibernate extends
			GenericHibernateDAO<Role, Long> implements IPPodRoleDAO {

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
			GenericHibernateDAO<TreeSet, Long> implements ITreeSetDAO {}

	private final Provider<StudyDAOHibernate> studyDAOProvider;

	public IStudyDAOHibernate getStudyDAO() {
		return (StudyDAOHibernate) studyDAOProvider.get().setSession(session);
	}

	private final Provider<StandardMatrixDAOHibernate> charStateMatrixProvider;

	public IStandardMatrixDAO getCharacterStateMatrixDAO() {
		return (IStandardMatrixDAO) charStateMatrixProvider.get()
				.setSession(session);
	}

	public IOTUDAO getOTUDAO() {
		return (IOTUDAO) new OTUDAOHibernate().setSession(session);
	}

	public IOTUSetDAO getOTUSetDAO() {
		return (IOTUSetDAO) new OTUSetDAOHibernate().setSession(session);
	}

	public ICharacterDAO getCharacterDAO() {
		return (ICharacterDAO) new CharacterDAOHibernate().setSession(session);
	}

	public ICharacterStateDAO getCharacterStateDAO() {
		return (ICharacterStateDAO) new CharacterStateDAOHibernate()
				.setSession(session);
	}

	public IVersionInfoDAO getVersionInfoDAO() {
		return (IVersionInfoDAO) new VersionInfoDAOHibernate()
				.setSession(session);
	}

	public ICharacterStateRowDAO getCharacterStateRowDAO() {
		return (ICharacterStateRowDAO) new CharacterStateRowDAOHibernate()
				.setSession(session);
	}

	public ITreeDAO getTreeDAO() {
		return (ITreeDAO) new TreeDAOHibernate().setSession(session);
	}

	public ITreeSetDAO getTreeSetDAO() {
		return (ITreeSetDAO) new TreeSetDAOHibernate().setSession(session);
	}

	public IUserDAO getPPodUserDAO() {
		return (IUserDAO) new UserDAOHibernate().setSession(session);
	}

	public IPPodRoleDAO getPPodRoleDAO() {
		return (IPPodRoleDAO) new PPodRoleDAOHibernate().setSession(session);
	}
}
