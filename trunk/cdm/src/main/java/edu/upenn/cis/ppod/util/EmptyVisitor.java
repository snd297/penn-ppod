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
import edu.upenn.cis.ppod.model.Character;
import edu.upenn.cis.ppod.model.CharacterState;
import edu.upenn.cis.ppod.model.CharacterStateCell;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.CharacterStateRow;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;

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
	public void visit(final Attachment attachment) {

	}

	/**
	 * Does nothing.
	 * 
	 * @param attachmentNamespace ignored
	 */
	public void visit(final AttachmentNamespace attachmentNamespace) {

	}

	/**
	 * Does nothing.
	 * 
	 * @param attachmentType ignored
	 */
	public void visit(final AttachmentType attachmentType) {

	}

	/**
	 * Does nothing.
	 * 
	 * @param character ignored
	 */
	public void visit(final Character character) {

	}

	/**
	 * Does nothing.
	 * 
	 * @param characterState ignored
	 */
	public void visit(final CharacterState characterState) {

	}

	/**
	 * Does nothing.
	 * 
	 * @param cell ignored
	 */
	public void visit(final CharacterStateCell cell) {

	}

	/**
	 * Does nothing.
	 * 
	 * @param matrix ignored
	 */
	public void visit(final CharacterStateMatrix matrix) {

	}

	/**
	 * Does nothing.
	 * 
	 * @param row ignored
	 */
	public void visit(final CharacterStateRow row) {

	}

	/**
	 * Does nothing.
	 * 
	 * @param otu ignored
	 */
	public void visit(final OTU otu) {

	}

	/**
	 * Does nothing.
	 * 
	 * @param otuSet ignored
	 */
	public void visit(final OTUSet otuSet) {

	}

	/**
	 * Does nothing.
	 * 
	 * @param study ignored
	 */
	public void visit(final Study study) {

	}

	/**
	 * Does nothing.
	 * 
	 * @param treeSet ignored
	 */
	public void visit(final TreeSet treeSet) {

	}

	/**
	 * Does nothing.
	 * 
	 * @param tree ignored
	 */
	public void visit(final Tree tree) {

	}
}
