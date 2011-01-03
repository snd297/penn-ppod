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

import edu.upenn.cis.ppod.imodel.IAttachment;
import edu.upenn.cis.ppod.imodel.IAttachmentNamespace;
import edu.upenn.cis.ppod.imodel.IAttachmentType;
import edu.upenn.cis.ppod.imodel.IDNACell;
import edu.upenn.cis.ppod.imodel.IDNAMatrix;
import edu.upenn.cis.ppod.imodel.IDNARow;
import edu.upenn.cis.ppod.imodel.IDNASequence;
import edu.upenn.cis.ppod.imodel.IDNASequenceSet;
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
	public void visitAttachment(final IAttachment attachment) {}

	/**
	 * Does nothing.
	 * 
	 * @param attachmentNamespace ignored
	 */
	public void visitAttachmentNamespace(
			final IAttachmentNamespace attachmentNamespace) {}

	/**
	 * Does nothing.
	 * 
	 * @param attachmentType ignored
	 */
	public void visitAttachmentType(final IAttachmentType attachmentType) {}

	/**
	 * Does nothing.
	 * 
	 * @param cell ignored
	 */
	public void visitDNACell(final IDNACell cell) {}

	/**
	 * Does nothing.
	 * 
	 * @param matrix ignored
	 */
	public void visitDNAMatrix(final IDNAMatrix matrix) {}

	/**
	 * Does nothing.
	 * 
	 * @param row ignored
	 */
	public void visitDNARow(final IDNARow row) {}

	/**
	 * Does nothing.
	 * 
	 * @param sequence ignored
	 */
	public void visitDNASequence(final IDNASequence sequence) {}

	/**
	 * Does nothing.
	 * 
	 * @param sequenceSet ignored
	 */
	public void visitDNASequenceSet(final IDNASequenceSet sequenceSet) {}

	/**
	 * Does nothing.
	 * 
	 * @param otu ignored
	 */
	public void visitOTU(final IOtu otu) {}

	/**
	 * Does nothing.
	 * 
	 * @param otuSet ignored
	 */
	public void visitOTUSet(final IOtuSet otuSet) {}

	/**
	 * Does nothing.
	 * 
	 * @param proteinRow ignored
	 */
	public void visitProteinRow(final IProteinRow row) {}

	/**
	 * Does nothing.
	 * 
	 * @param cell ignored
	 */
	public void visitStandardCell(final IStandardCell cell) {}

	/**
	 * Does nothing.
	 * 
	 * @param character ignored
	 */
	public void visitStandardCharacter(final IStandardCharacter character) {}

	/**
	 * Does nothing.
	 * 
	 * @param matrix ignored
	 */
	public void visitStandardMatrix(final IStandardMatrix matrix) {}

	/**
	 * Does nothing.
	 * 
	 * @param row ignored
	 */
	public void visitStandardRow(final IStandardRow row) {}

	/**
	 * Does nothing.
	 * 
	 * @param state ignored
	 */
	public void visitStandardState(final IStandardState state) {}

	/**
	 * Does nothing.
	 * 
	 * @param study ignored
	 */
	public void visitStudy(final IStudy study) {}

	/**
	 * Does nothing.
	 * 
	 * @param tree ignored
	 */
	public void visitTree(final ITree tree) {}

	/**
	 * Does nothing.
	 * 
	 * @param treeSet ignored
	 */
	public void visitTreeSet(final ITreeSet treeSet) {}
}
