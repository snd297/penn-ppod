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

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A {@link MolecularMatrix} composed of {@link DNARow}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DnaMatrix.TABLE)
public class DnaMatrix
		extends MolecularMatrix<DnaRow, DnaCell> {

	public final static String TABLE = "DNA_MATRIX";

	@Embedded
	private DnaRows rows = new DnaRows();

	/**
	 * No-arg constructor.
	 */
	public DnaMatrix() {
		rows.setParent(this);
	}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitDNAMatrix(this);
		super.accept(visitor);
	}

	/**
	 * Created for JAXB.
	 */
	@XmlElement(name = "rows")
	@Override
	protected DnaRows getOTUKeyedRows() {
		return rows;
	}

	/** {@inheritDoc} */
	public List<DnaCell> removeColumn(final int columnNo) {
		setColumnsSize(getColumnsSize() - 1);
		return super.removeColumnHelper(columnNo);
	}

	/**
	 * Created for JAXB.
	 */
	protected void setOTUKeyedRows(final DnaRows rows) {
		this.rows = rows;
	}

}
