package edu.upenn.cis.ppod.model;

import java.util.Map;

import edu.upenn.cis.ppod.imodel.IOtuKeyedMap;
import edu.upenn.cis.ppod.imodel.IOtuKeyedMapPlus;
import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.OtuProteinRowPair;

public class ProteinRows implements IOtuKeyedMap<ProteinRow> {

	private final IOtuKeyedMapPlus<ProteinRow, ProteinMatrix, OtuProteinRowPair> rows =
			new OtuKeyedMapPlus<ProteinRow, ProteinMatrix, OtuProteinRowPair>();

	/** {@inheritDoc} */
	public void accept(final IVisitor visitor) {
		rows.accept(visitor);
	}

	/** {@inheritDoc} */
	public void afterUnmarshal() {
		rows.afterUnmarshal();
	}

	/** {@inheritDoc} */
	public ProteinRow get(final Otu key) {
		return rows.get(key);
	}

	/** {@inheritDoc} */
	public Map<Otu, ProteinRow> getValues() {
		return rows.getValues();
	}

	/** {@inheritDoc} */
	public ProteinRow put(final Otu key, final ProteinRow value) {
		return rows.put(key, value);
	}

	/** {@inheritDoc} */
	public void updateOtus() {
		rows.updateOtus();
	}
}
