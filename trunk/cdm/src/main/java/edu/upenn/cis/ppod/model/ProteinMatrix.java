package edu.upenn.cis.ppod.model;

import java.util.List;

import javax.persistence.Entity;

import edu.upenn.cis.ppod.imodel.IOtuKeyedMap;

@Entity
public class ProteinMatrix
		extends Matrix<ProteinRow, ProteinCell> {

	public List<ProteinCell> removeColumn(int columnNo) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	IOtuKeyedMap<ProteinRow> getOTUKeyedRows() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
}
