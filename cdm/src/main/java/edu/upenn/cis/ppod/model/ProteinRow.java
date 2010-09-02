package edu.upenn.cis.ppod.model;

import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import edu.upenn.cis.ppod.imodel.IProteinCell;
import edu.upenn.cis.ppod.imodel.IProteinMatrix;
import edu.upenn.cis.ppod.imodel.IProteinRow;

public class ProteinRow extends Row<IProteinCell, IProteinMatrix> implements
		IProteinRow {

	public static class Adapter extends XmlAdapter<ProteinRow, IProteinRow> {

		@Override
		public ProteinRow marshal(final IProteinRow row) {
			return (ProteinRow) row;
		}

		@Override
		public IProteinRow unmarshal(final ProteinRow row) {
			return row;
		}
	}

	public static final String TABLE = "PROTEIN_ROW";

	public IProteinMatrix getParent() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void setParent(IProteinMatrix parent) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	List<IProteinCell> getCellsModifiable() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public List<IProteinCell> setCells(List<? extends IProteinCell> cells) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
