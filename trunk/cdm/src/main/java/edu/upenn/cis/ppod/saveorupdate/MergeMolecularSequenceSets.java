package edu.upenn.cis.ppod.saveorupdate;

import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.model.MolecularSequence;
import edu.upenn.cis.ppod.model.MolecularSequenceSet;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.IUUPPodEntity;

/**
 * @author Sam Donnelly
 * 
 */
public class MergeMolecularSequenceSets<SS extends MolecularSequenceSet<S>, S extends MolecularSequence<MolecularSequenceSet<?>>> {

	private final Provider<S> sequenceProvider;
	private final INewPPodVersionInfo newPPodVersionInfo;

	@Inject
	MergeMolecularSequenceSets(final Provider<S> sequenceProvider,
			@Assisted INewPPodVersionInfo newPPodVersionInfo) {
		this.sequenceProvider = sequenceProvider;
		this.newPPodVersionInfo = newPPodVersionInfo;
	}

	void merge(final SS targetSequenceSet, final SS sourceSequenceSet) {
		for (final S sourceSequence : sourceSequenceSet.getSequences()) {
			S targetSequence;
			if (null == (targetSequence = findIf(targetSequenceSet
					.getSequences(), compose(
					equalTo(sourceSequence.getPPodId()),
					IUUPPodEntity.getPPodId)))) {
				targetSequence = sequenceProvider.get();
				targetSequence.setPPodVersionInfo(newPPodVersionInfo
						.getNewPPodVersionInfo());
			}
			targetSequence.setName(sourceSequence.getName());
			targetSequence.setDescription(sourceSequence.getDescription());
			targetSequence.setAccession(sourceSequence.getAccession());
			
		}
	}
}
