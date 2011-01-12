package edu.upenn.cis.ppod.model;

import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class ProteinCell
		extends MolecularCell<Protein, ProteinRow> {

	public static final String TABLE = "PROTEIN_CELL";

	public static final String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = ProteinRow.JOIN_COLUMN)
	@Nullable
	private ProteinRow parent;

	/**
	 * The heart of the cell: the {@code DNANucleotide}s.
	 * <p>
	 * At most one of {@code element} and {@code elements} will be non-
	 * {@code null}.
	 */
	@ElementCollection
	@CollectionTable(name = "PROTEIN_CELL_ELEMENTS",
						joinColumns = @JoinColumn(name = JOIN_COLUMN))
	@Column(name = "ELEMENT")
	@Enumerated(EnumType.ORDINAL)
	@Nullable
	private Set<Protein> elements;

	/**
	 * To handle the most-common case of a single element.
	 * <p>
	 * At most of one of {@code element} and {@code elements} will be
	 * {@code null}.
	 */
	@Column(name = "ELEMENT", nullable = true)
	@Enumerated(EnumType.ORDINAL)
	@Nullable
	private Protein element;

	@Override
	protected Protein getElement() {
		return element;
	}

	@Override
	Set<Protein> getElementsModifiable() {
		return elements;
	}

	@Override
	@Nullable
	public ProteinRow getParent() {
		return parent;
	}

	@Override
	protected void setElement(final Protein element) {
		this.element = element;
	}

	@Override
	void setElements(final Set<Protein> elements) {
		this.elements = elements;
	}

	/** {@inheritDoc} */
	public void setParent(@CheckForNull final ProteinRow parent) {
		this.parent = parent;
	}

}
