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

import static com.google.common.collect.Iterables.get;

/**
 * @author Sam Donnelly
 */
public abstract class MolecularMatrix extends
		CharacterStateMatrix {

	/**
	 * Default constructor.
	 */
	protected MolecularMatrix() {}

	/**
	 * This constructor was created to allow for injected {@code
	 * OTUsToCharacterStateRows} to be passed up the inheritance hierarchy.
	 * 
	 * @param otusToRows the {@code OTUsToCharacterStateRows} for this matrix.
	 */
	protected MolecularMatrix(final OTUsToCharacterStateRows otusToRows) {
		super(otusToRows);
	}

	@Override
	public int getColumnsSize() {
		if (getOTUsToRows().getOTUsToValues().size() > 0) {
			return get(getOTUsToRows().getOTUsToValues().values(), 0)
					.getCellsSize();
		}
		return -1;
	}

}
