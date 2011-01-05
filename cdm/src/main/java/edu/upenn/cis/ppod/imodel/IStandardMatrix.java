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
package edu.upenn.cis.ppod.imodel;

import java.util.List;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.annotations.Beta;

import edu.upenn.cis.ppod.model.StandardMatrix;

@XmlJavaTypeAdapter(StandardMatrix.Adapter.class)
public interface IStandardMatrix
		extends IMatrix<IStandardRow, IStandardCell>, IDependsOnOtus {

	void afterUnmarshal();

	/**
	 * Get the characters contained in this matrix.
	 * 
	 * @return the characters contained in this matrix
	 */
	List<IStandardCharacter> getCharacters();

	/**
	 * Set the characters.
	 * <p>
	 * This method is does not reorder the columns of the matrix because that is
	 * a potentially expensive operation - it could load the entire matrix into
	 * the persistence context.
	 * <p>
	 * This method does reorder {@link #getColumnVersionInfos()}.
	 * <p>
	 * It is legal for two characters to have the same label, but not to be
	 * {@code .equals} to each other.
	 * <p>
	 * This method also takes care of calling {@link #setCol
	 * 
	 * @param characters the new characters
	 * 
	 * @return the characters removed as a result of this operation
	 */
	List<IStandardCharacter> setCharacters(
			final List<? extends IStandardCharacter> characters);

	@Beta
	void addColumn(int columnNo, IStandardCharacter character,
			List<? extends IStandardCell> column);

}