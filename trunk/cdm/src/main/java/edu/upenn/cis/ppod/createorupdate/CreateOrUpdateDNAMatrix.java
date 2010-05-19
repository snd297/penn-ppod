package edu.upenn.cis.ppod.createorupdate;

import static com.google.common.collect.Iterables.get;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.DNACell;
import edu.upenn.cis.ppod.model.DNAMatrix;
import edu.upenn.cis.ppod.model.DNANucleotide;
import edu.upenn.cis.ppod.model.DNARow;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.services.ppodentity.MatrixInfo;

final class CreateOrUpdateDNAMatrix
		extends CreateOrUpdateMatrix<DNAMatrix, DNARow, DNACell, DNANucleotide>
		implements ICreateOrUpdateDNAMatrix {

	@Inject
	CreateOrUpdateDNAMatrix(Provider<DNARow> rowProvider,
			Provider<DNACell> cellProvider,
			Provider<Attachment> attachmentProvider,
			Provider<MatrixInfo> matrixInfoProvider,
			@Assisted INewPPodVersionInfo newPPodVersionInfo,
			@Assisted IDAO<Object, Long> dao) {
		super(rowProvider, cellProvider, attachmentProvider,
				matrixInfoProvider,
				newPPodVersionInfo, dao);
	}

	@Override
	public MatrixInfo createOrUpdateMatrix(
			final DNAMatrix dbMatrix,
			final DNAMatrix sourceMatrix) {
		dbMatrix.setColumnsSize(
				get(sourceMatrix.getRows()
								.values(), 0)
						.getCells()
						.size());
		return super.createOrUpdateMatrix(dbMatrix, sourceMatrix);
	}

}
