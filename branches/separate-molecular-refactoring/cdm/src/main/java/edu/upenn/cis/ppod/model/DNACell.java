package edu.upenn.cis.ppod.model;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.CheckForNull;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import edu.upenn.cis.ppod.modelinterfaces.ICell;

/**
 * A cell composed of {@Link DNAState}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = "DNA_CELL")
public class DNACell extends MolecularCell<DNAState> {

	/**
	 * To handle the most-common case of a single {@code CharacterState}, we
	 * cache {@code states.get(0)}.
	 * <p>
	 * Will be {@code null} if this is a {@link Type#INAPPLICABLE} or
	 * {@link Type#UNASSIGNED}.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FIRST_" + DNAState.ID_COLUMN)
	@CheckForNull
	private DNAState firstState = null;

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
	@ManyToMany
	@Sort(type = SortType.COMPARATOR, comparator = CategoricalState.CharacterStateComparator.class)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = CategoricalState.ID_COLUMN))
	@CheckForNull
	private Set<DNAState> states = null;

	/**
	 * Used for serialization so we don't have to hit {@code states} directly
	 * and thereby cause unwanted database hits.
	 */
	@Transient
	@CheckForNull
	private Set<DNAState> xmlStates = null;

	@Override
	protected DNAState getFirstState() {
		return firstState;
	}

	@Override
	Row<? extends Cell<? extends DNAState>> getRow() {
		return row;
	}

	@Override
	protected Set<DNAState> getStates() {
		return states;
	}

	@Override
	protected Set<DNAState> getXmlStates() {
		return xmlStates;
	}

	@Override
	protected Cell<DNAState> setFirstState(final DNAState firstState) {
		this.firstState = firstState;
		return this;
	}

	@Override
	protected Cell<DNAState> setTypeAndStates(
			edu.upenn.cis.ppod.model.Cell.Type type,
			Set<? extends DNAState> states) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	protected Cell<DNAState> unsetXmlStates() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public ICell unsetRow() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Iterator<DNAState> iterator() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
