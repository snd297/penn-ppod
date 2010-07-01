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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.EnumSet;
import java.util.Set;

import javax.annotation.CheckForNull;
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
import javax.xml.bind.Unmarshaller;
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
public class DNACell extends Cell<DNANucleotide> {

	public static final String TABLE = "DNA_CELL";

	public static final String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	/**
	 * The heart of the cell: the {@code DNANucleotide}s.
	 * <p>
	 * At most one of {@code element} and {@code elements} will be {@code null}.
	 */
	@CheckForNull
	@ElementCollection
	@CollectionTable(name = "DNA_CELL_ELEMENTS",
						joinColumns = @JoinColumn(name = JOIN_COLUMN))
	@Column(name = "ELEMENT")
	@Enumerated(EnumType.ORDINAL)
	private Set<DNANucleotide> elements;

	/**
	 * To handle the most-common case of a single element.
	 * <p>
	 * At most of one of {@code element} and {@code elements} will be
	 * {@code null}.
	 */
	@CheckForNull
	@Column(name = "ELEMENT", nullable = true)
	@Enumerated(EnumType.ORDINAL)
	private DNANucleotide element;

	/**
	 * The {@code Row} to which this {@code Cell} belongs.
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = DNARow.JOIN_COLUMN)
	@CheckForNull
	private DNARow row;

	DNACell() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visit(this);
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@Override
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		super.afterUnmarshal(u, parent);
		row = (DNARow) parent;
	}

	@XmlAttribute(name = "nucleotide")
	@Override
	protected DNANucleotide getElement() {
		return element;
	}

	@Override
	protected Set<DNANucleotide> getElementsModifiable() {
		return elements;
	}

	/** This seemingly redundant method created For JAXB. */
	@XmlElement(name = "nucleotide")
	@Override
	protected Set<DNANucleotide> getElementsXml() {
		return super.getElementsXml();
	}

	@Override
	protected DNARow getRow() {
		return row;
	}

	@Override
	protected DNACell setElement(final DNANucleotide firstElement) {
		this.element = firstElement;
		return this;
	}

	@Override
	protected void initElements() {
		this.elements = EnumSet.noneOf(DNANucleotide.class);
	}

	@Override
	protected Cell<DNANucleotide> setElements(
			@CheckForNull final Set<DNANucleotide> elements) {
		if (equal(elements, getElementsModifiable())) {

		} else {
			if (elements == null) {
				this.elements = null;
			} else {
				if (this.elements == null) {
					initElements();
				} else {
					getElementsModifiable().clear();
				}
				if (this.elements == null) {
					// Added for FindBugs
					throw new AssertionError("elements should not be null");
				} else {
					getElementsModifiable().addAll(elements);
				}
			}
		}
		return this;
	}

	@Override
	protected DNACell setPolymorphicOrUncertain(
			final edu.upenn.cis.ppod.model.Cell.Type type,
			final Set<DNANucleotide> elements) {
		checkNotNull(type);
		checkNotNull(elements);

		checkArgument(
				type == Type.POLYMORPHIC
						|| type == Type.UNCERTAIN,
				" type is " + type + " but must be POLYMORPHIC OR UNCERTAIN");

		checkArgument(
				elements.size() > 1,
				"POLYMORPIC AND UNCERTAIN must have greater than 1 element but elements has "
						+ elements.size());

		if (getType() != null
				&& getType()
						.equals(type)
				&& elements
						.equals(this.elements)) {
			return this;
		}

		element = null;
		setElements(elements);

		setType(type);
		setInNeedOfNewVersion();
		return this;
	}

	protected DNACell setRow(final DNARow row) {
		this.row = row;
		return this;
	}

	@Override
	public Cell<DNANucleotide> setSingleElement(final DNANucleotide element) {
		checkNotNull(element);
		if (element == this.element) {
			if (getType() != Type.SINGLE) {
				throw new AssertionError(
						"element is set, but this cell is not a SINGLE");
			}
			return this;
		}
		setElements(null);
		setElement(element);
		setType(Type.SINGLE);
		setInNeedOfNewVersion();
		return this;
	}

	@Override
	protected DNACell unsetRow() {
		row = null;
		return this;
	}

}
