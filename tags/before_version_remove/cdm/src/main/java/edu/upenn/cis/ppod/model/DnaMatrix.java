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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collections;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.UPennCisPPodUtil;

/**
 * A {@link MolecularMatrix} composed of {@link DNARow}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DnaMatrix.TABLE)
public class DnaMatrix extends Matrix<DnaRow> {

	public final static String TABLE = "DNA_MATRIX";

	public final static String JOIN_COLUMN = TABLE + "_ID";

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = TABLE + "_" + DnaRow.TABLE,
			inverseJoinColumns = @JoinColumn(name = DnaRow.JOIN_COLUMN))
	@MapKeyJoinColumn(name = Otu.JOIN_COLUMN)
	private final Map<Otu, DnaRow> rows = newHashMap();

	/**
	 * No-arg constructor.
	 */
	public DnaMatrix() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitDnaMatrix(this);
		for (final DnaRow row : rows.values()) {
			if (row != null) {
				row.accept(visitor);
			}
		}
		super.accept(visitor);
	}

	@Override
	public Map<Otu, DnaRow> getRows() {
		return Collections.unmodifiableMap(rows);
	}

	@Override
	public DnaRow putRow(final Otu otu, final DnaRow row) {
		checkNotNull(otu);
		checkNotNull(row);
		final DnaRow oldRow = rows.put(otu, row);
		row.setParent(this);
		if (row != oldRow || oldRow == null) {
			setInNeedOfNewVersion();
		}

		if (row != oldRow && oldRow != null) {
			oldRow.setParent(null);
		}
		return oldRow;
	}

	@Override
	public void updateOtus() {
		UPennCisPPodUtil.updateOtus(getParent(), rows, this);
	}
}
