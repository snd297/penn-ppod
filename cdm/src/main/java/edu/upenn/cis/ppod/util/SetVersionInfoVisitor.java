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
import edu.upenn.cis.ppod.model.DNAMatrix;
import edu.upenn.cis.ppod.model.Matrix;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.modelinterfaces.INewVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.IVersioned;

/**
 * Stuff that should be done at the very end of a pPOD session.
 * 
 * @author Sam Donnelly
 */
final class SetVersionInfoVisitor extends EmptyVisitor implements
		ISetVersionInfoVisitor {

	private final INewVersionInfo newVersionInfo;

	@Inject
	SetVersionInfoVisitor(
			@Assisted final INewVersionInfo newVersionInfo) {
		this.newVersionInfo = newVersionInfo;
	}

	private void setNewVersionInfo(final IVersioned versioned) {
		if (versioned.isInNeedOfNewVersionInfo()) {
			versioned.setVersionInfo(newVersionInfo
					.getNewVersionInfo());
		}
	}

	@Override
	public void visit(final Attachment attachment) {
		setNewVersionInfo(attachment);
	}

	@Override
	public void visit(final Character character) {
		setNewVersionInfo(character);
	}

	@Override
	public void visit(final CharacterState characterState) {
		setNewVersionInfo(characterState);
	}

	@Override
	public void visit(final CharacterStateCell cell) {
		setNewVersionInfo(cell);
	}

	@Override
	public void visit(final CharacterStateMatrix matrix) {
		visitMatrix(matrix);
	}

	/**
	 * Does nothing.
	 * 
	 * @param row ignored
	 */
	@Override
	public void visit(final CharacterStateRow row) {
		setNewVersionInfo(row);
	}

	@Override
	public void visit(final DNAMatrix matrix) {
		visitMatrix(matrix);
	}

	@Override
	public void visit(final OTU otu) {
		setNewVersionInfo(otu);
	}

	@Override
	public void visit(final OTUSet otuSet) {
		setNewVersionInfo(otuSet);
	}

	@Override
	public void visit(final Study study) {
		setNewVersionInfo(study);
	}

	@Override
	public void visit(final Tree tree) {
		setNewVersionInfo(tree);
	}

	@Override
	public void visit(final TreeSet treeSet) {
		setNewVersionInfo(treeSet);
	}

	public void visitMatrix(final Matrix<?> matrix) {
		setNewVersionInfo(matrix);
		for (int pos = 0; pos < matrix.getColumnVersions().size(); pos++) {
			if (matrix.getColumnVersionInfos().get(pos) == null) {
				matrix.setColumnVersionInfo(pos, newVersionInfo
						.getNewVersionInfo());
			}
		}
	}

}
