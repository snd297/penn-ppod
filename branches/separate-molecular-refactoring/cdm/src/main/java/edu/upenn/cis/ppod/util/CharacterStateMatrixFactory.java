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

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.model.Matrix;
import edu.upenn.cis.ppod.model.DNAMatrix;

/**
 * @author Sam Donnelly
 */
class CharacterStateMatrixFactory implements
		ICategoricalMatrixFactory {
	private final Provider<Matrix> standardMatrixProvider;
	private final Provider<DNAMatrix> dnaMatrixProvider;

// private final Provider<RNAStateMatrix> rnaMatrixProvider;

	@Inject
	CharacterStateMatrixFactory(
			final Provider<Matrix> standardMatrixProvider,
			final Provider<DNAMatrix> dnaMatrixProvider) {
		// final Provider<RNAStateMatrix> rnaMatrixProvider) {
		this.standardMatrixProvider = standardMatrixProvider;
		this.dnaMatrixProvider = dnaMatrixProvider;
		// this.rnaMatrixProvider = rnaMatrixProvider;
	}

	public Matrix create(final Matrix matrix) {
		Matrix newMatrix;
		if (matrix.getClass().equals(Matrix.class)) {
			newMatrix = standardMatrixProvider.get();
		} else if (matrix.getClass().equals(DNAMatrix.class)) {
			newMatrix = dnaMatrixProvider.get();
			// } else if (matrix.getClass().equals(RNAStateMatrix.class)) {
			// newMatrix = rnaMatrixProvider.get();
		} else {
			throw new IllegalArgumentException("unsupported matrix type: "
												+ matrix.getClass());
		}
		return newMatrix;
	}

}
