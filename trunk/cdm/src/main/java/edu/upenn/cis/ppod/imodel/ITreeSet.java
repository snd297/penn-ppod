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

import edu.upenn.cis.ppod.model.TreeSet;

@XmlJavaTypeAdapter(TreeSet.Adapter.class)
public interface ITreeSet
		extends ILabeled, IUuPPodEntity, IOTUSetChild, IHasDocId {

	/**
	 * Add {@code tree} to this {@code ITreeSet}.
	 * 
	 * @param tree to be added
	 * 
	 * @throws IllegalArgumentException if the tree is already contained in this
	 *             tree set
	 */
	void addTree(ITree tree);

	/**
	 * Get the constituent trees.
	 * 
	 * @return the constituent trees.
	 */
	List<ITree> getTrees();

	/**
	 * Set the label.
	 * 
	 * @param label the label value
	 */
	void setLabel(String label);

	/**
	 * Set the trees in this tree set.
	 * <p>
	 * This handles both sides of the {@code ITreeSet<->ITree} relationship.
	 * 
	 * @param trees the trees we're setting
	 * 
	 * @return any trees which were removed as a result of this operation
	 */
	List<ITree> setTrees(List<? extends ITree> trees);

}