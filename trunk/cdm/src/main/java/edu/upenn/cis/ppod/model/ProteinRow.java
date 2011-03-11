package edu.upenn.cis.ppod.model;

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

	@CheckForNull
	private Long id;

	@CheckForNull
	private Integer version;

	public static final String TABLE = "PROTEIN_ROW";

	public static final String ID_COLUMN =
			TABLE + "_ID";

	@CheckForNull
	private ProteinMatrix parent;

	@CheckForNull
	private String sequence;

	public ProteinRow() {}

	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@Nullable
	public Long getId() {
		return id;
	}

	/** {@inheritDoc} */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = ProteinMatrix.ID_COLUMN)
	public ProteinMatrix getParent() {
		return parent;
	}

	@Lob
	@Column(nullable = false)
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
		this.sequence = sequence;
	}

	/**
	 * @return the version
	 */
	@Version
	@Column(name = "OBJ_VERSION")
	@Nullable
	public Integer getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	@SuppressWarnings("unused")
	private void setVersion(Integer version) {
		this.version = version;
	}
}
