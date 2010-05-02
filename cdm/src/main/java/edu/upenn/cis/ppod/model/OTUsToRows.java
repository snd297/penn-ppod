package edu.upenn.cis.ppod.model;

import java.util.Map;

/**
 * 
 * @author Sam Donnelly
 */
public abstract class OTUsToRows<R extends Row<?>> extends
		OTUKeyedMap<R> {

	protected abstract Matrix<R> getMatrix();

	@Override
	protected abstract Map<OTU, R> getOTUsToValues();


}
