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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Sam Donnelly
 */
public final class DNAMatrix extends MolecularMatrix {

	DNAMatrix() {
		setType(Type.DNA);
	}

	public DNACharacter setCharacter(final int characterIdx,
			final Character character) {
		checkNotNull(character);
		checkArgument(character instanceof DNACharacter,
				"character must be a DNACharacer for a DNAMatrix, not a "
						+ character.getClass().getCanonicalName());
		return (DNACharacter) super.setCharacter(characterIdx, character);
	}
}
