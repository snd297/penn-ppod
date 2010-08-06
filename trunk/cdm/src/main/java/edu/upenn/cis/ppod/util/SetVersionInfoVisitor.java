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
import edu.upenn.cis.ppod.model.ITreeSet;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.StandardRow;
import edu.upenn.cis.ppod.model.StandardState;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.modelinterfaces.IDNACell;
import edu.upenn.cis.ppod.modelinterfaces.IDNAMatrix;
import edu.upenn.cis.ppod.modelinterfaces.IDNARow;
import edu.upenn.cis.ppod.modelinterfaces.IMatrix;
import edu.upenn.cis.ppod.modelinterfaces.INewVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.IOTU;
import edu.upenn.cis.ppod.modelinterfaces.IOTUSet;
import edu.upenn.cis.ppod.modelinterfaces.IStudy;
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
	public void visitAttachment(final Attachment attachment) {
		setNewVersionInfo(attachment);
	}

	@Override
	public void visitDNACell(final IDNACell cell) {
		setNewVersionInfo(cell);
	}

	@Override
	public void visitDNAMatrix(final IDNAMatrix matrix) {
		visitMatrix(matrix);
	}

	@Override
	public void visitDNARow(final IDNARow row) {
		setNewVersionInfo(row);
	}

	@Override
	public void visitOTU(final IOTU otu) {
		setNewVersionInfo(otu);
	}

	@Override
	public void visitOTUSet(final IOTUSet otuSet) {
		setNewVersionInfo(otuSet);
	}

	@Override
	public void visitStandardCell(final StandardCell cell) {
		setNewVersionInfo(cell);
	}

	@Override
	public void visitStandardCharacter(final StandardCharacter standardCharacter) {
		setNewVersionInfo(standardCharacter);
	}

	@Override
	public void visitStandardMatrix(final StandardMatrix matrix) {
		visitMatrix(matrix);
	}

	@Override
	public void visitStandardRow(final StandardRow row) {
		setNewVersionInfo(row);
	}

	@Override
	public void visitStandardState(final StandardState standardState) {
		setNewVersionInfo(standardState);
	}

	@Override
	public void visitStudy(final IStudy study) {
		setNewVersionInfo(study);
	}

	@Override
	public void visitTree(final Tree tree) {
		setNewVersionInfo(tree);
	}

	@Override
	public void visitTreeSet(final ITreeSet treeSet) {
		setNewVersionInfo(treeSet);
	}

	private void visitMatrix(final IMatrix<?> matrix) {
		setNewVersionInfo(matrix);
		for (int pos = 0; pos < matrix.getColumnVersionInfos().size(); pos++) {
			if (matrix.getColumnVersionInfos().get(pos) == null) {
				matrix.setColumnVersionInfo(pos,
						newVersionInfo.getNewVersionInfo());
			}
		}
	}
}
