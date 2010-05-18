package edu.upenn.cis.ppod.createorupdate;

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.DNACell;
import edu.upenn.cis.ppod.model.DNANucleotide;
import edu.upenn.cis.ppod.model.DNARow;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;

/**
 * @author Sam Donnelly
 */
@ImplementedBy(MergeAndMakeDNAMatrixPersistent.class)
public interface IMergeAndMakeDNAMatrixPersistent extends
		IMergeAndMakeMatrixPersistent<DNARow, DNACell, DNANucleotide> {
	static interface IFactory extends
			IMergeAndMakeMatrixPersistent.IFactory<DNARow, DNACell, DNANucleotide> {
		IMergeAndMakeDNAMatrixPersistent create(
				INewPPodVersionInfo newPPodVersionInfo,
				IDAO<Object, Long> dao);
	}
}
