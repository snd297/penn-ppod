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

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

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
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.modelinterfaces.IOTU;
import edu.upenn.cis.ppod.modelinterfaces.IOTUSet;
import edu.upenn.cis.ppod.modelinterfaces.IStudy;

/**
 * Made so we can test the various {@code accept(IVisitor)} methods.
 * <p>
 * Collects, in order, all of the objects that are visited.
 * 
 * @author Sam Donnelly
 */
public class TestVisitor extends EmptyVisitor {

	final List<Object> visited = newArrayList();

	TestVisitor() {}

	public List<Object> getVisited() {
		return visited;
	}

	@Override
	public void visitAttachment(final Attachment attachment) {
		visited.add(attachment);
	}

	@Override
	public void visitAttachmentNamespace(
			final AttachmentNamespace attachmentNamespace) {
		visited.add(attachmentNamespace);
	}

	@Override
	public void visitAttachmentType(final AttachmentType attachmentType) {
		visited.add(attachmentType);
	}

	@Override
	public void visitDNACell(final DNACell cell) {
		visited.add(cell);
	}

	@Override
	public void visitDNAMatrix(final DNAMatrix matrix) {
		visited.add(matrix);
	}

	@Override
	public void visitDNARow(final DNARow row) {
		visited.add(row);
	}

	@Override
	public void visitDNASequence(final DNASequence sequence) {
		visited.add(sequence);
	}

	@Override
	public void visitDNASequenceSet(final DNASequenceSet sequenceSet) {
		visited.add(sequenceSet);
	}

	@Override
	public void visitOTU(final IOTU otu) {
		visited.add(otu);
	}

	@Override
	public void visitOTUSet(final IOTUSet otuSet) {
		visited.add(otuSet);
	}

	@Override
	public void visitStandardCell(final StandardCell cell) {
		visited.add(cell);
	}

	@Override
	public void visitStandardCharacter(final StandardCharacter character) {
		visited.add(character);
	}

	@Override
	public void visitStandardMatrix(final StandardMatrix matrix) {
		visited.add(matrix);
	}

	@Override
	public void visitStandardRow(final StandardRow row) {
		visited.add(row);
	}

	@Override
	public void visitStandardState(final StandardState state) {
		visited.add(state);
	}

	@Override
	public void visitStudy(final IStudy study) {
		visited.add(study);
	}

	@Override
	public void visitTree(final Tree tree) {
		visited.add(tree);
	}

	@Override
	public void visitTreeSet(final TreeSet treeSet) {
		visited.add(treeSet);
	}
}
