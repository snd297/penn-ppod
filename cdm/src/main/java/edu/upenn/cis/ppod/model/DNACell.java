package edu.upenn.cis.ppod.model;

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

import edu.upenn.cis.ppod.modelinterfaces.ICell;

/**
 * A cell composed of {@Link DNAState}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = "DNA_CELL")
public class DNACell extends Cell<DNANucleotide> {

	/**
	 * To handle the most-common case of a single {@code CharacterState}, we
	 * cache {@code states.get(0)}.
	 * <p>
	 * Will be {@code null} if this is a {@link Type#INAPPLICABLE} or
	 * {@link Type#UNASSIGNED}.
	 */
	@Column(name = "FIRST_STATE")
	@Enumerated(EnumType.ORDINAL)
	@CheckForNull
	private DNANucleotide firstState = null;

	/**
	 * The {@code CharacterStateRow} to which this {@code CategoricalCell}
	 * belongs.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = CategoricalRow.ID_COLUMN)
	@CheckForNull
	private DNARow row;

	/**
	 * The heart of the cell: the states.
	 * <p>
	 * Will be {@code null} when first created, but is generally not-null.
	 */
	@ElementCollection
	@CollectionTable(name = "DNA_CELL_STATES", joinColumns = @JoinColumn(name = ID_COLUMN))
	@Column(name = "STATE")
	@Enumerated(EnumType.ORDINAL)
	@CheckForNull
	private Set<DNANucleotide> states = null;

	/**
	 * Used for serialization so we don't have to hit {@code states} directly
	 * and thereby cause unwanted database hits.
	 */
	@Transient
	@CheckForNull
	private Set<DNANucleotide> xmlStates = null;

	@Override
	protected DNANucleotide getFirstState() {
		return firstState;
	}

	@Override
	Row<? extends Cell<? extends DNANucleotide>> getRow() {
		return row;
	}

	@Override
	protected Set<DNANucleotide> getStates() {
		return states;
	}

	@Override
	protected Set<DNANucleotide> getXmlStates() {
		return xmlStates;
	}

	@Override
	protected Cell<DNANucleotide> setFirstState(final DNANucleotide firstState) {
		this.firstState = firstState;
		return this;
	}

	@Override
	protected Cell<DNANucleotide> setTypeAndStates(
			edu.upenn.cis.ppod.model.Cell.Type type,
			Set<? extends DNANucleotide> states) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	protected Cell<DNA> unsetXmlStates() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public ICell unsetRow() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Iterator<DNA> iterator() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
