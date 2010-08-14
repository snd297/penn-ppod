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
import edu.upenn.cis.ppod.imodel.IDNACell;
import edu.upenn.cis.ppod.imodel.IDNAMatrix;
import edu.upenn.cis.ppod.imodel.IDNARow;
import edu.upenn.cis.ppod.imodel.IDNASequence;
import edu.upenn.cis.ppod.imodel.IDNASequenceSet;
import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.IStandardCell;
import edu.upenn.cis.ppod.imodel.IStandardCharacter;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStandardRow;
import edu.upenn.cis.ppod.imodel.IStandardState;
import edu.upenn.cis.ppod.imodel.IStudy;
import edu.upenn.cis.ppod.imodel.ITree;
import edu.upenn.cis.ppod.imodel.ITreeSet;
import edu.upenn.cis.ppod.model.IAttachmentNamespace;
import edu.upenn.cis.ppod.model.IAttachmentType;

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
	 * @param attachment visitee
	 */
	void visitAttachment(IAttachment attachment);

	/**
	 * Visit an attachment namespace.
	 * 
	 * @param the attachmentNamespace visitee
	 */
	void visitAttachmentNamespace(IAttachmentNamespace attachemntNamespace);

	/**
	 * Visit an attachment type.
	 * 
	 * @param the attachmentType visitee
	 */
	void visitAttachmentType(IAttachmentType attachmentType);

	/**
	 * Visit a {@code DNACell}.
	 * 
	 * @param cell visitee
	 */
	void visitDNACell(IDNACell cell);

	/**
	 * Visit a {@code IDNAMatrix}
	 * 
	 * @param matrix visitee
	 */
	void visitDNAMatrix(IDNAMatrix matrix);

	void visitDNARow(IDNARow row);

	void visitDNASequence(IDNASequence sequence);

	void visitDNASequenceSet(IDNASequenceSet sequenceSet);

	void visitOTU(IOTU otu);

	void visitOTUSet(IOTUSet otuSet);

	void visitStandardCell(IStandardCell cell);

	/**
	 * Visit a character
	 * 
	 * @param the character visitee
	 */
	void visitStandardCharacter(IStandardCharacter standardCharacter);

	void visitStandardMatrix(IStandardMatrix matrix);

	void visitStandardRow(IStandardRow row);

	/**
	 * Visit the character state.
	 * 
	 * @param state the visitee
	 */
	void visitStandardState(IStandardState state);

	/**
	 * Visit the study
	 * 
	 * @param study visitee
	 */
	void visitStudy(IStudy study);

	void visitTree(ITree tree);

	void visitTreeSet(ITreeSet treeSet);

}
