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
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.util;

import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.AttachmentNamespace;
import edu.upenn.cis.ppod.model.AttachmentType;
import edu.upenn.cis.ppod.model.DNACell;
import edu.upenn.cis.ppod.model.DNAMatrix;
import edu.upenn.cis.ppod.model.DNARow;
import edu.upenn.cis.ppod.model.DNASequence;
import edu.upenn.cis.ppod.model.DNASequenceSet;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.StandardRow;
import edu.upenn.cis.ppod.model.StandardState;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.modelinterfaces.IOTU;
import edu.upenn.cis.ppod.modelinterfaces.IOTUSet;

/**
 * An {@link IVisitor} that does nothing.
 * 
 * @author Sam Donnelly
 */
public abstract class EmptyVisitor implements IVisitor {

	/**
	 * Does nothing.
	 * 
	 * @param attachment ignored
	 */
	public void visitAttachment(final Attachment attachment) {}

	/**
	 * Does nothing.
	 * 
	 * @param attachmentNamespace ignored
	 */
	public void visitAttachmentNamespace(
			final AttachmentNamespace attachmentNamespace) {}

	/**
	 * Does nothing.
	 * 
	 * @param attachmentType ignored
	 */
	public void visitAttachmentType(final AttachmentType attachmentType) {}

	/**
	 * Does nothing.
	 * 
	 * @param cell ignored
	 */
	public void visitDNACell(final DNACell cell) {}

	/**
	 * Does nothing.
	 * 
	 * @param matrix ignored
	 */
	public void visitDNAMatrix(final DNAMatrix matrix) {}

	/**
	 * Does nothing.
	 * 
	 * @param row ignored
	 */
	public void visitDNARow(final DNARow row) {}

	/**
	 * Does nothing.
	 * 
	 * @param sequence ignored
	 */
	public void visitDNASequence(final DNASequence sequence) {}

	/**
	 * Does nothing.
	 * 
	 * @param sequenceSet ignored
	 */
	public void visitDNASequenceSet(final DNASequenceSet sequenceSet) {}

	/**
	 * Does nothing.
	 * 
	 * @param otu ignored
	 */
	public void visitOTU(final IOTU otu) {}

	/**
	 * Does nothing.
	 * 
	 * @param otuSet ignored
	 */
	public void visitOTUSet(final IOTUSet otuSet) {}

	/**
	 * Does nothing.
	 * 
	 * @param cell ignored
	 */
	public void visitStandardCell(final StandardCell cell) {}

	/**
	 * Does nothing.
	 * 
	 * @param character ignored
	 */
	public void visitStandardCharacter(final StandardCharacter character) {}

	/**
	 * Does nothing.
	 * 
	 * @param matrix ignored
	 */
	public void visitStandardMatrix(final StandardMatrix matrix) {}

	/**
	 * Does nothing.
	 * 
	 * @param row ignored
	 */
	public void visitStandardRow(final StandardRow row) {}

	/**
	 * Does nothing.
	 * 
	 * @param state ignored
	 */
	public void visitStandardState(final StandardState state) {}

	/**
	 * Does nothing.
	 * 
	 * @param study ignored
	 */
	public void visitStudy(final Study study) {}

	/**
	 * Does nothing.
	 * 
	 * @param tree ignored
	 */
	public void visitTree(final Tree tree) {}

	/**
	 * Does nothing.
	 * 
	 * @param treeSet ignored
	 */
	public void visitTreeSet(final TreeSet treeSet) {}
}
