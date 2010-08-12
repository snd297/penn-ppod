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

import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public interface ISequenceSet<S extends ISequence<?>>
		extends ILabeled, IChild<IOTUSet>, IUUPPodEntity {

	/**
	 * Get the sequence indexed by {@code otu}.
	 * 
	 * @param otu index
	 * 
	 * @return the sequence at {@code otu}
	 * 
	 * @throws IllegalArgument Exception if {@code otu} does not belong to this
	 *             sequence's {@code OTUSet}
	 */
	@Nullable
	S getSequence(IOTU otu);

	/**
	 * Get the length of the sequences in this set, or {@code null} if no
	 * sequences have been added to this set.
	 * 
	 * @return the length of the sequences in this set, or {@code null} if no
	 *         sequences have been added to this set
	 */
	@CheckForNull
	Integer getSequenceLengths();

	Map<IOTU, S> getSequences();

	/**
	 * 
	 * 
	 * @param otu
	 * @param sequence
	 * 
	 * @throws IllegalArgumentException if {@code sequence.getSequence() ==
	 *             null}
	 * @throws IllegalArgumentException if
	 *             {@code sequence.getSequence().length() != this.getLength()}
	 * @return
	 */
	@CheckForNull
	S putSequence(IOTU otu, S sequence);

	void setLabel(String label);

	/**
	 * Remove all sequences from this set. Null's out the
	 * {@code ISequence->ISequenceSet} relationships and sets
	 * {@code getSequenceLengths()} to {@code null}.
	 */
	void clearSequences();

}