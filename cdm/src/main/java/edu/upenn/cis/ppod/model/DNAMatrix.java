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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A {@link MolecularMatrix} composed of {@link DNARow}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNAMatrix.TABLE)
public class DNAMatrix extends Matrix<DNARow> {
	public final static String TABLE = "DNA_MATRIX";

	@OneToOne(fetch = FetchType.LAZY, optional = false,
			cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = DNARows.JOIN_COLUMN)
	private DNARows rows;

	DNAMatrix() {}

	@Inject
	DNAMatrix(final DNARows rows) {
		this.rows = rows;
		this.rows.setMatrix(this);
	}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visit(this);
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

	/**
	 * Created for JAXB.
	 */
	protected DNAMatrix setOTUKeyedRows(final DNARows rows) {
		this.rows = rows;
		return this;
	}

}
