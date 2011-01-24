package edu.upenn.cis.ppod.model;

import java.util.List;

public class ProteinRow extends Row<ProteinCell, ProteinMatrix> {

	public static final String TABLE = "PROTEIN_ROW";

	public ProteinMatrix getParent() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void setParent(ProteinMatrix parent) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	List<ProteinCell> getCellsModifiable() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCells(List<? extends ProteinCell> cells) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
