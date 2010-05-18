package edu.upenn.cis.ppod.saveorupdate;

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.DNACell;
import edu.upenn.cis.ppod.model.DNANucleotide;
import edu.upenn.cis.ppod.model.DNARow;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;

/**
 * @author Sam Donnelly
 */
@ImplementedBy(SaveOrUpdateDNAMatrix.class)
public interface ISaveOrUpdateDNAMatrix extends
		ISaveOrUpdateMatrix<DNARow, DNACell, DNANucleotide> {
	static interface IFactory extends
			ISaveOrUpdateMatrix.IFactory<DNARow, DNACell, DNANucleotide> {
		ISaveOrUpdateDNAMatrix create(
				INewPPodVersionInfo newPPodVersionInfo,
				IDAO<Object, Long> dao);
	}
}
