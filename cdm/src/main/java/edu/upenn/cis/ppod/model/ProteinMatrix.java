package edu.upenn.cis.ppod.model;

import java.util.List;

import javax.persistence.Entity;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import edu.upenn.cis.ppod.imodel.IOtuKeyedMap;
import edu.upenn.cis.ppod.imodel.IProteinCell;
import edu.upenn.cis.ppod.imodel.IProteinMatrix;
import edu.upenn.cis.ppod.imodel.IProteinRow;

@Entity
public class ProteinMatrix
		extends Matrix<IProteinRow, IProteinCell>
		implements IProteinMatrix {
	public static class Adapter extends
			XmlAdapter<ProteinMatrix, IProteinMatrix> {

		@Override
		public ProteinMatrix marshal(final IProteinMatrix matrix) {
			return (ProteinMatrix) matrix;
		}

		@Override
		public IProteinMatrix unmarshal(final ProteinMatrix matrix) {
			return matrix;
		}
	}

	public List<IProteinCell> removeColumn(int columnNo) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	IOtuKeyedMap<IProteinRow> getOTUKeyedRows() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
}
