package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;

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

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.dto.PPodCellType;
import edu.upenn.cis.ppod.dto.PPodProtein;

@Entity
@Table(name = ProteinCell.TABLE)
public class ProteinCell
		extends Cell<PPodProtein, ProteinRow> {

	public static final String TABLE = "PROTEIN_CELL";

	public static final String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = ProteinRow.JOIN_COLUMN)
	@CheckForNull
	private ProteinRow parent;

	/**
	 * If a cell contains multiple elements, we use this. Otherwise it's null.
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
	private Set<PPodProtein> elements;

	/**
	 * To handle the most-common case of a single element.
	 * <p>
	 * At most of one of {@code element} and {@code elements} will be
	 * {@code null}.
	 */
	@Column(name = "ELEMENT", nullable = true)
	@Enumerated(EnumType.ORDINAL)
	@Nullable
	private PPodProtein element;

	@Override
	protected PPodProtein getElement() {
		return element;
	}

	@Override
	Set<PPodProtein> getElementsModifiable() {
		return elements;
	}

	@Override
	@Nullable
	public ProteinRow getParent() {
		return parent;
	}

	@Override
	protected void setElement(final PPodProtein element) {
		this.element = element;
	}

	@Override
	void setElements(final Set<PPodProtein> elements) {
		this.elements = elements;
	}

	/** {@inheritDoc} */
	public void setParent(final ProteinRow parent) {
		this.parent = parent;
	}

	@Override
	public void setSingle(final PPodProtein protein) {
		super.setSingle(protein);
	}

	public void setPolymorphic(final Set<PPodProtein> proteins) {
		checkNotNull(proteins);
		setPolymorphicOrUncertain(PPodCellType.POLYMORPHIC, proteins);
	}

}
