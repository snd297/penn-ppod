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

import edu.upenn.cis.ppod.imodel.IHasColumnVersionInfos;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.imodel.IVersioned;
import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.DnaMatrix;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.OtuSet;
import edu.upenn.cis.ppod.model.ProteinMatrix;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.StandardState;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;

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
	public void visitAttachment(final Attachment attachment) {
		setNewVersionInfo(attachment);
	}

	@Override
	public void visitDnaMatrix(final DnaMatrix matrix) {
		setNewVersionInfo(matrix);
	}

	private void visitMatrix(final IHasColumnVersionInfos matrix) {
		setNewVersionInfo(matrix);
		for (int pos = 0; pos < matrix.getColumnVersionInfos().size(); pos++) {
			if (matrix.getColumnVersionInfos().get(pos) == null) {
				matrix.setColumnVersionInfo(pos,
						newVersionInfo.getNewVersionInfo());
			}
		}
	}

	@Override
	public void visitOtu(final Otu otu) {
		setNewVersionInfo(otu);
	}

	@Override
	public void visitOtuSet(final OtuSet otuSet) {
		setNewVersionInfo(otuSet);
	}

	@Override
	public void visitProteinMatrix(final ProteinMatrix matrix) {
		setNewVersionInfo(matrix);
	}

	@Override
	public void visitStandardCharacter(final StandardCharacter character) {
		setNewVersionInfo(character);
	}

	@Override
	public void visitStandardMatrix(final StandardMatrix matrix) {
		visitMatrix(matrix);
	}

	@Override
	public void visitStandardState(final StandardState standardState) {
		setNewVersionInfo(standardState);
	}

	@Override
	public void visitStudy(final Study study) {
		setNewVersionInfo(study);
	}

	@Override
	public void visitTree(final Tree tree) {
		setNewVersionInfo(tree);
	}

	@Override
	public void visitTreeSet(final TreeSet treeSet) {
		setNewVersionInfo(treeSet);
	}
}
