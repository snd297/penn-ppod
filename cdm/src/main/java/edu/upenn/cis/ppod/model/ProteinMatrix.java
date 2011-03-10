package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import javax.annotation.Nullable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.imodel.IDependsOnParentOtus;
import edu.upenn.cis.ppod.util.UPennCisPPodUtil;

@Entity
@Table(name = ProteinMatrix.TABLE)
public class ProteinMatrix
		extends Matrix<ProteinRow>
		implements IDependsOnParentOtus {

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

	public final static String TABLE = "PROTEIN_MATRIX";

	public final static String ID_COLUMN = TABLE + "_ID";

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = TABLE + "_" + ProteinRow.TABLE,
			inverseJoinColumns = @JoinColumn(name = ProteinRow.ID_COLUMN))
	@MapKeyJoinColumn(name = Otu.ID_COLUMN)
	private final Map<Otu, ProteinRow> rows = newHashMap();

	/**
	 * No-arg constructor.
	 */
	public ProteinMatrix() {}

	@Nullable
	public Long getId() {
		return id;
	}

	@Override
	public Map<Otu, ProteinRow> getRows() {
		return rows;
	}

	@Override
	public void putRow(final Otu otu, final ProteinRow row) {
		UPennCisPPodUtil.put(rows, otu, row, this);
	}

	@SuppressWarnings("unused")
	private void setId(final Long id) {
		this.id = id;
	}

	@Override
	public void updateOtus() {
		UPennCisPPodUtil.updateOtus(getParent(), rows);
	}
}
