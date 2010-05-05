package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Sam Donnelly
 * 
 */
public abstract class OTUsToRows<R extends Row<?>> extends
		OTUKeyedMap<R> {

	@Override
	public R put(final OTU key, final R row) {
		checkNotNull(key);
		checkNotNull(row);
		final R originalRow = super.put(key, row);

		// If we are replacing an OTU's sequence, we need to sever the previous
		// sequence's sequence->sequenceSet pointer.
		if (originalRow != null && !originalRow.equals(row)) {
			originalRow.unsetOTUsToRows();
		}
		return originalRow;
	}
}
