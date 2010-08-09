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
import edu.upenn.cis.ppod.imodel.IDNASequenceSet;
import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStudy;
import edu.upenn.cis.ppod.imodel.ITree;
import edu.upenn.cis.ppod.imodel.ITreeSet;
import edu.upenn.cis.ppod.model.AttachmentNamespace;
import edu.upenn.cis.ppod.model.AttachmentType;
import edu.upenn.cis.ppod.model.DNASequence;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardRow;
import edu.upenn.cis.ppod.model.StandardState;


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
	void visitAttachmentNamespace(AttachmentNamespace attachemntNamespace);

	/**
	 * Visit an attachment type.
	 * 
	 * @param the attachmentType visitee
	 */
	void visitAttachmentType(AttachmentType attachmentType);

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

	void visitDNASequence(DNASequence sequence);

	void visitDNASequenceSet(IDNASequenceSet sequenceSet);

	void visitOTU(IOTU otu);

	void visitOTUSet(IOTUSet otuSet);

	void visitStandardCell(StandardCell cell);

	/**
	 * Visit a character
	 * 
	 * @param the character visitee
	 */
	void visitStandardCharacter(StandardCharacter standardCharacter);

	void visitStandardMatrix(IStandardMatrix matrix);

	void visitStandardRow(StandardRow row);

	/**
	 * Visit the character state.
	 * 
	 * @param characterState the character state visitee
	 */
	void visitStandardState(StandardState state);

	/**
	 * Visit the study
	 * 
	 * @param study visitee
	 */
	void visitStudy(IStudy study);

	void visitTree(ITree tree);

	void visitTreeSet(ITreeSet treeSet);

}
