package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Sam Donnelly
 * 
 */
public abstract class OTUsToMolecularSequences<T extends MolecularSequence<?>, P extends MolecularSequenceSet<?>>
		extends OTUKeyedBimap<T, P> {

	@Override
	protected T putHelper(final OTU key,
			final T value,
			final P parent) {
		checkNotNull(value);
		final T originalSequence = super.putHelper(key, value,
				parent);
		if (originalSequence != null && !originalSequence.equals(value)) {
			originalSequence.setSequenceSet(null);
		}
		return originalSequence;
	}

}
