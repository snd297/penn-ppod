package edu.upenn.cis.ppod.model;

import java.util.Iterator;
import java.util.List;

import edu.upenn.cis.ppod.modelinterfaces.IMatrix;

/**
 * @author Sam Donnelly
 */
public class DNARow extends MolecularStateRow<DNACell> {

	@Override
	protected List<DNACell> getCells() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	protected IMatrix getMatrix() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public List<DNACell> setCells(List<? extends DNACell> cells) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Iterator<DNACell> iterator() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
