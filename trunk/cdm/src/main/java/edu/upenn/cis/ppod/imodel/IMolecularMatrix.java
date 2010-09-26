package edu.upenn.cis.ppod.imodel;

public interface IMolecularMatrix<R extends IRow<C, ?>, C extends IMolecularCell<?, ?>>
		extends IMatrix<R, C> {
	void setColumnsSize(int columnsSize);
}
