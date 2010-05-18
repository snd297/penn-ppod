package edu.upenn.cis.ppod.saveorupdate;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.DNACell;
import edu.upenn.cis.ppod.model.DNANucleotide;
import edu.upenn.cis.ppod.model.DNARow;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.services.ppodentity.MatrixInfo;

/**
 * @author Sam Donnelly
 */
class SaveOrUpdateDNAMatrix extends
		SaveOrUpdateMatrix<DNARow, DNACell, DNANucleotide> implements
		ISaveOrUpdateDNAMatrix {
	@Inject
	SaveOrUpdateDNAMatrix(Provider<DNARow> rowProvider,
			Provider<DNACell> cellProvider,
			Provider<Attachment> attachmentProvider,
			Provider<MatrixInfo> matrixInfoProvider,
			@Assisted INewPPodVersionInfo newPPodVersionInfo,
			@Assisted IDAO<Object, Long> dao) {
		super(rowProvider, cellProvider, attachmentProvider,
				matrixInfoProvider,
				newPPodVersionInfo, dao);
	}

}
