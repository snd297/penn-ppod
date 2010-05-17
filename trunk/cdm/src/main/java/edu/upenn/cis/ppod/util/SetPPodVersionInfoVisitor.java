/*
 * Copyright (C) 2010 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.util;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

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
import edu.upenn.cis.ppod.modelinterfaces.IPPodVersioned;

/**
 * Stuff that should be done at the very end of a pPOD session.
 * 
 * @author Sam Donnelly
 */
final class SetPPodVersionInfoVisitor extends EmptyVisitor implements
		ISetPPodVersionInfoVisitor {

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
		}
	}

	@Override
	public void visit(final Attachment attachment) {
		setNewPPodVersionIfNeeded(attachment);
	}

	@Override
	public void visit(final Character character) {
		setNewPPodVersionIfNeeded(character);
	}

	@Override
	public void visit(final CharacterState characterState) {
		setNewPPodVersionIfNeeded(characterState);
	}

	@Override
	public void visit(final CharacterStateCell cell) {
		setNewPPodVersionIfNeeded(cell);
	}

	@Override
	public void visit(final CharacterStateMatrix matrix) {
		setNewPPodVersionIfNeeded(matrix);
		for (int pos = 0; pos < matrix.getColumnsSize(); pos++) {
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

	@Override
	public void visit(final Study study) {
		setNewPPodVersionIfNeeded(study);
	}

	@Override
	public void visit(final TreeSet treeSet) {
		setNewPPodVersionIfNeeded(treeSet);
	}

	@Override
	public void visit(final Tree tree) {
		setNewPPodVersionIfNeeded(tree);
	}

}
