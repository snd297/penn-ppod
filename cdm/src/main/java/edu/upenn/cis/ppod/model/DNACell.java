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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.imodel.IDNACell;
import edu.upenn.cis.ppod.imodel.IDNARow;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A cell that contains {@link DNANucleotide}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNACell.TABLE)
public class DNACell
		extends MolecularCell<DNANucleotide, IDNARow>
		implements IDNACell {

	public static class Adapter extends XmlAdapter<DNACell, IDNACell> {

		@Override
		public DNACell marshal(final IDNACell dnaCell) {
			return (DNACell) dnaCell;
		}

		@Override
		public IDNACell unmarshal(final DNACell dnaCell) {
			return dnaCell;
		}
	}

	public static final String TABLE = "DNA_CELL";

	public static final String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	/**
	 * The heart of the cell: the {@code DNANucleotide}s.
	 * <p>
	 * At most one of {@code element} and {@code elements} will be non-
	 * {@code null}.
	 */
	@ElementCollection
	@CollectionTable(name = "DNA_CELL_ELEMENTS",
						joinColumns = @JoinColumn(name = JOIN_COLUMN))
	@Column(name = "ELEMENT")
	@Enumerated(EnumType.ORDINAL)
	@CheckForNull
	private Set<DNANucleotide> elements;

	/**
	 * To handle the most-common case of a single element.
	 * <p>
	 * At most of one of {@code element} and {@code elements} will be
	 * {@code null}.
	 */
	@Column(name = "ELEMENT", nullable = true)
	@Enumerated(EnumType.ORDINAL)
	@CheckForNull
	private DNANucleotide element;

	@ManyToOne(fetch = FetchType.LAZY, optional = false,
			targetEntity = DNARow.class)
	@JoinColumn(name = DNARow.JOIN_COLUMN)
	private IDNARow parent;

	DNACell() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitDNACell(this);
	}

	/** {@inheritDoc} */
	@XmlAttribute(name = "nucleotide")
	@Override
	protected DNANucleotide getElement() {
		return element;
	}

	@Override
	Set<DNANucleotide> getElementsModifiable() {
		return elements;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Protected for JAXB.
	 */
	@XmlElement(name = "nucleotide")
	@Override
	protected Set<DNANucleotide> getElementsXml() {
		return super.getElementsXml();
	}

	@Override
	public IDNARow getParent() {
		return parent;
	}

	@Override
	void initElements() {
		setElements(EnumSet.noneOf(DNANucleotide.class));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Protected for JAXB.
	 */
	@Override
	protected void setElement(final DNANucleotide element) {
		this.element = element;
	}

	/** {@inheritDoc} */
	@Override
	void setElements(final Set<DNANucleotide> elements) {
		this.elements = elements;
	}

	/** {@inheritDoc} */
	public void setParent(final IDNARow parent) {
		this.parent = parent;
	}
}
