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
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.model.OTUSet;

@XmlJavaTypeAdapter(OTUSet.Adapter.class)
public interface IOTUSet
		extends ILabeled, IUUPPodEntity, IOrderedChild<IStudy> {

	/**
	 * Add a DNA matrix to this OTU set.
	 * <p>
	 * Handles the {@code IDNAMatrix->IOTUSet} side of the relationship.
	 * 
	 * @param matrix to be added
	 * 
	 * @throws IllegalArgumentException if this otu set already contains the
	 *             matrix
	 */
	void addDNAMatrix(IDNAMatrix matrix);

	/**
	 * Add an {@code IDNASequenceSet}.
	 * <p>
	 * Also handles the {@code IDNASequenceSet->IOTUSet} side of the
	 * relationship.
	 * 
	 * @param dnaSequenceSet the new {@code DNASequenceSet}
	 */
	void addDNASequenceSet(IDNASequenceSet sequenceSet);

	/**
	 * Scaffolding code that does two things:
	 * <ol>
	 * <li>Adds <code>otu</code> to this {@code IOTUSet}'s constituent
	 * {@code IOTU}s</li>
	 * <li>Adds this {@code IOTUSet} to {@code otu}'s {@code IOTUSet}s</li>
	 * </ol>
	 * So it takes care of both sides of the <code>IOTUSet</code><->
	 * <code>IOTU</code> relationship.
	 * <p>
	 * {@code otu} must have a label that is unique relative to this OTU set.
	 * 
	 * @throws IllegalArgumentException if this OTU set already has an OTU with
	 *             {@code otu}'s label
	 * 
	 * @param otu see description
	 */
	void addOTU(IOTU otu);

	/**
	 * Add {@code matrix} to this {@code OTUSet}.
	 * <p>
	 * Also handles the {@code IStandardMatrix->IOTUSet} side of the
	 * relationship.
	 * 
	 * @param matrix matrix we're adding
	 */
	void addStandardMatrix(IStandardMatrix matrix);

	/**
	 * Add a tree set to this OTU set.
	 * <p>
	 * Also handles the {@code ITreeSet->IOTUSet} side of the relationship.
	 * 
	 * @param treeSet to be added
	 */
	void addTreeSet(ITreeSet treeSet);

	/**
	 * Get the description.
	 * 
	 * @return the description
	 */
	@CheckForNull
	String getDescription();

	Set<IDNAMatrix> getDNAMatrices();

	Set<IDNASequenceSet> getDNASequenceSets();

	/**
	 * Getter. {@code null} when the object is created. Once set, it will never
	 * be {@code null}.
	 * 
	 * @return the label
	 */
	@Nullable
	String getLabel();

	/**
	 * Get the {@code IOTU}s that make up this {@code IOTUSet}.
	 * 
	 * @return the {@code IOTU}s that make up this {@code IOTUSet}
	 */
	List<IOTU> getOTUs();

	/**
	 * Get the standard matrices contained in this OTU set.
	 * 
	 * @return the standard matrices contained in this OTU set
	 */
	Set<IStandardMatrix> getStandardMatrices();

	/**
	 * Get the tree sets contained in this OTU set.
	 * 
	 * @return the tree sets contained in this OTU set
	 */
	Set<ITreeSet> getTreeSets();

	/**
	 * Remove {@code matrix} from this OTU set.
	 * 
	 * @param matrix to be removed
	 * 
	 * @return {@code true} if this OTU set contained the specified matrix,
	 *         {@code false} otherwise
	 */
	boolean removeDNAMatrix(IDNAMatrix matrix);

	/**
	 * Remove {@code sequenceSet} from this OTU set.
	 * 
	 * @param sequenceSet to be removed
	 * 
	 * @return {@code true} if this OTU set contained the specified sequence
	 *         set, {@code false} otherwise
	 */
	boolean removeDNASequenceSet(IDNASequenceSet sequenceSet);

	/**
	 * Remove {@code matrix} from this OTU set.
	 * 
	 * @param matrix to be removed
	 * 
	 * @return {@code true} if this OTU set contained the specified matrix,
	 *         {@code false} otherwise
	 */
	boolean removeStandardMatrix(IStandardMatrix matrix);

	/**
	 * Remove {@code treeSet} from this OTU set.
	 * 
	 * @param matrix to be removed
	 * 
	 * @throws IllegalArgumentException if the tree set does not belong to this
	 *             otu set
	 */
	void removeTreeSet(ITreeSet treeSet);

	/**
	 * Setter.
	 * 
	 * @param description the description
	 */
	void setDescription(@CheckForNull String description);

	/**
	 * Set the label of this OTU set
	 * 
	 * @param label the label
	 */
	void setLabel(String label);

	/**
	 * Set this {@code IOTUSet}'s {@code IOTU}s.
	 * <p>
	 * If this method is effectively removing any of this sets's original OTUs,
	 * then the {@code IOTU->IOTUSet} relationship is severed.
	 * 
	 * @param otus the otus to assign to this OTU set
	 * 
	 * @return any {@code OTU}s that were removed as a result of this operation,
	 *         in their original order
	 */
	List<IOTU> setOTUs(final List<? extends IOTU> otus);

}