package edu.upenn.cis.ppod.model;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.IPPodVersioned;
import edu.upenn.cis.ppod.util.EmptyVisitor;

/**
 * Stuff that should be done at the very end of a pPOD session.
 * 
 * @author Sam Donnelly
 */
public class SetPPodVersionInfoVisitor extends EmptyVisitor {

	public static interface IFactory {
		SetPPodVersionInfoVisitor create(INewPPodVersionInfo newPPodVersionInfo);
	}

	private final INewPPodVersionInfo newPPodVersionInfo;

	@Inject
	SetPPodVersionInfoVisitor(
			@Assisted final INewPPodVersionInfo newPPodVersionInfo) {
		this.newPPodVersionInfo = newPPodVersionInfo;
	}

	private void setNewPPodVersionIfNeeded(final IPPodVersioned pPodVersioned) {
		if (pPodVersioned.isInNeedOfNewPPodVersionInfo()) {
			pPodVersioned.setPPodVersionInfo(newPPodVersionInfo
					.getNewPPodVersionInfo());
			pPodVersioned.unsetInNeedOfNewPPodVersionInfo();
		}
	}

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
		for (int pos = 0; pos < matrix.getColumnPPodVersionInfos().size(); pos++) {
			if (matrix.getColumnPPodVersionInfos().get(pos) == null) {
				matrix.setColumnPPodVersionInfo(pos, newPPodVersionInfo
						.getNewPPodVersionInfo());
			}
		}
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
