package edu.upenn.cis.ppod.util;

import com.google.inject.AbstractModule;

import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;

/**
 * @author Sam Donnelly
 * 
 */
public class TestPPodUtilModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(INewPPodVersionInfo.class).to(StubNewVersionInfo.class);
	}

}
