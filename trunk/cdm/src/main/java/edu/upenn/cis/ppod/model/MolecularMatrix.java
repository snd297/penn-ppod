package edu.upenn.cis.ppod.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * @author Sam Donnelly
 * 
 */
@MappedSuperclass
public abstract class MolecularMatrix<R extends Row<?>> extends Matrix<R> {

	@Column(name = "COLUMNS_SIZE", nullable = false)
	private Integer columnsSize;

	public int getColumnsSize() {
		return columnsSize;
	}

	public MolecularMatrix<R> setColumnsSize(final int columnsSize) {
		this.columnsSize = columnsSize;
		return this;
	}
}
