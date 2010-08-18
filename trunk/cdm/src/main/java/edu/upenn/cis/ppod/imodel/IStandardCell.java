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

	void afterUnmarshal();

	/**
	 * Set the type to polymorphic with the appropriate elements equivalent to
	 * {@code stateNumbers}.
	 * <p>
	 * {@code stateNumbers} must contain more than one element.
	 * 
	 * @param elements the elements
	 */
	void setPolymorphicElements(Set<Integer> stateNumbers);

	/**
	 * Set this cell to {@link Type.SINGLE} and its elements to contain this
	 * cell's character's state with the given state number.
	 * 
	 * @param stateNumber the state number of the state for this cell
	 */
	void setSingleElement(Integer stateNumber);

	/**
	 * Set the type to uncertain and this cell's elements to the values
	 * equivalent to {@code elements}.
	 * <p>
	 * {@code elements.size()} must be greater than 2.
	 * 
	 * @param elements the elements
	 */
	void setUncertainElements(Set<Integer> stateNumbers);
}