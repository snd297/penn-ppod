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
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.util.UPennCisPPodUtil;

/**
 * A {@link MolecularMatrix} composed of {@link DNARow}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DnaMatrix.TABLE)
public class DnaMatrix extends Matrix<DnaRow> {

	@CheckForNull
	private Long id;

	@CheckForNull
	private Integer version;

	public final static String TABLE = "DNA_MATRIX";

	public final static String ID_COLUMN = TABLE + "_ID";

	private Map<Otu, DnaRow> rows = newHashMap();

	/**
	 * No-arg constructor.
	 */
	public DnaMatrix() {}

	@Access(AccessType.PROPERTY)
	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@Nullable
	public Long getId() {
		return id;
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = TABLE + "_" + DnaRow.TABLE,
			inverseJoinColumns = @JoinColumn(name = DnaRow.ID_COLUMN))
	@MapKeyJoinColumn(name = Otu.ID_COLUMN)
	@Override
	public Map<Otu, DnaRow> getRows() {
		return rows;
	}

	/**
	 * @return the version
	 */
	@Version
	@Column(name = "OBJ_VERSION")
	public Integer getVersion() {
		return version;
	}

	@Override
	public void putRow(final Otu otu, final DnaRow row) {
		UPennCisPPodUtil.put(rows, otu, row, this);
	}

	@SuppressWarnings("unused")
	private void setId(final Long id) {
		this.id = id;
	}

	@SuppressWarnings("unused")
	private void setRows(final Map<Otu, DnaRow> rows) {
		this.rows = rows;
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
		UPennCisPPodUtil.updateOtus(getParent(), rows);
	}
}
