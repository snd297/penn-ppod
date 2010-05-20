package edu.upenn.cis.ppod.createorupdate;

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.DNACell;
import edu.upenn.cis.ppod.model.DNAMatrix;
import edu.upenn.cis.ppod.model.DNANucleotide;
import edu.upenn.cis.ppod.model.DNARow;
import edu.upenn.cis.ppod.modelinterfaces.INewVersionInfo;

/**
 * @author Sam Donnelly
 */
@ImplementedBy(CreateOrUpdateDNAMatrix.class)
public interface ICreateOrUpdateDNAMatrix extends
		ICreateOrUpdateMatrix<DNAMatrix, DNARow, DNACell, DNANucleotide> {
	static interface IFactory {
		ICreateOrUpdateDNAMatrix create(
				INewVersionInfo newVersionInfo,
				IDAO<Object, Long> dao);
	}
}
