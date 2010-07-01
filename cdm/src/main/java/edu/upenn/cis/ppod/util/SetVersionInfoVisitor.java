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
import edu.upenn.cis.ppod.model.DNACell;
import edu.upenn.cis.ppod.model.DNAMatrix;
import edu.upenn.cis.ppod.model.DNARow;
import edu.upenn.cis.ppod.model.Matrix;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.StandardRow;
import edu.upenn.cis.ppod.model.StandardState;
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
		if (versioned.isInNeedOfNewVersion()) {
			versioned.setVersionInfo(newVersionInfo
					.getNewVersionInfo());
		}
	}

	@Override
	public void visit(final Attachment attachment) {
		setNewVersionInfo(attachment);
	}

	@Override
	public void visit(final DNACell cell) {
		setNewVersionInfo(cell);
	}

	@Override
	public void visit(final DNAMatrix matrix) {
		visitMatrix(matrix);
	}

	@Override
	public void visit(final DNARow row) {
		setNewVersionInfo(row);
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
	public void visit(final StandardCell cell) {
		setNewVersionInfo(cell);
	}

	@Override
	public void visit(final StandardCharacter standardCharacter) {
		setNewVersionInfo(standardCharacter);
	}

	@Override
	public void visit(final StandardMatrix matrix) {
		visitMatrix(matrix);
	}

	@Override
	public void visit(final StandardRow row) {
		setNewVersionInfo(row);
	}

	@Override
	public void visit(final StandardState standardState) {
		setNewVersionInfo(standardState);
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
		for (int pos = 0; pos < matrix.getColumnVersionInfos().size(); pos++) {
			if (matrix.getColumnVersionInfos().get(pos) == null) {
				matrix.setColumnVersionInfo(pos,
						newVersionInfo.getNewVersionInfo());
			}
		}
	}
}
