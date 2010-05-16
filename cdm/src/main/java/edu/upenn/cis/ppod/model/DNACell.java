package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
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
import javax.persistence.Transient;

/**
 * A cell that contains {@link DNANucleotide}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNACell.TABLE)
public class DNACell extends Cell<DNANucleotide> {

	public static final String TABLE = "DNA_CELL";

	public static final String JOIN_COLUMN = TABLE + "_"
												+ PersistentObject.ID_COLUMN;

	/**
	 * The heart of the cell: the {@code DNANucleotide}s.
	 * <p>
	 * Will be {@code null} when first created, but is generally not-null.
	 */
	@ElementCollection
	@CollectionTable(name = "DNA_CELL_STATES", joinColumns = @JoinColumn(name = JOIN_COLUMN))
	@Column(name = "ELEMENT")
	@CheckForNull
	private Set<DNANucleotide> elements = null;

	/**
	 * To handle the most-common case of a single {@code CharacterState}, we
	 * cache {@code states.get(0)}.
	 * <p>
	 * Will be {@code null} if this is a {@link Type#INAPPLICABLE} or
	 * {@link Type#UNASSIGNED}.
	 */
	@Enumerated(EnumType.ORDINAL)
	@CheckForNull
	private DNANucleotide firstElement;

	/**
	 * The {@code CharacterStateRow} to which this {@code CharacterStateCell}
	 * belongs.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = CharacterStateRow.JOIN_COLUMN)
	@CheckForNull
	private DNARow row;

	/**
	 * Used for serialization so we don't have to hit {@code states} directly
	 * and thereby cause unwanted database hits.
	 */
	@Transient
	@CheckForNull
	private Set<DNANucleotide> xmlStates = null;

	protected DNACell() {}

	@Override
	@CheckForNull
	protected Set<DNANucleotide> getElementsRaw() {
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

	@Override
	protected Set<DNANucleotide> getXmlElements() {
		if (xmlStates == null) {
			xmlStates = newHashSet();
		}
		return xmlStates;
	}

	public Iterator<DNANucleotide> iterator() {
		return Collections.unmodifiableSet(elements).iterator();
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

		if (getElementsRaw() == null) {
			this.elements = EnumSet.noneOf(DNANucleotide.class);
		}

		// So FindBugs knows that we got it when it wasn't null
		final Set<DNANucleotide> thisElements = getElementsRaw();

		if (getType() != null && getType().equals(type)
				&& states.equals(getElements())) {
			return this;
		}

		clearElements();

		thisElements.addAll(states);

		if (states.size() > 0) {
			firstElement = get(thisElements, 0);
		}

		setType(type);
		setInNeedOfNewPPodVersionInfo();
		return this;
	}

	@Override
	protected Cell<DNANucleotide> unsetFirstElement() {
		firstElement = null;
		return this;
	}

	@Override
	protected DNACell unsetRow() {
		row = null;
		return this;
	}

	@Override
	protected Cell<DNANucleotide> unsetXmlElements() {
		xmlStates = null;
		return this;
	}

}
