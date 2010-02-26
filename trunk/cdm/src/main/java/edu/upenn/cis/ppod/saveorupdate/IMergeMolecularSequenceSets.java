package edu.upenn.cis.ppod.saveorupdate;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.MolecularSequence;
import edu.upenn.cis.ppod.model.MolecularSequenceSet;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;

/**
 * @author Sam Donnelly
 * 
 * @param <SS> the kind of {@link MolecularSequenceSet} we're operating on
 * @param <S> the kind of {@link MolecularSequence} that belongs in the sequence
 *            set
 */
public interface IMergeMolecularSequenceSets<SS extends MolecularSequenceSet<S>, S extends MolecularSequence<SS>> {

	
	public void merge(final SS targetSequenceSet, final SS sourceSequenceSet);

	static interface IFactory<SS extends MolecularSequenceSet<S>, S extends MolecularSequence<SS>> {


		IMergeMolecularSequenceSets<SS, S> create(IDAO<Object, Long> dao,
				INewPPodVersionInfo newPPodVersionInfo);
	}
}