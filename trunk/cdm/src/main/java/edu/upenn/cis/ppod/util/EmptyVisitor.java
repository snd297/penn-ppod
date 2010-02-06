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
import edu.upenn.cis.ppod.model.CharacterStateCell;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.CharacterStateRow;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.Study;
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
	 * @param matrix ignored
	 * 
	 * @return this
	 */
	public IVisitor visit(final CharacterStateMatrix matrix) {
		return this;
	}

	/**
	 * Does nothing.
	 * 
	 * @param row ignored
	 * 
	 * @return this
	 */
	public IVisitor visit(final CharacterStateRow row) {
		return this;
	}

	/**
	 * Does nothing.
	 * 
	 * @param otu ignored
	 * 
	 * @return this
	 */
	public IVisitor visit(final OTU otu) {
		return this;
	}

	/**
	 * Does nothing.
	 * 
	 * @param otuSet ignored
	 * 
	 * @return this
	 */
	public IVisitor visit(final OTUSet otuSet) {
		return this;
	}

	/**
	 * Does nothing.
	 * 
	 * @param study ignored
	 * 
	 * @return this
	 */
	public IVisitor visit(final Study study) {
		return this;
	}

	/**
	 * Does nothing.
	 * 
	 * @param treeSet ignored
	 * 
	 * @return this
	 */
	public IVisitor visit(final TreeSet treeSet) {
		return this;
	}

	/**
	 * Does nothing.
	 * 
	 * @param attachment ignored
	 * 
	 * @return this
	 */
	public IVisitor visit(final Attachment attachment) {
		return this;
	}

	/**
	 * Does nothing.
	 * 
	 * @param cell ignored
	 * 
	 * @return this
	 */
	public IVisitor visit(final CharacterStateCell cell) {
		return this;
	}
}
