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

import edu.upenn.cis.ppod.imodel.IAttachment;
import edu.upenn.cis.ppod.imodel.IAttachmentNamespace;
import edu.upenn.cis.ppod.imodel.IAttachmentType;
import edu.upenn.cis.ppod.imodel.IDnaCell;
import edu.upenn.cis.ppod.imodel.IDnaMatrix;
import edu.upenn.cis.ppod.imodel.IDnaRow;
import edu.upenn.cis.ppod.imodel.IDnaSequence;
import edu.upenn.cis.ppod.imodel.IDnaSequenceSet;
import edu.upenn.cis.ppod.imodel.IOtu;
import edu.upenn.cis.ppod.imodel.IOtuSet;
import edu.upenn.cis.ppod.imodel.IProteinRow;
import edu.upenn.cis.ppod.imodel.IStandardCell;
import edu.upenn.cis.ppod.imodel.IStandardCharacter;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStandardRow;
import edu.upenn.cis.ppod.imodel.IStandardState;
import edu.upenn.cis.ppod.imodel.IStudy;
import edu.upenn.cis.ppod.imodel.ITree;
import edu.upenn.cis.ppod.imodel.ITreeSet;

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
	void visitAttachment(IAttachment attachment);

	/**
	 * Visit an attachment namespace.
	 * 
	 * @param the attachmentNamespace to be visited
	 */
	void visitAttachmentNamespace(IAttachmentNamespace attachemntNamespace);

	/**
	 * Visit an attachment type.
	 * 
	 * @param the attachmentType to be visited
	 */
	void visitAttachmentType(IAttachmentType attachmentType);

	/**
	 * Visit a DNA cell
	 * 
	 * @param cell to be visited
	 */
	void visitDNACell(IDnaCell cell);

	/**
	 * Visit a DNA matrix.
	 * 
	 * @param matrix to be visited
	 */
	void visitDNAMatrix(IDnaMatrix matrix);

	void visitDNARow(IDnaRow row);

	void visitDNASequence(IDnaSequence sequence);

	void visitDNASequenceSet(IDnaSequenceSet sequenceSet);

	void visitOTU(IOtu otu);

	void visitOTUSet(IOtuSet otuSet);

	void visitProteinRow(IProteinRow row);

	void visitStandardCell(IStandardCell cell);

	/**
	 * Visit a character
	 * 
	 * @param to be visited
	 */
	void visitStandardCharacter(IStandardCharacter character);

	void visitStandardMatrix(IStandardMatrix matrix);

	/**
	 * Visit the row
	 * 
	 * @param row to be visited
	 */
	void visitStandardRow(IStandardRow row);

	/**
	 * Visit the standard state.
	 * 
	 * @param state to be visited
	 */
	void visitStandardState(IStandardState state);

	/**
	 * Visit the study.
	 * 
	 * @param study to be visited
	 */
	void visitStudy(IStudy study);

	/**
	 * Visit the tree
	 * 
	 * @param tree to be visited
	 */
	void visitTree(ITree tree);

	void visitTreeSet(ITreeSet treeSet);

}
