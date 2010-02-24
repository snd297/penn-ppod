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
package edu.upenn.cis.ppod.model;

import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class MolecularSequenceSet extends UUPPodEntity {

	static final String TABLE = "MOLECULAR_SEQUENCE_SET";

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = OTUSet.ID_COLUMN, nullable = false)
	@Nullable
	private OTUSet otuSet;

	/**
	 * Intentionally package-private and meant to be called in {@link OTUSet}.
	 * 
	 * @param newOTUSet the OTU set that will own this sequence set
	 * 
	 * @return this sequence set
	 */
	MolecularSequenceSet setOTUSet(@Nullable OTUSet newOTUSet) {
		this.otuSet = newOTUSet;
		return this;
	}

	/**
	 * Get this sequence set's owning OTU set.
	 * 
	 * @return this sequence set's owning OTU set
	 */
	public OTUSet getOTUSet() {
		return otuSet;
	}

	/**
	 * Get a copy of the set constituent sequences.
	 * <p>
	 * The sequences themselves will not be copies.
	 * 
	 * @return a copy of the set constituent sequences
	 */
	public abstract Set<MolecularSequence> getSequences();

	@Override
	public MolecularSequenceSet resetPPodVersionInfo() {
		getOTUSet().resetPPodVersionInfo();
		super.resetPPodVersionInfo();
		return this;
	}

}
