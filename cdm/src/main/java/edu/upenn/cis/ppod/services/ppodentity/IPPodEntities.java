package edu.upenn.cis.ppod.services.ppodentity;

import java.util.Set;

import edu.upenn.cis.ppod.model.OTU;

/**
 * @author Sam Donnelly
 */
public interface IPPodEntities extends IOTUSetCentricEntities {
	Set<OTU> getOTUs();

	OTU addOTU(OTU otu);
}
