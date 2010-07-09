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

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.modelinterfaces.IMatrix;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A row of {@link DNACell}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNARow.TABLE)
@Access(AccessType.PROPERTY)
public class DNARow extends Row<DNACell> {

	public static final String TABLE = "DNA_ROW";

	public static final String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	/**
	 * This is the parent of the row. It lies in between this and the matrix.
	 */
	@CheckForNull
	private DNARows rows;

	DNARow() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visit(this);
		super.accept(visitor);
	}

	@XmlElement(name = "cell")
	@OneToMany(mappedBy = "row", cascade = CascadeType.ALL,
			orphanRemoval = true)
	@OrderBy("position")
	@Override
	protected List<DNACell> getCellsRaw() {
		return super.getCellsRaw();
	}

	/** {@inheritDoc} */
	@Transient
	public IMatrix getMatrix() {
		if (rows == null) {
			return null;
		}
		return rows.getParent();
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = DNARows.JOIN_COLUMN)
	@Override
	protected DNARows getRows() {
		return rows;
	}

	@Override
	public List<DNACell> setCells(final List<? extends DNACell> cells) {
		final List<DNACell> clearedCells = super.setCellsHelper(cells);

		for (final DNACell cell : getCells()) {
			cell.setRow(this);
		}
		return clearedCells;
	}

	void setRows(final DNARows otusToRows) {
		this.rows = otusToRows;
	}

	public Row<DNACell> unsetOTUKeyedMap() {
		rows = null;
		return this;
	}

}
