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

import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import edu.upenn.cis.ppod.model.StandardCell;

/**
 * A cell in a {@link IStandardMatrix}.
 * 
 * @author Sam Donnelly
 */
@XmlJavaTypeAdapter(StandardCell.Adapter.class)
public interface IStandardCell extends ICell<IStandardState, IStandardRow> {

	/**
	 * Take actions that must be performed after
	 * {@link javax.xml.bind.annotation.XmlIDREF}s have been resolved, that is,
	 * after unmarshalling is finished.
	 */
	void afterUnmarshal();

	/**
	 * Set the cell's type to {@link Type.POLYMORPHIC} and its states to contain
	 * only the states with the given state numbers. The states are pulled from
	 * this column's character, so it is not legal to call this method if this
	 * cell is not part of a matrix with a character in the column.
	 * 
	 * @param stateNumbers the state numbers of the states we want
	 */
	void setPolymorphicWithStateNos(Set<Integer> stateNumbers);

	/**
	 * Set the cell's type to {@link Type.SINGLE} and its states to contain only
	 * the state with the given state number. The state is pulled from this
	 * column's character, so it is not legal to call this method if this cell
	 * is not part of a matrix with a character in the column.
	 * 
	 * @param stateNumber the state number of the state we want
	 */
	void setSingleWithStateNo(Integer stateNumber);

	/**
	 * Set the cell's type to {@link Type.UNCERTAIN} and its states to contain
	 * only the states with the given state numbers. The states are pulled from
	 * this column's character, so it is not legal to call this method if this
	 * cell is not part of a matrix with a character in the column.
	 * 
	 * @param stateNumbers the state numbers of the states we want
	 */
	void setUncertainWithStateNos(Set<Integer> stateNumbers);
}