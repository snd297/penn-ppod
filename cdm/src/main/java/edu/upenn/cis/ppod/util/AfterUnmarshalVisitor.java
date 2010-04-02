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

import edu.upenn.cis.ppod.model.Character;
import edu.upenn.cis.ppod.model.CharacterStateCell;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.DNASequenceSet;
import edu.upenn.cis.ppod.model.OTUKeyedBimap;

/**
 * For straightening up or filling data structures after we've unmarshalled.
 * 
 * @author Sam Donnelly
 */
class AfterUnmarshalVisitor extends EmptyVisitor implements
		IAfterUnmarshalVisitor {

	/**
	 * Call {@code matrix.afterUnmarshal()}.
	 * 
	 * @param matrix target
	 */
	@Override
	public void visit(final CharacterStateMatrix matrix) {
		matrix.afterUnmarshal();
	}

	/**
	 * Call {@code cell.afterUnmarshal()}.
	 * 
	 * @param cell target
	 */
	@Override
	public void visit(final CharacterStateCell cell) {
		cell.afterUnmarshal();
	}

	@Override
	public void visit(final Character character) {
		character.afterUnmarshal();
	}

	@Override
	public void visit(final OTUKeyedBimap<?, ?> otuKeyedMap) {
		otuKeyedMap.afterUnmarshal();
	}

	@Override
	public void visit(DNASequenceSet dnaSequenceSet) {
		dnaSequenceSet.afterUnmarshal();
	}
}
