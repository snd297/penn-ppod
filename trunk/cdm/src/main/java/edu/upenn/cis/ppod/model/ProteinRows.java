package edu.upenn.cis.ppod.model;

import java.util.Map;

import edu.upenn.cis.ppod.imodel.IOtuKeyedMap;
import edu.upenn.cis.ppod.imodel.IOtuKeyedMapPlus;
import edu.upenn.cis.ppod.imodel.IProteinMatrix;
import edu.upenn.cis.ppod.imodel.IProteinRow;
import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.OtuProteinRowPair;

public class ProteinRows implements IOtuKeyedMap<IProteinRow> {

	private final IOtuKeyedMapPlus<IProteinRow, IProteinMatrix, OtuProteinRowPair> rows =
			new OtuKeyedMapPlus<IProteinRow, IProteinMatrix, OtuProteinRowPair>();

	/** {@inheritDoc} */
	public void accept(final IVisitor visitor) {
		rows.accept(visitor);
	}

	/** {@inheritDoc} */
	public void afterUnmarshal() {
		rows.afterUnmarshal();
	}

	/** {@inheritDoc} */
	public IProteinRow get(final Otu key) {
		return rows.get(key);
	}

	/** {@inheritDoc} */
	public Map<Otu, IProteinRow> getValues() {
		return rows.getValues();
	}

	/** {@inheritDoc} */
	public IProteinRow put(final Otu key, final IProteinRow value) {
		return rows.put(key, value);
	}

	/** {@inheritDoc} */
	public void updateOtus() {
		rows.updateOtus();
	}
}
