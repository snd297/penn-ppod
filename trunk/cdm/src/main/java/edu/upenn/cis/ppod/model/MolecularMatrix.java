package edu.upenn.cis.ppod.model;

import static edu.upenn.cis.ppod.util.CollectionsUtil.nullFill;

import javax.annotation.Nonnegative;
import javax.persistence.MappedSuperclass;

/**
 * A matrix with a fixed number of columns.
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class MolecularMatrix<R extends Row<?>> extends Matrix<R> {


	public MolecularMatrix<R> setColumnsSize(
			@Nonnegative final Integer columnsSize) {

		// Add in column versions as necessary
		nullFill(getColumnVersionInfosModifiable(), columnsSize);

		// Remove column versions as necessary
		while (getColumnVersionInfos().size() > columnsSize) {
			getColumnVersionInfosModifiable()
					.remove(
							getColumnVersionInfos().size() - 1);
		}
		return this;
	}
}
