package edu.upenn.cis.ppod.model;

import static edu.upenn.cis.ppod.util.CollectionsUtil.nullFill;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * A matrix with a fixed number of columns.
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class MolecularMatrix<R extends Row<?>> extends Matrix<R> {

	@Column(name = "COLUMNS_SIZE")
	private Integer columnsSize;

	/**
	 * Get the number of columns that this matrix has. This value is used to
	 * determine the legal lengths of rows added to the matrix.
	 * <p>
	 * Will return {@code null} for newly created objects until
	 * {@link #setColumnsSize(Integer)} is set.
	 * <p>
	 * This is only here for simple error checking and can be changed at will.
	 */
	@Nullable
	@Override
	public Integer getColumnsSize() {
		return columnsSize;
	}

	public MolecularMatrix<R> setColumnsSize(
			@Nonnegative final Integer columnsSize) {

		this.columnsSize = columnsSize;

		// Add in column versions as necessary
		nullFill(getColumnPPodVersionInfosModifiable(), columnsSize);

		// Remove column versions as necessary
		while (getColumnPPodVersionInfos().size() > columnsSize) {
			getColumnPPodVersionInfosModifiable()
					.remove(
							getColumnPPodVersionInfos().size() - 1);
		}
		return this;
	}
}
