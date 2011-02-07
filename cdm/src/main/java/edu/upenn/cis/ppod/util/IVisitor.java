/*
 * Copyright (C) 2009 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.util;

import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.AttachmentNamespace;
import edu.upenn.cis.ppod.model.AttachmentType;
import edu.upenn.cis.ppod.model.DnaCell;
import edu.upenn.cis.ppod.model.DnaMatrix;
import edu.upenn.cis.ppod.model.DnaRow;
import edu.upenn.cis.ppod.model.DnaSequence;
import edu.upenn.cis.ppod.model.DnaSequenceSet;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.OtuSet;
import edu.upenn.cis.ppod.model.ProteinCell;
import edu.upenn.cis.ppod.model.ProteinMatrix;
import edu.upenn.cis.ppod.model.ProteinRow;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.StandardRow;
import edu.upenn.cis.ppod.model.StandardState;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;

/**
 * The visitor in the visitor pattern.
 * <p>
 * <a href="http://en.wikipedia.org/wiki/Visitor_pattern">wikipedia visitor
 * entry</a>
 * 
 * @author Sam Donnelly
 */
public interface IVisitor {

	/**
	 * Visit an attachment
	 * 
	 * @param attachment to be visited
	 */
	void visitAttachment(Attachment attachment);

	/**
	 * Visit an attachment namespace.
	 * 
	 * @param the attachmentNamespace to be visited
	 */
	void visitAttachmentNamespace(AttachmentNamespace attachemntNamespace);

	/**
	 * Visit an attachment type.
	 * 
	 * @param the attachmentType to be visited
	 */
	void visitAttachmentType(AttachmentType attachmentType);

	/**
	 * Visit a DNA cell
	 * 
	 * @param cell to be visited
	 */
	void visitDnaCell(DnaCell cell);

	/**
	 * Visit a DNA matrix.
	 * 
	 * @param matrix to be visited
	 */
	void visitDnaMatrix(DnaMatrix matrix);

	void visitDnaRow(DnaRow row);

	void visitDnaSequence(DnaSequence sequence);

	void visitDnaSequenceSet(DnaSequenceSet sequenceSet);

	void visitOtu(Otu otu);

	void visitOtuSet(OtuSet otuSet);

	void visitProteinCell(ProteinCell cell);

	void visitProteinMatrix(ProteinMatrix matrix);

	void visitProteinRow(ProteinRow row);

	void visitStandardCell(StandardCell cell);

	/**
	 * Visit a character
	 * 
	 * @param to be visited
	 */
	void visitStandardCharacter(StandardCharacter character);

	void visitStandardMatrix(StandardMatrix matrix);

	/**
	 * Visit the row
	 * 
	 * @param row to be visited
	 */
	void visitStandardRow(StandardRow row);

	/**
	 * Visit the standard state.
	 * 
	 * @param state to be visited
	 */
	void visitStandardState(StandardState state);

	/**
	 * Visit the study.
	 * 
	 * @param study to be visited
	 */
	void visitStudy(Study study);

	/**
	 * Visit the tree
	 * 
	 * @param tree to be visited
	 */
	void visitTree(Tree tree);

	void visitTreeSet(TreeSet treeSet);

}
