package edu.upenn.cis.ppod.model;

import edu.upenn.cis.ppod.imodel.IMolecularCell;
import edu.upenn.cis.ppod.imodel.IMolecularMatrix;
import edu.upenn.cis.ppod.imodel.IRow;

public abstract class MolecularMatrix<R extends IRow<C, ?>, C extends IMolecularCell<?, ?>>
		extends Matrix<R, C>
		implements IMolecularMatrix<R, C> {

	/** {@inheritDoc} */
	public void setColumnsSize(final int columnsSize) {
		super.resizeColumnVersionInfos(columnsSize);
	}

}
