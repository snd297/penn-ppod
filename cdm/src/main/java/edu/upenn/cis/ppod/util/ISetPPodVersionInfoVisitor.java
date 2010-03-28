package edu.upenn.cis.ppod.util;

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.Character;
import edu.upenn.cis.ppod.model.CharacterState;
import edu.upenn.cis.ppod.model.CharacterStateCell;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.CharacterStateRow;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;

/**
 * @author Sam Donnelly
 */
public interface ISetPPodVersionInfoVisitor extends IVisitor {

	void visit(final Attachment attachment);

	void visit(final Character character);

	void visit(final CharacterState characterState);

	void visit(final CharacterStateCell cell);

	void visit(final CharacterStateMatrix matrix);

	void visit(final CharacterStateRow row);

	void visit(final OTU otu);

	void visit(final OTUSet otuSet);

	void visit(final Study study);

	void visit(final TreeSet treeSet);

	void visit(final Tree tree);

	/**
	 * Create a {@code SetPPodVersionInfoVisitor} with the given {@code
	 * INewPPodVersionInfo}.
	 */
	@ImplementedBy(SetPPodVersionInfoVisitor.Factory.class)
	static interface IFactory {

		/**
		 * Create a {@code SetPPodVersionInfoVisitor} with the given {@code
		 * INewPPodVersionInfo}.
		 * 
		 * @param newPPodVersionInfo to be assigned to objects that are
		 *            {@link PPodEntity#isInNeedOfNewPPodVersionInfo()}
		 * 
		 * @return the new {@code SetPPodVersionInfoVisitor}
		 */
		ISetPPodVersionInfoVisitor create(INewPPodVersionInfo newPPodVersionInfo);
	}

}