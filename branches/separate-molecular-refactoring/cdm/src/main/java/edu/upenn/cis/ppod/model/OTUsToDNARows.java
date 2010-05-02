package edu.upenn.cis.ppod.model;

import java.util.Map;
import java.util.Set;

import edu.upenn.cis.ppod.modelinterfaces.IPPodVersionedWithOTUSet;
import edu.upenn.cis.ppod.util.OTUSomethingPair;

/**
 * @author Sam Donnelly
 * 
 */
public class OTUsToDNARows extends OTUsToRows<DNARow> {

	@Override
	protected Matrix<DNARow> getMatrix() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	protected Map<OTU, DNARow> getOTUsToValues() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	protected Set<OTUSomethingPair<DNARow>> getOTUValuePairs() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	protected IPPodVersionedWithOTUSet getParent() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	protected OTUKeyedMap<DNARow> setInNeedOfNewPPodVersionInfo() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
