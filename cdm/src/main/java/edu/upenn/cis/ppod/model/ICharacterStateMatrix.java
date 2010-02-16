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
package edu.upenn.cis.ppod.model;

import java.util.List;

import javax.xml.bind.annotation.XmlType;

/**
 * @author Sam Donnelly
 * 
 */
public interface ICharacterStateMatrix {

	/**
	 * We use these to figure out what kind of matrix we have after
	 * unmarshalling.
	 */
	@XmlType(name = "CharacterStateMatrixType")
	public static enum Type {
		/** A {@link DNAStateMatrix}. */
		DNA,

		/** An {@link RNAStateMatrix}. */
		RNA,

		/** A standard {@link CharacterStateMatrix}. */
		STANDARD;
	}

	/**
	 * Get an unmodifiable view of the {@code PPodVersionInfo}s for each for the
	 * columns of the matrix.
	 * <p>
	 * This value is {@code equals()} to the max pPOD version info in a column.
	 * 
	 * @return an unmodifiable view of the columns' {@code PPodVersionInfo}s
	 */
	public List<PPodVersionInfo> getColumnPPodVersionInfos();

	public ICharacterStateMatrix setColumnPPodVersionInfo(int pos,
			PPodVersionInfo pPodVersionInfo);

}