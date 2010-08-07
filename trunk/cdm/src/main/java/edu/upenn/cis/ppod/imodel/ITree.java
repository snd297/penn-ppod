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

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.model.Tree;

/**
 * A phylogenetic tree.
 * 
 * @author Sam Donnelly
 */
@XmlJavaTypeAdapter(Tree.Adapter.class)
public interface ITree extends ILabeled, IUUPPodEntity {

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
	 * Get the tree set that owns this tree.
	 * <p>
	 * Will be {@code null} for newly created trees. Will never be {@code null}
	 * for trees in a persistent state.
	 * 
	 * @return the tree set that owns this tree
	 */
	@Nullable
	ITreeSet getParent();

	/**
	 * Set the label of this tree.
	 * 
	 * @param label the label
	 * 
	 * @return this tree
	 */
	ITree setLabel(final String label);

	/**
	 * Setter.
	 * 
	 * @param newick the Newick tree, composed of pPOD id's.
	 * 
	 * @return this {@code Tree}
	 */
	ITree setNewick(final String newick);

	/**
	 * Set the owning {@code TreeSet}.
	 * <p>
	 * Generall there is not need for client code to call this method - it is
	 * intended to be called from places responsible for managing the
	 * {@code Tree<->TreeSet} relationship, for example implements of
	 * {@link #ITreeSet}.
	 * <p>
	 * Use {@code null} to sever the relationship.
	 * 
	 * @param parent the {@code TreeSet} that we're removing
	 * 
	 * @return this
	 */
	ITree setParent(@CheckForNull final ITreeSet parent);

}