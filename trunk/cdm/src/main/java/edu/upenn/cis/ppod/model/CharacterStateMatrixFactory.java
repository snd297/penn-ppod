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
package edu.upenn.cis.ppod.model;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Sam Donnelly
 */
public class CharacterStateMatrixFactory implements
		ICharacterStateMatrixFactory {
	private final Provider<CharacterStateMatrix> standardMatrixProvider;
	private final Provider<DNAStateMatrix> dnaMatrixProvider;
	private final Provider<RNAStateMatrix> rnaMatrixProvider;

	@Inject
	CharacterStateMatrixFactory(
			final Provider<CharacterStateMatrix> standardMatrixProvider,
			final Provider<DNAStateMatrix> dnaMatrixProvider,
			final Provider<RNAStateMatrix> rnaMatrixProvider) {
		this.standardMatrixProvider = standardMatrixProvider;
		this.dnaMatrixProvider = dnaMatrixProvider;
		this.rnaMatrixProvider = rnaMatrixProvider;
	}

	public CharacterStateMatrix create(final CharacterStateMatrix matrix) {
		CharacterStateMatrix newMatrix;
		if (matrix.getClass().equals(CharacterStateMatrix.class)) {
			newMatrix = standardMatrixProvider.get();
		} else if (matrix.getClass().equals(DNAStateMatrix.class)) {
			newMatrix = dnaMatrixProvider.get();
		} else if (matrix.getClass().equals(RNAStateMatrix.class)) {
			newMatrix = rnaMatrixProvider.get();
		} else {
			throw new IllegalArgumentException("unsupported matrix type: "
					+ matrix.getClass());
		}
		return newMatrix;
	}

}
