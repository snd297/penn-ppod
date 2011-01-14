package edu.upenn.cis.ppod.model;

import java.util.Map;

import edu.upenn.cis.ppod.imodel.IOtuKeyedMap;
import edu.upenn.cis.ppod.util.IVisitor;

public class ProteinRows implements IOtuKeyedMap<ProteinRow> {

	private final OtuKeyedMapPlus<ProteinRow, ProteinMatrix> rows =
			new OtuKeyedMapPlus<ProteinRow, ProteinMatrix>();

	/** {@inheritDoc} */
	public void accept(final IVisitor visitor) {
		rows.accept(visitor);
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
