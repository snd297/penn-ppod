package edu.upenn.cis.ppod.model;

abstract class MolecularMatrix<R extends Row<C, ?>, C extends CellWithCase<?, ?>>
		extends Matrix<R, C> {

	// public void setColumnsSize(final int columnsSize) {
	// // Add in column versions as necessary
	// checkArgument(columnsSize >= 0, "columnsSize < 0");
	//
	// nullFill(getColumnVersionInfosModifiable(), columnsSize);
	//
	// // Remove column versions as necessary
	// while (getColumnVersionInfos().size() > columnsSize) {
	// getColumnVersionInfosModifiable()
	// .remove(getColumnVersionInfos().size() - 1);
	// }
	// }

}
