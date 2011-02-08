package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.util.IVisitor;

@Entity
@Table(name = ProteinRow.TABLE)
public class ProteinRow extends Row<ProteinCell, ProteinMatrix> {

	public static final String TABLE = "PROTEIN_ROW";
	public static final String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = ProteinMatrix.JOIN_COLUMN)
	@CheckForNull
	private ProteinMatrix parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL,
			orphanRemoval = true)
	@OrderColumn(name = "POSITION")
	private final List<ProteinCell> cells = newArrayList();

	public ProteinRow() {}

	/** {@inheritDoc} */
	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitProteinRow(this);
		super.accept(visitor);
	}

	/** {@inheritDoc} */
	@Override
	protected List<ProteinCell> getCellsModifiable() {
		return cells;
	}

	/** {@inheritDoc} */
	public ProteinMatrix getParent() {
		return parent;
	}

	public void addCell(final ProteinCell cell) {
		checkNotNull(cell);
		cells.add(cell);
		cell.setPosition(cells.size() - 1);
		cell.setParent(this);
		setInNeedOfNewVersion();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCells(final List<? extends ProteinCell> cells) {
		checkNotNull(cells);
		super.setCellsHelper(cells);
		for (final ProteinCell cell : getCells()) {
			cell.setParent(this);
		}
	}

	/** {@inheritDoc} */
	public void setParent(final ProteinMatrix parent) {
		this.parent = parent;
	}
}
