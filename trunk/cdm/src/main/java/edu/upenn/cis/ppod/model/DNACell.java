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

import java.util.EnumSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A cell that contains {@link DNANucleotide}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNACell.TABLE)
@Access(AccessType.PROPERTY)
public class DNACell extends Cell<DNANucleotide, DNARow> {

	public static final String TABLE = "DNA_CELL";

	public static final String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	DNACell() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visit(this);
	}

	@XmlAttribute(name = "nucleotide")
	@Column(name = "ELEMENT", nullable = true)
	@Enumerated(EnumType.ORDINAL)
	@Override
	protected DNANucleotide getElement() {
		return super.getElement();
	}

	@ElementCollection
	@CollectionTable(name = "DNA_CELL_ELEMENTS",
						joinColumns = @JoinColumn(name = JOIN_COLUMN))
	@Column(name = "ELEMENT")
	@Enumerated(EnumType.ORDINAL)
	@Override
	protected Set<DNANucleotide> getElementsRaw() {
		return super.getElementsRaw();
	}

	@XmlElement(name = "nucleotide")
	@Transient
	@Override
	protected Set<DNANucleotide> getElementsXml() {
		return super.getElementsXml();
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = DNARow.JOIN_COLUMN)
	@Override
	protected DNARow getRow() {
		return super.getRow();
	}

	@Override
	protected void initElements() {
		setElementsRaw(EnumSet.noneOf(DNANucleotide.class));
	}

	/**
	 * For Jaxb.
	 */
	@Override
	protected void setElement(final DNANucleotide nucleotide) {
		super.setElement(nucleotide);
	}

}
