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
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A row of {@link DnaCell}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DnaRow.TABLE)
public class DnaRow extends Row<DnaCell, DnaMatrix> {

	public static final String TABLE = "DNA_ROW";

	public static final String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = DnaMatrix.JOIN_COLUMN)
	@Nullable
	private DnaMatrix parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL,
			orphanRemoval = true)
	@OrderColumn(name = "POSITION")
	private final List<DnaCell> cells = newArrayList();

	public DnaRow() {}

	/** {@inheritDoc} */
	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitDnaRow(this);
		super.accept(visitor);
	}

	/** {@inheritDoc} */
	@Override
	protected List<DnaCell> getCellsModifiable() {
		return cells;
	}

	/** {@inheritDoc} */
	public DnaMatrix getParent() {
		return parent;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * DNA matrices do not support moving columns or deleting non-last columns
	 * See <a href="http://code.google.com/p/penn-ppod/issues/detail?id=28">bug
	 * 28</a>.
	 * 
	 * @throws IllegalArgumentException if {@code cells} is such that this
	 *             method should do anything but add or removes cells from the
	 *             end
	 */
	@Override
	public void setCells(final List<? extends DnaCell> cells) {
		checkNotNull(cells);
		checkArgument(
				cells.size() >= getCells().size(),
				"input cells must be longer than the current cells: "
						+ "this method can't do anything but add or removes cells from the "
						+ "end");
		checkArgument(
				cells.subList(0, getCells().size()).equals(getCells()),
				"cells.subList(0, getCells().size()) does not .equals(getCells()) "
						+ "this method can't do anything but add or removes cells from the "
						+ "end");

		super.setCellsHelper(cells);

		for (final DnaCell cell : getCells()) {
			cell.setParent(this);
		}
	}

	/** {@inheritDoc} */
	public void setParent(final DnaMatrix parent) {
		this.parent = parent;
	}
}
