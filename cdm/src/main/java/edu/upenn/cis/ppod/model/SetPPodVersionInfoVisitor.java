package edu.upenn.cis.ppod.model;

import java.util.Date;

import org.hibernate.Session;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IPPodVersionInfoDAO;
import edu.upenn.cis.ppod.dao.hibernate.PPodVersionInfoDAOHibernate;
import edu.upenn.cis.ppod.util.EmptyVisitor;

/**
 * Stuff that should be done at the very end of a pPOD session.
 * 
 * @author Sam Donnelly
 */
public class SetPPodVersionInfoVisitor extends EmptyVisitor {

	public static interface IFactory {
		SetPPodVersionInfoVisitor create(Session s);
	}

	private final PPodVersionInfo newPPodVersionInfo;
	private final IPPodVersionInfoDAO pPodVersionInfoDAO;

	static int counter = 0;

	@Inject
	SetPPodVersionInfoVisitor(
			final PPodVersionInfoDAOHibernate pPodVersionInfoDAO,
			final PPodVersionInfo newPPodVersionInfo,
			@Assisted final Session session) {
		this.newPPodVersionInfo = newPPodVersionInfo;
		this.pPodVersionInfoDAO = (IPPodVersionInfoDAO) pPodVersionInfoDAO
				.setSession(session);
		counter++;
	}

	private boolean pPodVersionInfoInitialized = false;

	private void initializePPodVersionInfo() {
		if (pPodVersionInfoInitialized) {

		} else {
			final Long newPPodVersion = pPodVersionInfoDAO.getMaxPPodVersion() + 1;
			newPPodVersionInfo.setPPodVersion(newPPodVersion);
			newPPodVersionInfo.setCreated(new Date());
			pPodVersionInfoDAO.saveOrUpdate(newPPodVersionInfo);
			pPodVersionInfoInitialized = true;
		}
	}

	private void setNewPPodVersionIfNeeded(final IPPodVersioned pPodVersioned) {
		if (pPodVersioned.isInNeedOfNewPPodVersionInfo()) {
			initializePPodVersionInfo();
			pPodVersioned.setpPodVersionInfo(newPPodVersionInfo);
			pPodVersioned.unsetInNeedOfNewPPodVersionInfo();
		}
	}

	/**
	 * Does nothing.
	 * 
	 * @param attachment ignored
	 */
	@Override
	public void visit(final Attachment attachment) {
		setNewPPodVersionIfNeeded(attachment);
	}

	/**
	 * Does nothing.
	 * 
	 * @param character ignored
	 */
	@Override
	public void visit(final Character character) {
		setNewPPodVersionIfNeeded(character);
	}

	/**
	 * Does nothing.
	 * 
	 * @param characterState ignored
	 */
	@Override
	public void visit(final CharacterState characterState) {
		setNewPPodVersionIfNeeded(characterState);
	}

	/**
	 * Does nothing.
	 * 
	 * @param cell ignored
	 */
	@Override
	public void visit(final CharacterStateCell cell) {
		setNewPPodVersionIfNeeded(cell);
	}

	/**
	 * Does nothing.
	 * 
	 * @param matrix ignored
	 */
	@Override
	public void visit(final CharacterStateMatrix matrix) {
		setNewPPodVersionIfNeeded(matrix);
	}

	/**
	 * Does nothing.
	 * 
	 * @param row ignored
	 */
	@Override
	public void visit(final CharacterStateRow row) {
		setNewPPodVersionIfNeeded(row);
	}

	/**
	 * Does nothing.
	 * 
	 * @param otu ignored
	 */
	@Override
	public void visit(final OTU otu) {
		setNewPPodVersionIfNeeded(otu);
	}

	/**
	 * Does nothing.
	 * 
	 * @param otuSet ignored
	 */
	@Override
	public void visit(final OTUSet otuSet) {
		setNewPPodVersionIfNeeded(otuSet);
	}

	/**
	 * Does nothing.
	 * 
	 * @param study ignored
	 */
	@Override
	public void visit(final Study study) {
		setNewPPodVersionIfNeeded(study);
	}

	/**
	 * Does nothing.
	 * 
	 * @param treeSet ignored
	 */
	@Override
	public void visit(final TreeSet treeSet) {
		setNewPPodVersionIfNeeded(treeSet);
	}

	@Override
	public void visit(final Tree tree) {
		setNewPPodVersionIfNeeded(tree);
	}
}
