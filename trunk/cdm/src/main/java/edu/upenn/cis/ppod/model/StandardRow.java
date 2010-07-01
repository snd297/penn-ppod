/*
 * Copyright (C) 2009 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * Rows of a {@link StandardMatrix}.
 * <p>
 * In Hibernate, composite elements may contain components but not collections -
 * that's why this is an entity and not a component class: it needs to contain a
 * {@code List<StandardCell>}.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StandardRow.TABLE)
public class StandardRow extends Row<StandardCell> {

	/** This entitiy's table name. */
	public static final String TABLE = "STANDARD_ROW";

	public static final String JOIN_COLUMN = TABLE + "_ID";

	/**
	 * The {@code CharacterStateCell}s that make up the row.
	 * <p>
	 * There is evidence that {@code DELETE_ORPHAN} slows things down so we're
	 * not including that either.(More of an issue for Protein matrices, but
	 * they share code.) Plus it seems to break things when we use
	 * {@link #clearCells()} to free up cells for garbage collection, even if we
	 * evict the cells and the row.
	 */
	@OneToMany(mappedBy = "row", cascade = CascadeType.REMOVE)
	@OrderBy("position")
	private final List<StandardCell> cells = newArrayList();

	/**
	 * This is the parent of the row. It lies in between this and the matrix.
	 */
	@CheckForNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = DNASequences.JOIN_COLUMN)
	private StandardRows rows;

	StandardRow() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visit(this);
		super.accept(visitor);
	}

	public int getCellPosition(final StandardCell cell) {
		checkNotNull(cell);
		checkArgument(this.equals(cell.getRow()),
				"cell does not belong to this row");
		final Integer cellPosition = cell.getPosition();
		if (cellPosition == null) {
			throw new AssertionError(
					"cell has been assigned to a row but has now position set");
		}
		return cellPosition;
	}

	@XmlElement(name = "cell")
	@Override
	protected List<StandardCell> getCellsModifiable() {
		return cells;
	}

	/**
	 * Getter.
	 * <p>
	 * Will be {@code null} if and only if this row is not part of a matrix.
	 * Will never be {@code null} right after a row is pulled from the DB.
	 * 
	 * @return the {@code CharacterStateMatrix} of which this is a row
	 */
	@Nullable
	public StandardMatrix getMatrix() {
		if (rows == null) {
			return null;
		}
		return rows.getParent();
	}

	@Override
	protected OTUKeyedMap<StandardRow> getParent() {
		return rows;
	}

	@Override
	public List<StandardCell> setCells(
			final List<? extends StandardCell> cells) {
		final List<StandardCell> clearedCells =
				super.setCellsHelper(cells);

		for (final StandardCell cell : getCells()) {
			cell.setRow(this);
		}
		return clearedCells;
	}

	/**
	 * Set the owner of this row.
	 * 
	 * @param rows the owner
	 * 
	 * @return this row
	 */
	protected StandardRow setRows(
			@CheckForNull final StandardRows rows) {
		this.rows = rows;
		return this;
	}

	/**
	 * Constructs a {@code String} with all attributes in name=value format.
	 * 
	 * @return a {@code String} representation of this object
	 */
	@Override
	public String toString() {
		final String TAB = ",";

		final StringBuilder retValue = new StringBuilder();

		retValue.append("CharacterStateRow(").append("id=").append(TAB).append(
				"cells=").append(this.cells).append(")\n");

		return retValue.toString();
	}

	public StandardRow unsetOTUKeyedMap() {
		rows = null;
		return this;
	}

}
