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
import javax.xml.bind.annotation.adapters.XmlAdapter;

import edu.upenn.cis.ppod.imodel.IDNACell;
import edu.upenn.cis.ppod.imodel.IDNAMatrix;
import edu.upenn.cis.ppod.imodel.IDNARow;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A {@link MolecularMatrix} composed of {@link DNARow}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNAMatrix.TABLE)
public class DNAMatrix
		extends MolecularMatrix<IDNARow, IDNACell>
		implements IDNAMatrix {

	public static class Adapter extends XmlAdapter<DNAMatrix, IDNAMatrix> {

		@Override
		public DNAMatrix marshal(final IDNAMatrix matrix) {
			return (DNAMatrix) matrix;
		}

		@Override
		public IDNAMatrix unmarshal(final DNAMatrix matrix) {
			return matrix;
		}
	}

	public final static String TABLE = "DNA_MATRIX";

	@Embedded
	private DNARows rows = new DNARows();

	/**
	 * No-arg constructor.
	 */
	public DNAMatrix() {
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
	protected DNARows getOTUKeyedRows() {
		return rows;
	}

	/** {@inheritDoc} */
	public List<IDNACell> removeColumn(final int columnNo) {
		setColumnsSize(getColumnsSize() - 1);
		return super.removeColumnHelper(columnNo);
	}

	/**
	 * Created for JAXB.
	 */
	protected IDNAMatrix setOTUKeyedRows(final DNARows rows) {
		this.rows = rows;
		return this;
	}

}
