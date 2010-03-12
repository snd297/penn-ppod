package edu.upenn.cis.ppod.saveorupdate;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.MolecularSequence;
import edu.upenn.cis.ppod.model.MolecularSequenceSet;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;

/**
 * @author Sam Donnelly
 */
public class MergeMolecularSequenceSets<SS extends MolecularSequenceSet<S>, S extends MolecularSequence<SS>>
		implements IMergeMolecularSequenceSets<SS, S> {

	final IDAO<Object, Long> dao;
	final Provider<S> sequenceProvider;
	final INewPPodVersionInfo newPPodVersionInfo;

	@Inject
	MergeMolecularSequenceSets(final Provider<S> sequenceProvider,
			@Assisted IDAO<Object, Long> dao,
			@Assisted INewPPodVersionInfo newPPodVersionInfo) {
		this.sequenceProvider = sequenceProvider;
		this.dao = dao;
		this.newPPodVersionInfo = newPPodVersionInfo;
	}

	public void merge(final SS targetSequenceSet, final SS sourceSequenceSet) {
		checkNotNull(targetSequenceSet);
		checkNotNull(sourceSequenceSet);
		targetSequenceSet.setLabel(sourceSequenceSet.getLabel());
		dao.saveOrUpdate(targetSequenceSet);

		for (int i = 0; i < sourceSequenceSet.getOTUSet().getOTUs()
				.size(); i++) {
			final OTU sourceOTU = sourceSequenceSet.getOTUSet().getOTUs()
					.get(i);

			final S sourceSequence = sourceSequenceSet.getSequence(sourceOTU);
			final OTU targetOTU = targetSequenceSet.getOTUSet().getOTUs().get(
					i);

			S targetSequence;

			if (null == (targetSequence = targetSequenceSet
					.getSequence(targetOTU))) {
				targetSequence = sequenceProvider.get();
				targetSequence.setPPodVersionInfo(newPPodVersionInfo
						.getNewPPodVersionInfo());
			}
			targetSequence.setSequence(sourceSequence.getSequence());
			targetSequenceSet.putSequence(targetOTU, targetSequence);
			targetSequence.setName(sourceSequence.getName());
			targetSequence.setDescription(sourceSequence.getDescription());
			targetSequence.setAccession(sourceSequence.getAccession());

			dao.saveOrUpdate(targetSequence);
			dao.flush();
			dao.evict(targetSequence);
		}
	}

}
