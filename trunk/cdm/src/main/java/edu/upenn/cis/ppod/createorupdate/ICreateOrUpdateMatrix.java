package edu.upenn.cis.ppod.createorupdate;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.Cell;
import edu.upenn.cis.ppod.model.Matrix;
import edu.upenn.cis.ppod.model.Row;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.services.ppodentity.MatrixInfo;

/**
 * @author Sam Donnelly
 * 
 * @param <R>
 * @param <C>
 * @param <E>
 */
public interface ICreateOrUpdateMatrix<R extends Row<C>, C extends Cell<E>, E> {

	MatrixInfo createOrUpdateMatrix(Matrix<R> dbMatrix, Matrix<R> sourceMatrix);

	interface IFactory<R extends Row<C>, C extends Cell<E>, E> {
		ICreateOrUpdateMatrix<R, C, E> create(
				INewPPodVersionInfo newPPodVersionInfo,
				IDAO<Object, Long> dao);
	}
}