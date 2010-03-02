package edu.upenn.cis.ppod.saveorupdate;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.MolecularSequence;
import edu.upenn.cis.ppod.model.MolecularSequenceSet;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.IUUPPodEntity;

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
		dao.saveOrUpdate(targetSequenceSet);
		final Set<S> newTargetSequences = newHashSet();
		for (final S sourceSequence : sourceSequenceSet.getSequences()) {
			S targetSequence;
			if (null == (targetSequence = findIf(targetSequenceSet
					.getSequences(), compose(
					equalTo(sourceSequence.getPPodId()),
					IUUPPodEntity.getPPodId)))) {
				targetSequence = sequenceProvider.get();
				targetSequence.setPPodId();
				targetSequence.setPPodVersionInfo(newPPodVersionInfo
						.getNewPPodVersionInfo());
				targetSequence.setSequenceSet(targetSequenceSet);
			}
			newTargetSequences.add(targetSequence);

			targetSequence.setSequence(sourceSequence.getSequence());
			targetSequence.setName(sourceSequence.getName());
			targetSequence.setDescription(sourceSequence.getDescription());
			targetSequence.setAccession(sourceSequence.getAccession());

			dao.saveOrUpdate(targetSequence);
			dao.flush();
			dao.evict(targetSequence);
		}

		// Now let's remove what's wasn't in sourceSequenceSet
		final Set<S> removedTargetSequences = targetSequenceSet
				.setSequences(newTargetSequences);
		for (final S removedSequence : removedTargetSequences) {
			dao.delete(removedSequence);
		}
	}
}
