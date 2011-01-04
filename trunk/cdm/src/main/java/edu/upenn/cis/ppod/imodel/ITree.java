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

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import edu.upenn.cis.ppod.model.Tree;

/**
 * A phylogenetic tree.
 * <p>
 * The trees are stored in Newick format composed of pPOD IDs of the constituent
 * OTUs.
 * 
 * @author Sam Donnelly
 */
@XmlJavaTypeAdapter(Tree.Adapter.class)
public interface ITree extends ILabeled, IUuPPodEntity, IChild<ITreeSet> {

	/**
	 * Return the label. {@code null} when the tree is constructed, but will
	 * never be {@code null} once set.
	 * 
	 * @return the label
	 */
	@Nullable
	String getLabel();

	/**
	 * Get the newick string. {@code null} when the tree is constructed, but
	 * will never be {@code null} once set.
	 * 
	 * @return the newick string
	 */
	@XmlElement
	@Nullable
	String getNewick();

	/**
	 * Set the label of this tree.
	 * 
	 * @param label the label
	 */
	void setLabel(final String label);

	/**
	 * Setter.
	 * 
	 * @param newick the Newick tree, composed of pPOD id's.
	 * 
	 * @return this {@code Tree}
	 */
	void setNewick(final String newick);

}