package edu.upenn.cis.ppod.model;

import java.util.Map;

import edu.upenn.cis.ppod.imodel.IOtuChangeCase;
import edu.upenn.cis.ppod.imodel.IOTUKeyedMap;
import edu.upenn.cis.ppod.imodel.IOTUKeyedMapPlus;
import edu.upenn.cis.ppod.imodel.IProteinMatrix;
import edu.upenn.cis.ppod.imodel.IProteinRow;
import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.OTUProteinRowPair;

public class ProteinRows implements IOTUKeyedMap<IProteinRow> {

	private final IOTUKeyedMapPlus<IProteinRow, IProteinMatrix, OTUProteinRowPair> rows =
			new OTUKeyedMapPlus<IProteinRow, IProteinMatrix, OTUProteinRowPair>();

	/** {@inheritDoc} */
	public void accept(final IVisitor visitor) {
		rows.accept(visitor);
	}

	/** {@inheritDoc} */
	public void afterUnmarshal() {
		rows.afterUnmarshal();
	}

	/** {@inheritDoc} */
	public IProteinRow get(final IOtuChangeCase key) {
		return rows.get(key);
	}

	/** {@inheritDoc} */
	public Map<IOtuChangeCase, IProteinRow> getValues() {
		return rows.getValues();
	}

	/** {@inheritDoc} */
	public IProteinRow put(final IOtuChangeCase key, final IProteinRow value) {
		return rows.put(key, value);
	}

	/** {@inheritDoc} */
	public void updateOTUs() {
		rows.updateOTUs();
	}
}
