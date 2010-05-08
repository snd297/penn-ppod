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
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.google.inject.Inject;

/**
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNAStateMatrix.TABLE)
public class DNAStateMatrix extends MolecularStateMatrix {

	/** Database table name. */
	public static final String TABLE = "DNA_STATE_MATRIX";

	DNAStateMatrix() {}

	/**
	 * This constructor is {@code protected} to allow for injected {@code
	 * OTUsToCharacterStateRows} in subclasses to be passed up the inheritance
	 * hierarchy.
	 * 
	 * @param otusToRows the {@code OTUsToCharacterStateRows} for this matrix.
	 */
	@Inject
	protected DNAStateMatrix(final OTUsToStandardRows otusToRows) {
		super(otusToRows);
	}
}
