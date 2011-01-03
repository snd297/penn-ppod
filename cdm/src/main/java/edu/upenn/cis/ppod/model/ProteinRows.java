package edu.upenn.cis.ppod.model;

import java.util.Map;

import edu.upenn.cis.ppod.imodel.IOtu;
import edu.upenn.cis.ppod.imodel.IOtuKeyedMap;
import edu.upenn.cis.ppod.imodel.IOtuKeyedMapPlus;
import edu.upenn.cis.ppod.imodel.IProteinMatrix;
import edu.upenn.cis.ppod.imodel.IProteinRow;
import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.OTUProteinRowPair;

public class ProteinRows implements IOtuKeyedMap<IProteinRow> {

	private final IOtuKeyedMapPlus<IProteinRow, IProteinMatrix, OTUProteinRowPair> rows =
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
	public IProteinRow get(final IOtu key) {
		return rows.get(key);
	}

	/** {@inheritDoc} */
	public Map<IOtu, IProteinRow> getValues() {
		return rows.getValues();
	}

	/** {@inheritDoc} */
	public IProteinRow put(final IOtu key, final IProteinRow value) {
		return rows.put(key, value);
	}

	/** {@inheritDoc} */
	public void updateOTUs() {
		rows.updateOTUs();
	}
}
