package edu.upenn.cis.ppod.saveorupdate;

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
public interface ISaveOrUpdateMatrix<R extends Row<C>, C extends Cell<E>, E> {

	MatrixInfo saveOrUpdate(Matrix<R> dbMatrix, Matrix<R> sourceMatrix);

	interface IFactory<R extends Row<C>, C extends Cell<E>, E> {
		ISaveOrUpdateMatrix<R, C, E> create(
				INewPPodVersionInfo newPPodVersionInfo,
				IDAO<Object, Long> dao);
	}
}