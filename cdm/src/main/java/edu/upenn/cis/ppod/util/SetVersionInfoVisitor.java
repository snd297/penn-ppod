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

import edu.upenn.cis.ppod.imodel.IAttachment;
import edu.upenn.cis.ppod.imodel.IDNACell;
import edu.upenn.cis.ppod.imodel.IDNAMatrix;
import edu.upenn.cis.ppod.imodel.IDNARow;
import edu.upenn.cis.ppod.imodel.IMatrix;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.imodel.IOtu;
import edu.upenn.cis.ppod.imodel.IOtuSetChangeCase;
import edu.upenn.cis.ppod.imodel.IStandardCell;
import edu.upenn.cis.ppod.imodel.IStandardCharacter;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStandardRow;
import edu.upenn.cis.ppod.imodel.IStandardState;
import edu.upenn.cis.ppod.imodel.IStudy;
import edu.upenn.cis.ppod.imodel.ITree;
import edu.upenn.cis.ppod.imodel.ITreeSet;
import edu.upenn.cis.ppod.imodel.IVersioned;

/**
 * Stuff that should be done at the very end of a pPOD session.
 * 
 * @author Sam Donnelly
 */
public final class SetVersionInfoVisitor extends EmptyVisitor {

	private final INewVersionInfo newVersionInfo;

	public SetVersionInfoVisitor(
			final INewVersionInfo newVersionInfo) {
		this.newVersionInfo = newVersionInfo;
	}

	private void setNewVersionInfo(final IVersioned versioned) {
		if (versioned.isInNeedOfNewVersion()) {
			versioned.setVersionInfo(newVersionInfo
					.getNewVersionInfo());
		}
	}

	@Override
	public void visitAttachment(final IAttachment attachment) {
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
	public void visitOTU(final IOtu otu) {
		setNewVersionInfo(otu);
	}

	@Override
	public void visitOTUSet(final IOtuSetChangeCase otuSet) {
		setNewVersionInfo(otuSet);
	}

	@Override
	public void visitStandardCell(final IStandardCell cell) {
		setNewVersionInfo(cell);
	}

	@Override
	public void visitStandardCharacter(final IStandardCharacter character) {
		setNewVersionInfo(character);
	}

	@Override
	public void visitStandardMatrix(final IStandardMatrix matrix) {
		visitMatrix(matrix);
	}

	@Override
	public void visitStandardRow(final IStandardRow row) {
		setNewVersionInfo(row);
	}

	@Override
	public void visitStandardState(final IStandardState standardState) {
		setNewVersionInfo(standardState);
	}

	@Override
	public void visitStudy(final IStudy study) {
		setNewVersionInfo(study);
	}

	@Override
	public void visitTree(final ITree tree) {
		setNewVersionInfo(tree);
	}

	@Override
	public void visitTreeSet(final ITreeSet treeSet) {
		setNewVersionInfo(treeSet);
	}

	private void visitMatrix(final IMatrix<?, ?> matrix) {
		setNewVersionInfo(matrix);
		for (int pos = 0; pos < matrix.getColumnVersionInfos().size(); pos++) {
			if (matrix.getColumnVersionInfos().get(pos) == null) {
				matrix.setColumnVersionInfo(pos,
						newVersionInfo.getNewVersionInfo());
			}
		}
	}
}
