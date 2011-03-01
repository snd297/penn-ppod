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

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A {@link MolecularMatrix} composed of {@link DNARow}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DnaMatrix.TABLE)
public class DnaMatrix extends Matrix<DnaRow> {

	public final static String TABLE = "DNA_MATRIX";

	public final static String JOIN_COLUMN = TABLE + "_ID";

	@Embedded
	private DnaRows rows = new DnaRows(this);

	/**
	 * No-arg constructor.
	 */
	public DnaMatrix() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitDnaMatrix(this);
		rows.accept(visitor);
		super.accept(visitor);
	}

	@Override
	protected DnaRows getOtuKeyedRows() {
		return rows;
	}

}
