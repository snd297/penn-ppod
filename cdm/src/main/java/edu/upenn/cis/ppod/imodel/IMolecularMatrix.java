package edu.upenn.cis.ppod.imodel;

public interface IMolecularMatrix<R extends IRow<C, ?>, C extends ICell<?, ?>>
		extends IMatrix<R, C> {
	void setColumnsSize(final int columnsSize);
}
