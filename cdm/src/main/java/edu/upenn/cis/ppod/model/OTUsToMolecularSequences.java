package edu.upenn.cis.ppod.model;

import java.util.Map;
import java.util.Set;

import edu.upenn.cis.ppod.util.OTUSomethingPair;

/**
 * @author Sam Donnelly
 * 
 */
public abstract class OTUsToMolecularSequences extends
		OTUKeyedBimap<MolecularSequence<?>, MolecularSequenceSet<?>> {

	@Override
	protected Map<OTU, MolecularSequence<?>> getOTUsToValuesModifiable() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	protected Set<OTUSomethingPair<MolecularSequence<?>>> getOTUValuePairs() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
