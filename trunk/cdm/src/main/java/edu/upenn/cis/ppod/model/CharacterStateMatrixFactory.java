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


/**
 * @author Sam Donnelly
 */
public class CharacterStateMatrixFactory implements
		CharacterStateMatrix.IFactory {

	public CharacterStateMatrix create(final CharacterStateMatrix.Type type) {
		switch (type) {
			case STANDARD:
				return new CharacterStateMatrix();
			case DNA:
				return new DNAStateMatrix();
			case RNA:
				return new RNAStateMatrix();
			default:
				throw new AssertionError("unknown type " + type);
		}
	}
}
