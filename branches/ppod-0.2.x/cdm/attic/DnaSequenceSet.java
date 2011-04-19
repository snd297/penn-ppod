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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.util.UPennCisPPodUtil;

/**
 * A set of {@link DNASequence}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DnaSequenceSet.TABLE)
public class DnaSequenceSet
		extends SequenceSet<DnaSequence> {

	/**
	 * The name of the entity's table.
	 */
	public final static String TABLE = "DNA_SEQUENCE_SET";

	/**
	 * Used for foreign keys that point at this table.
	 */
	public final static String ID_COLUMN = TABLE + "_ID";

	@CheckForNull
	private Long id;

	@CheckForNull
	private Integer version;

	private Map<Otu, DnaSequence> sequences = newHashMap();

	/**
	 * Default constructor.
	 */
	public DnaSequenceSet() {}

	/** {@inheritDoc} */
	public void clearSequences() {
		sequences.clear();
	}

	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@Nullable
	public Long getId() {
		return id;
	}

	@JoinTable(name = TABLE + "_" + DnaSequence.TABLE,
			joinColumns = @JoinColumn(name = ID_COLUMN),
			inverseJoinColumns = @JoinColumn(name = DnaSequence.ID_COLUMN))
	@MapKeyJoinColumn(name = Otu.ID_COLUMN)
	@Override
	public Map<Otu, DnaSequence> getSequences() {
		return sequences;
	}

	/**
	 * @return the version
	 */
	@Version
	@Column(name = "OBJ_VERSION")
	@Nullable
	public Integer getVersion() {
		return version;
	}

	@Override
	@Nullable
	public void putSequence(
			final Otu otu,
			final DnaSequence sequence) {
		UPennCisPPodUtil.put(sequences, otu, sequence, this);
	}

	@SuppressWarnings("unused")
	private void setId(final Long id) {
		this.id = id;
	}

	@SuppressWarnings("unused")
	private void setSequences(final Map<Otu, DnaSequence> sequences) {
		this.sequences = sequences;
	}

	/**
	 * @param version the version to set
	 */
	@SuppressWarnings("unused")
	private void setVersion(final Integer version) {
		this.version = version;
	}

	@Override
	public void updateOtus() {
		UPennCisPPodUtil.updateOtus(getParent(), sequences);
	}
}
