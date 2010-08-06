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
package edu.upenn.cis.ppod.modelinterfaces;

import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import edu.upenn.cis.ppod.model.DNASequenceSet;
import edu.upenn.cis.ppod.model.ITreeSet;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.util.IVisitor;

@XmlJavaTypeAdapter(OTUSet.Adapter.class)
public interface IOTUSet
		extends ILabeled, IWithXmlID, IUUPPodEntity {

	void accept(IVisitor visitor);

	/**
	 * Add a DNA matrix to this OTU set.
	 * 
	 * @param matrix to be added
	 * 
	 * @return {@code matrix}
	 */
	IDNAMatrix addDNAMatrix(IDNAMatrix matrix);

	/**
	 * Add a {@code DNASequenceSet}.
	 * <p>
	 * Also handles the {@code DNASequenceSet->IOTUSet} side of the
	 * relationship.
	 * 
	 * @param dnaSequenceSet the new {@code DNASequenceSet}
	 * 
	 * @return {@code dnaSequenceSet}
	 */
	DNASequenceSet addDNASequenceSet(DNASequenceSet sequenceSet);

	/**
	 * Scaffolding code that does two things:
	 * <ol>
	 * <li>Adds <code>otu</code> to this {@code OTUSet}'s constituent
	 * {@code OTU}s</li>
	 * <li>Adds this {@code OTUSet} to {@code otu}'s {@code OTUSet}s</li>
	 * </ol>
	 * So it takes care of both sides of the <code>OTUSet</code><->
	 * <code>OTU</code> relationship.
	 * <p>
	 * Assumes {@code otu} is in a detached state.
	 * <p>
	 * {@code otu} must have a label that is unique relative to this OTU set.
	 * 
	 * @param otu see description
	 * 
	 * @return {@code otu}
	 * 
	 * @throws IllegalArgumentException if this OTU set already has an OTU with
	 *             {@code otu}'s label
	 */
	IOTU addOTU(IOTU otu);

	/**
	 * Add {@code matrix} to this {@code OTUSet}.
	 * <p>
	 * Also handles the {@code StandardMatrix->OTUSet} side of the relationship.
	 * 
	 * @param matrix matrix we're adding
	 * 
	 * @return {@code matrix}
	 */
	StandardMatrix addStandardMatrix(StandardMatrix matrix);

	/**
	 * Add a tree set to this OTU set.
	 * <p>
	 * Also handles the {@code TreeSet->OTUSet} side of the relationship.
	 * 
	 * @param treeSet to be added
	 * @return {@code treeSet}
	 */
	ITreeSet addTreeSet(ITreeSet treeSet);

	/**
	 * Get the description.
	 * 
	 * @return the description
	 */
	@CheckForNull
	String getDescription();

	Set<IDNAMatrix> getDNAMatrices();

	Set<DNASequenceSet> getDNASequenceSets();

	/**
	 * Getter. {@code null} when the object is created. Once set, it will never
	 * be {@code null}.
	 * 
	 * @return the label
	 */
	@Nullable
	String getLabel();

	/**
	 * Get the {@code IOTU}s that make up this {@code OTUSet}.
	 * 
	 * @return the {@code IOTU}s that make up this {@code OTUSet}
	 */
	List<IOTU> getOTUs();

	/**
	 * Get the study to which this OTU set belongs. Will be {@code null} when
	 * this OTU set does not belong to a {@code Study}.
	 * 
	 * @return the study to which this OTU set belongs
	 */
	@Nullable
	IStudy getParent();

	Set<StandardMatrix> getStandardMatrices();

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
	boolean removeDNASequenceSet(DNASequenceSet sequenceSet);

	/**
	 * Remove {@code matrix} from this OTU set.
	 * 
	 * @param matrix to be removed
	 * 
	 * @return {@code true} if this OTU set contained the specified matrix,
	 *         {@code false} otherwise
	 */
	boolean removeStandardMatrix(StandardMatrix matrix);

	/**
	 * Remove {@code treeSet} from this OTU set.
	 * 
	 * @param matrix to be removed
	 * 
	 * @return {@code true} if this OTU set contained the specified tree set,
	 *         {@code false} otherwise
	 */
	boolean removeTreeSet(ITreeSet treeSet);

	/**
	 * Setter.
	 * 
	 * @param description the description
	 * 
	 * @return this
	 */
	IOTUSet setDescription(@CheckForNull String description);

	/**
	 * Set the label of this <code>OTUSet</code>.
	 * 
	 * @param label the label
	 * 
	 * @return this
	 */
	IOTUSet setLabel(String label);

	/**
	 * Set this {@code IOTUSet}'s {@code IOTU}s.
	 * <p>
	 * This {@code IOTUSet} makes a copy of {@code newOTUs}.
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

	IOTUSet setParent(@CheckForNull IStudy study);

}