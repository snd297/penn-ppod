package edu.upenn.cis.ppod.util;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.model.VersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.INewVersionInfo;

/**
 * Return an empty {@link PPodVersionInfo}
 * 
 * @author Sam Donnelly
 */
class StubNewVersionInfo implements INewVersionInfo {

	private final VersionInfo newPPodVersionInfo;

	@Inject
	StubNewVersionInfo(final VersionInfo newPPodVersionInfo) {
		this.newPPodVersionInfo = newPPodVersionInfo;
	}

	public VersionInfo getNewVersionInfo() {
		return newPPodVersionInfo;
	}
}
