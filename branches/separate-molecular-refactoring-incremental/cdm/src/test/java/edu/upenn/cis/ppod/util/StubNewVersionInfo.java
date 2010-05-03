package edu.upenn.cis.ppod.util;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.model.PPodVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;

/**
 * Return an empty {@link PPodVersionInfo}
 * 
 * @author Sam Donnelly
 */
class StubNewVersionInfo implements INewPPodVersionInfo {

	private final PPodVersionInfo newPPodVersionInfo;

	@Inject
	StubNewVersionInfo(final PPodVersionInfo newPPodVersionInfo) {
		this.newPPodVersionInfo = newPPodVersionInfo;
	}

	public PPodVersionInfo getNewPPodVersionInfo() {
		return newPPodVersionInfo;
	}
}
