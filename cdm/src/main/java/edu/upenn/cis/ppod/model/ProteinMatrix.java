/*
 * Copyright (C) 2011 Trustees of the University of Pennsylvania
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
import edu.upenn.cis.ppod.imodel.IDependsOnParentOtus;
import edu.upenn.cis.ppod.util.UPennCisPPodUtil;

@Entity
@Table(name = ProteinMatrix.TABLE)
public class ProteinMatrix
		extends Matrix<ProteinRow>
		implements IDependsOnParentOtus {

	@CheckForNull
	private Long id;

	@CheckForNull
	private Integer version;

	public final static String TABLE = "PROTEIN_MATRIX";

	public final static String ID_COLUMN = TABLE + "_ID";

	private Map<Otu, ProteinRow> rows = newHashMap();

	/**
	 * No-arg constructor.
	 */
	public ProteinMatrix() {}

	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@Nullable
	public Long getId() {
		return id;
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = TABLE + "_" + ProteinRow.TABLE,
			joinColumns = @JoinColumn(name = ID_COLUMN),
			inverseJoinColumns = @JoinColumn(name = ProteinRow.ID_COLUMN))
	@MapKeyJoinColumn(name = Otu.ID_COLUMN)
	@Override
	public Map<Otu, ProteinRow> getRows() {
		return rows;
	}

	@Version
	@Column(name = "OBJ_VERSION")
	@Nullable
	public Integer getVersion() {
		return version;
	}

	@Override
	public void putRow(final Otu otu, final ProteinRow row) {
		UPennCisPPodUtil.put(rows, otu, row, this);
	}

	@SuppressWarnings("unused")
	private void setId(final Long id) {
		this.id = id;
	}

	@SuppressWarnings("unused")
	private void setRows(final Map<Otu, ProteinRow> rows) {
		this.rows = rows;
	}

	@SuppressWarnings("unused")
	private void setVersion(final Integer version) {
		this.version = version;
	}

	@Override
	public void updateOtus() {
		UPennCisPPodUtil.updateOtus(getParent(), rows);
	}
}
