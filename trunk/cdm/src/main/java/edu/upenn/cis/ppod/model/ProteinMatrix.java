package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.upenn.cis.ppod.imodel.IDependsOnParentOtus;
import edu.upenn.cis.ppod.util.UPennCisPPodUtil;

@Entity
@Table(name = ProteinMatrix.TABLE)
public class ProteinMatrix
		extends Matrix<ProteinRow>
		implements IDependsOnParentOtus {

	public final static String TABLE = "PROTEIN_MATRIX";

	public final static String JOIN_COLUMN = TABLE + "_ID";

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = TABLE + "_" + ProteinRow.TABLE,
			inverseJoinColumns = @JoinColumn(name = ProteinRow.JOIN_COLUMN))
	@MapKeyJoinColumn(name = Otu.JOIN_COLUMN)
	private final Map<Otu, ProteinRow> rows = newHashMap();

	/**
	 * No-arg constructor.
	 */
	public ProteinMatrix() {}

	@Override
	public Map<Otu, ProteinRow> getRows() {
		return rows;
	}

	@Override
	public ProteinRow putRow(final Otu otu, final ProteinRow row) {
		checkNotNull(otu);
		checkNotNull(row);
		final ProteinRow oldRow = rows.put(otu, row);
		row.setParent(this);

		if (row != oldRow && oldRow != null) {
			oldRow.setParent(null);
		}
		return oldRow;
	}

	@Override
	public void updateOtus() {
		UPennCisPPodUtil.updateOtus(getParent(), rows);
	}
}
