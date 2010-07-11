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

import java.util.List;

import javax.annotation.CheckForNull;
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

import edu.upenn.cis.ppod.modelinterfaces.IOTUKeyedMapValue;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * Rows of a {@link StandardMatrix}.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StandardRow.TABLE)
@Access(AccessType.PROPERTY)
public class StandardRow extends Row<StandardCell> {

	/** This entitiy's table name. */
	public static final String TABLE = "STANDARD_ROW";

	public static final String JOIN_COLUMN = TABLE + "_ID";

	@CheckForNull
	private StandardMatrix matrix;

	StandardRow() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visit(this);
		super.accept(visitor);
	}

	@Transient
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
	@OneToMany(mappedBy = "row", cascade = CascadeType.ALL,
			orphanRemoval = true)
	@OrderBy("position")
	@Override
	protected List<StandardCell> getCellsRaw() {
		return super.getCellsRaw();
	}

	/**
	 * Getter.
	 * <p>
	 * Will be {@code null} if and only if this row is not part of a matrix.
	 * Will never be {@code null} right after a row is pulled from the DB.
	 * 
	 * @return the {@code StandardMatrix} of which this is a row
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = StandardMatrix.JOIN_COLUMN)
	public StandardMatrix getMatrix() {
		return matrix;
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

	void setMatrix(final StandardMatrix matrix) {
		this.matrix = matrix;
	}

	/** {@inheritDoc} */
	public IOTUKeyedMapValue unsetParent() {
		matrix = null;
		return this;
	}
}
