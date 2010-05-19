package edu.upenn.cis.ppod.createorupdate;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.Cell;
import edu.upenn.cis.ppod.model.Matrix;
import edu.upenn.cis.ppod.model.Row;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.services.ppodentity.MatrixInfo;

public interface ICreateOrUpdateMatrix<M extends Matrix<R>, R extends Row<C>, C extends Cell<E>, E> {

	MatrixInfo createOrUpdateMatrix(M dbMatrix, M sourceMatrix);

	interface IFactory<M extends Matrix<R>, R extends Row<C>, C extends Cell<E>, E> {
		ICreateOrUpdateMatrix<M, R, C, E> create(
				INewPPodVersionInfo newPPodVersionInfo,
				IDAO<Object, Long> dao);
	}
}