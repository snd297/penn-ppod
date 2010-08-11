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
	 * Set the type to polymorphic with the appropriate elements equivalent to
	 * {@code elements}.
	 * <p>
	 * {@code elements} must contain more than one element.
	 * <p>
	 * Note that the elements that are actually assigned won't be {@code ==} to
	 * the elements passed in, but will be the ones with the same state numbers
	 * from the owning matrix's characters' states.
	 * 
	 * @param elements the elements
	 */
	void setPolymorphicElements(
			final Set<? extends IStandardState> elements);

	/**
	 * Set this cell to {@link Type.SINGLE} and its elements to contain the
	 * owning matrix's equivalent of {@code element}.
	 * <p>
	 * Note that the element that is actually assigned won't be {@code ==} to
	 * the element passed in, but will be the one with the same state number
	 * from the owning matrix's characters' states.
	 * 
	 * @param element the single element to be contained in this cell
	 */
	void setSingleElement(final IStandardState element);

	void afterUnmarshal();
}