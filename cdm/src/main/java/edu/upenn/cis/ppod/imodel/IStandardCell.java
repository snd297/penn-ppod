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

@XmlJavaTypeAdapter(StandardCell.Adapter.class)
public interface IStandardCell extends ICell<IStandardState, IStandardRow> {

	/**
	 * Take actions that must be performed after
	 * {@link javax.xml.bind.annotation.XmlIDREF}s have been resolved.
	 */
	void afterUnmarshal();

	/**
	 * Set the cell's type to {@link Type.POLYMORPHIC} and its elements to
	 * contain only {@code elements}
	 * 
	 * @param elements the elements for this cell to contain
	 */
	void setPolymorphicWithStateNos(Set<Integer> stateNumbers);

	void setUncertainWithStateNos(Set<Integer> stateNumbers);

	/**
	 * Set the cell's type to {@link Type.SINGLE} and its states to contain only
	 * {@code getParent().getParent().getCharacters().get(getPosition()).getState(stateNumber)}
	 * .
	 * 
	 * @param element the element for this cell to contain
	 */
	void setSingleWithStateNo(Integer stateNumber);
}