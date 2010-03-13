package edu.upenn.cis.ppod.services.ppodentity;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * @author Sam Donnelly
 * 
 */
public class MolecularSequenceSetInfo extends PPodEntityInfoWDocId {

	private Map<String, Long> sequenceVersionsByOTUDocId = newHashMap();

	@XmlElementWrapper(name = "sequenceVersionsByOTUDocId")
	public Map<String, Long> getSequenceVersionsByOTUDocId() {
		return sequenceVersionsByOTUDocId;
	}
}
