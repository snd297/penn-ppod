package edu.upenn.cis.ppod.saveorupdate;

import com.google.inject.Provider;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.DNASequence;
import edu.upenn.cis.ppod.model.DNASequenceSet;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;

/**
 * @author Sam Donnelly
 * 
 */
public class MergeDNASequenceSetsFactory implements
		IMergeMolecularSequenceSets.IFactory<DNASequenceSet, DNASequence> {

	private final Provider<DNASequence> dnaSequenceProvider;

	public MergeDNASequenceSetsFactory(
			final Provider<DNASequence> dnaSequenceSetProvider) {
		this.dnaSequenceProvider = dnaSequenceSetProvider;
	}

	public IMergeMolecularSequenceSets<DNASequenceSet, DNASequence> create(
			IDAO<Object, Long> dao, INewPPodVersionInfo newPPodVersionInfo) {
		return new MergeMolecularSequenceSets<DNASequenceSet, DNASequence>(
				dnaSequenceProvider, dao, newPPodVersionInfo);
	}
}
