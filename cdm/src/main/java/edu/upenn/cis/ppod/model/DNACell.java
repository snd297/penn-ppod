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

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A cell that contains {@link DNANucleotide}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNACell.TABLE)
public class DNACell extends Cell<DNANucleotide, DNARow> {

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

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = DNARow.JOIN_COLUMN)
	private DNARow parent;

	DNACell() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitDNACell(this);
	}

	@XmlAttribute(name = "nucleotide")
	@Override
	protected DNANucleotide getElement() {
		return element;
	}

	@Override
	Set<DNANucleotide> getElementsModifiable() {
		return elements;
	}

	@XmlElement(name = "nucleotide")
	@Override
	protected Set<DNANucleotide> getElementsXml() {
		return super.getElementsXml();
	}

	@Override
	public DNARow getParent() {
		return parent;
	}

	@Override
	void initElements() {
		setElements(EnumSet.noneOf(DNANucleotide.class));
	}

	/**
	 * Protected for Jaxb.
	 */
	@Override
	protected void setElement(final DNANucleotide element) {
		this.element = element;
	}

	@Override
	void setElements(final Set<DNANucleotide> elements) {
		this.elements = elements;
	}

	@Override
	void setParent(final DNARow parent) {
		this.parent = parent;
	}

}
