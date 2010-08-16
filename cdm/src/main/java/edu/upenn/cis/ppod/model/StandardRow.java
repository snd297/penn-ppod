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
import javax.xml.bind.annotation.adapters.XmlAdapter;

import edu.upenn.cis.ppod.imodel.IStandardCell;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStandardRow;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * Rows of a {@link StandardMatrix}.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StandardRow.TABLE)
public class StandardRow
		extends Row<IStandardCell, IStandardMatrix>
		implements IStandardRow {

	public static class Adapter extends XmlAdapter<StandardRow, IStandardRow> {

		@Override
		public StandardRow marshal(final IStandardRow row) {
			return (StandardRow) row;
		}

		@Override
		public IStandardRow unmarshal(final StandardRow row) {
			return row;
		}
	}

	/** This entitiy's table name. */
	public static final String TABLE = "STANDARD_ROW";

	public static final String JOIN_COLUMN = TABLE + "_ID";

	@OneToMany(
			mappedBy = "parent",
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			targetEntity = StandardCell.class)
	@OrderBy("position")
	private final List<IStandardCell> cells = newArrayList();

	@ManyToOne(fetch = FetchType.LAZY, optional = false,
			targetEntity = StandardMatrix.class)
	@JoinColumn(name = StandardMatrix.JOIN_COLUMN)
	private IStandardMatrix parent;

	StandardRow() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitStandardRow(this);
		super.accept(visitor);
	}

	@XmlElement(name = "cell")
	@Override
	protected List<IStandardCell> getCellsModifiable() {
		return cells;
	}

	/** {@inheritDoc} */
	public IStandardMatrix getParent() {
		return parent;
	}

	@Override
	public List<IStandardCell> setCells(
			final List<? extends IStandardCell> cells) {
		final List<IStandardCell> clearedCells =
				super.setCellsHelper(cells);

		for (final IStandardCell cell : getCells()) {
			cell.setParent(this);
		}
		return clearedCells;
	}

	/** {@inheritDoc} */
	public void setParent(
			final IStandardMatrix parent) {
		this.parent = parent;
	}
}
