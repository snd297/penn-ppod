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
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A row of {@link DNACell}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNARow.TABLE)
public class DNARow extends Row<DNACell, DNAMatrix> {

	public static final String TABLE = "DNA_ROW";

	public static final String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = DNAMatrix.JOIN_COLUMN)
	@CheckForNull
	private DNAMatrix parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL,
			orphanRemoval = true)
	@OrderBy("position")
	private final List<DNACell> cells = newArrayList();

	DNARow() {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.upenn.cis.ppod.model.Row#accept(edu.upenn.cis.ppod.util.IVisitor)
	 */
	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitDNARow(this);
		super.accept(visitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.upenn.cis.ppod.model.Row#getCellsModifiable()
	 */
	@XmlElement(name = "cell")
	@Override
	protected List<DNACell> getCellsModifiable() {
		return cells;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.upenn.cis.ppod.modelinterfaces.IOTUKeyedMapValue#getParent()
	 */
	public DNAMatrix getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.upenn.cis.ppod.model.Row#setCells(java.util.List)
	 */
	@Override
	public List<DNACell> setCells(final List<? extends DNACell> cells) {
		final List<DNACell> clearedCells = super.setCellsHelper(cells);

		for (final DNACell cell : getCells()) {
			cell.setParent(this);
		}
		return clearedCells;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.upenn.cis.ppod.modelinterfaces.IOTUKeyedMapValue#setParent(java.lang
	 * .Object)
	 */
	public Row<DNACell, DNAMatrix> setParent(final DNAMatrix parent) {
		this.parent = parent;
		return this;
	}

}
