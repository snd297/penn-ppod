package edu.upenn.cis.ppod.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.imodel.IChild;
import edu.upenn.cis.ppod.util.IVisitor;

@Entity
@Table(name = ProteinRow.TABLE)
public class ProteinRow extends PPodEntity implements IChild<ProteinMatrix> {

	public static final String TABLE = "PROTEIN_ROW";
	public static final String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = ProteinMatrix.JOIN_COLUMN)
	@CheckForNull
	private ProteinMatrix parent;

	@Lob
	@Column(nullable = false)
	@CheckForNull
	private String sequence;

	public ProteinRow() {}

	/** {@inheritDoc} */
	@Override
	public void accept(final IVisitor visitor) {
		visitor.visitProteinRow(this);
		super.accept(visitor);
	}

	/** {@inheritDoc} */
	public ProteinMatrix getParent() {
		return parent;
	}

	@Nullable
	public String getSequence() {
		return this.sequence;
	}

	@Override
	public void setInNeedOfNewVersion() {
		super.setInNeedOfNewVersion();
		if (parent != null) {
			parent.setInNeedOfNewVersion();
		}
	}

	/** {@inheritDoc} */
	public void setParent(final ProteinMatrix parent) {
		this.parent = parent;
	}

	public void setSequence(final String sequence) {
		if (!sequence.equals(this.sequence)) {
			this.sequence = sequence;
			setInNeedOfNewVersion();
		}
	}
}
