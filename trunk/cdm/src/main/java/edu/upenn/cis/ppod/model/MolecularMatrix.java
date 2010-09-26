package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkArgument;
import static edu.upenn.cis.ppod.util.CollectionsUtil.nullFill;
import edu.upenn.cis.ppod.imodel.IMolecularCell;
import edu.upenn.cis.ppod.imodel.IMolecularMatrix;
import edu.upenn.cis.ppod.imodel.IRow;

public abstract class MolecularMatrix<R extends IRow<C, ?>, C extends IMolecularCell<?, ?>>
		extends Matrix<R, C>
		implements IMolecularMatrix<R, C> {

	/** {@inheritDoc} */
	public void setColumnsSize(final int columnsSize) {
		// Add in column versions as necessary
		checkArgument(columnsSize >= 0, "columnsSize < 0");

		nullFill(getColumnVersionInfosModifiable(), columnsSize);

		// Remove column versions as necessary
		while (getColumnVersionInfos().size() > columnsSize) {
			getColumnVersionInfosModifiable()
					.remove(getColumnVersionInfos().size() - 1);
		}
	}

}
