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

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardState;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.StandardRow;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.modelinterfaces.INewVersionInfo;

/**
 * @author Sam Donnelly
 */
@ImplementedBy(SetVersionInfoVisitor.class)
public interface ISetVersionInfoVisitor extends IVisitor {

	void visit(final Attachment attachment);

	void visit(final StandardCharacter standardCharacter);

	void visit(final StandardState standardState);

	void visit(final StandardCell cell);

	void visit(final StandardMatrix matrix);

	void visit(final StandardRow row);

	void visit(final OTU otu);

	void visit(final OTUSet otuSet);

	void visit(final Study study);

	void visit(final TreeSet treeSet);

	void visit(final Tree tree);

	/**
	 * Create a {@code SetPPodVersionInfoVisitor} with the given {@code
	 * INewPPodVersionInfo}.
	 */
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
		ISetVersionInfoVisitor create(INewVersionInfo newVersionInfo);
	}

}