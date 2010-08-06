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

import edu.upenn.cis.ppod.model.DNASequenceSet;
import edu.upenn.cis.ppod.model.IDNAMatrix;
import edu.upenn.cis.ppod.model.StandardMatrix;

/**
 * For straightening up or filling data structures after we've unmarshalled.
 * This can be necessary because {@code @XmlIDRef}'s are not resolved until
 * after the unmarshaller callbacks are called.
 * 
 * @author Sam Donnelly
 */
class AfterUnmarshalVisitor
		extends EmptyVisitor
		implements IAfterUnmarshalVisitor {

	@Override
	public void visitDNAMatrix(final IDNAMatrix matrix) {
		matrix.afterUnmarshal();
	}

	@Override
	public void visitDNASequenceSet(final DNASequenceSet sequenceSet) {
		sequenceSet.afterUnmarshal();
	}

	@Override
	public void visitStandardMatrix(final StandardMatrix matrix) {
		matrix.afterUnmarshal();
	}
}
