package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.get;

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
	 * Will be {@code null} when first created, but is generally not-null.
	 */
	@ElementCollection
	@CollectionTable(name = "DNA_CELL_ELEMENTS", joinColumns = @JoinColumn(name = JOIN_COLUMN))
	@Column(name = "ELEMENT")
	@CheckForNull
	private EnumSet<DNANucleotide> elements = null;

	/**
	 * To handle the most-common case of a single {@code CharacterState}, we
	 * cache {@code states.get(0)}.
	 * <p>
	 * Will be {@code null} if this is a {@link Type#INAPPLICABLE} or
	 * {@link Type#UNASSIGNED}.
	 */
	@Column(name = "FIRST_ELEMENT")
	@Enumerated(EnumType.ORDINAL)
	@CheckForNull
	private DNANucleotide firstElement;

	/**
	 * The {@code Row} to which this {@code Cell} belongs.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = DNARow.JOIN_COLUMN)
	@CheckForNull
	private DNARow row;

	DNACell() {}

	@Override
	public void accept(final IVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	@CheckForNull
	protected Set<DNANucleotide> getElementsRaw() {
		if (elements == null) {
			elements = EnumSet.noneOf(DNANucleotide.class);
		}
		return elements;
	}

	@Override
	protected DNANucleotide getFirstElement() {
		return firstElement;
	}

	@Override
	protected DNARow getRow() {
		return row;
	}

	/** For JAXB. */
	@XmlElement(name = "element")
	@Override
	protected Set<DNANucleotide> getXmlElements() {
		return super.getXmlElements();
	}

	@Override
	protected DNACell setFirstElement(final DNANucleotide firstElement) {
		this.firstElement = firstElement;
		return this;
	}

	protected DNACell setRow(final DNARow row) {
		this.row = row;
		return this;
	}

	@Override
	protected Cell<DNANucleotide> setTypeAndElements(
			final edu.upenn.cis.ppod.model.Cell.Type type,
			final Set<? extends DNANucleotide> states) {
		checkNotNull(type);
		checkNotNull(states);

		// So FindBugs knows that we got it when it wasn't null
		final Set<DNANucleotide> thisElements = getElementsRaw();

		if (getType() != null
				&& getType()
						.equals(type)
				&& states
						.equals(getElements())) {
			return this;
		}

		clearElements();

		thisElements.addAll(states);

		if (states.size() > 0) {
			firstElement = get(thisElements, 0);
		}

		setType(type);
		setInNeedOfNewVersion();
		return this;
	}

	@Override
	protected DNACell unsetRow() {
		row = null;
		return this;
	}
}
