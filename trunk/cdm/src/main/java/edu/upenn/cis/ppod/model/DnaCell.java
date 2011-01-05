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

import org.hibernate.annotations.Immutable;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.imodel.DnaNucleotide;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A cell that contains {@link DnaNucleotide}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DnaCell.TABLE)
@Immutable
public class DnaCell
		extends MolecularCell<DnaNucleotide, DnaRow> {

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
	@Nullable
	private Set<DnaNucleotide> elements;

	/**
	 * To handle the most-common case of a single element.
	 * <p>
	 * At most of one of {@code element} and {@code elements} will be
	 * {@code null}.
	 */
	@Column(name = "ELEMENT", nullable = true)
	@Enumerated(EnumType.ORDINAL)
	@Nullable
	private DnaNucleotide element;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = DnaRow.JOIN_COLUMN)
	@Nullable
	private DnaRow parent;

	public DnaCell() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitDNACell(this);
	}

	/** {@inheritDoc} */
	@XmlAttribute(name = "nucleotide")
	@Override
	protected DnaNucleotide getElement() {
		return element;
	}

	@Override
	Set<DnaNucleotide> getElementsModifiable() {
		return elements;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Protected for JAXB.
	 */
	@XmlElement(name = "nucleotide")
	@Override
	protected Set<DnaNucleotide> getElementsIfMultiple() {
		return super.getElementsIfMultiple();
	}

	@Override
	public DnaRow getParent() {
		return parent;
	}

	@Override
	void initElements() {
		setElements(EnumSet.noneOf(DnaNucleotide.class));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Protected for JAXB.
	 */
	@Override
	protected void setElement(final DnaNucleotide element) {
		this.element = element;
	}

	/** {@inheritDoc} */
	@Override
	void setElements(final Set<DnaNucleotide> elements) {
		this.elements = elements;
	}

	/** {@inheritDoc} */
	public void setParent(final DnaRow parent) {
		this.parent = parent;
	}
}
