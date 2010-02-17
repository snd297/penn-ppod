package edu.upenn.cis.ppod.model;

import java.util.Date;

import org.hibernate.Session;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.dao.hibernate.PPodVersionInfoDAOHibernate;
import edu.upenn.cis.ppod.thirdparty.util.HibernateUtil;

/**
 * @author Sam Donnelly
 * 
 */
public class LazyPPodVersionInfo {

	public static interface IFactory {
		LazyPPodVersionInfo create(Session s);
	}

	private final PPodVersionInfo newPPodVersionInfo;
	private final PPodVersionInfoDAOHibernate pPodVersionInfoDAO;

	@Inject
	LazyPPodVersionInfo(final PPodVersionInfoDAOHibernate pPodVersionInfoDAO,
			final PPodVersionInfo newPPodVersionInfo) {
		this.newPPodVersionInfo = newPPodVersionInfo;
		this.pPodVersionInfoDAO = pPodVersionInfoDAO;
	}

	private boolean pPodVersionInfoInitialized = false;

	private void initializePPodVersionInfo() {
		if (pPodVersionInfoInitialized) {

		} else {
			pPodVersionInfoDAO.setSession(HibernateUtil.getSessionFactory()
					.getCurrentSession());
			final Long newPPodVersion = pPodVersionInfoDAO.getMaxPPodVersion() + 1;
			newPPodVersionInfo.setPPodVersion(newPPodVersion);
			newPPodVersionInfo.setCreated(new Date());
			pPodVersionInfoDAO.saveOrUpdate(newPPodVersionInfo);
			pPodVersionInfoInitialized = true;
		}
	}

	public PPodVersionInfo getNewPPodVersionInfo() {
		initializePPodVersionInfo();
		return newPPodVersionInfo;
	}
}
