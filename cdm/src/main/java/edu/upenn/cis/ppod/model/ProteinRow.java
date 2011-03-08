package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.imodel.IChild;

@Entity
@Table(name = ProteinRow.TABLE)
public class ProteinRow implements IChild<ProteinMatrix> {

	@Access(AccessType.PROPERTY)
	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@CheckForNull
	private Long id;

	@SuppressWarnings("unused")
	@Version
	@Column(name = "OBJ_VERSION")
	@CheckForNull
	private Integer objVersion;

	public static final String TABLE = "PROTEIN_ROW";

	public static final String ID_COLUMN =
			TABLE + "_ID";

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = ProteinMatrix.ID_COLUMN)
	@CheckForNull
	private ProteinMatrix parent;
	@Lob
	@Column(nullable = false)
	@CheckForNull
	private String sequence;

	public ProteinRow() {}

	@Nullable
	public Long getId() {
		return id;
	}

	/** {@inheritDoc} */
	public ProteinMatrix getParent() {
		return parent;
	}

	@Nullable
	public String getSequence() {
		return this.sequence;
	}

	@SuppressWarnings("unused")
	private void setId(final Long id) {
		this.id = id;
	}

	/** {@inheritDoc} */
	public void setParent(final ProteinMatrix parent) {
		this.parent = parent;
	}

	public void setSequence(final String sequence) {
		this.sequence = checkNotNull(sequence);
	}
}
