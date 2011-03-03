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
import static com.google.common.base.Preconditions.checkState;
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

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * Rows of a {@link StandardMatrix}.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StandardRow.TABLE)
public class StandardRow
		extends Row<StandardCell, StandardMatrix> {

	/** This entitiy's table name. */
	public static final String TABLE = "STANDARD_ROW";

	public static final String JOIN_COLUMN = TABLE + "_ID";

	@OneToMany(
			mappedBy = "parent",
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	@OrderColumn(name = "POSITION")
	private final List<StandardCell> cells = newArrayList();

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = StandardMatrix.JOIN_COLUMN)
	@CheckForNull
	private StandardMatrix parent;

	public StandardRow() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitStandardRow(this);
		super.accept(visitor);
	}

	@Override
	protected List<StandardCell> getCellsModifiable() {
		return cells;
	}

	/** {@inheritDoc} */
	public StandardMatrix getParent() {
		return parent;
	}

	@Override
	public void setCells(
			final List<? extends StandardCell> cells) {
		checkState(parent != null);
		checkState(parent.getCharacters().size() == cells.size(),
				"the matrix has different number of columns "
						+ parent.getCharacters().size()
						+ " than cells "
						+ cells.size());

		super.setCellsHelper(cells);

		for (final StandardCell cell : getCells()) {
			cell.setParent(this);
		}
	}

	/** {@inheritDoc} */
	public void setParent(final StandardMatrix parent) {
		this.parent = parent;
	}
}
