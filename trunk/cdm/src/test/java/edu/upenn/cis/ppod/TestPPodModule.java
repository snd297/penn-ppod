package edu.upenn.cis.ppod;

import com.google.inject.AbstractModule;

import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.util.StubNewVersionInfo;

/**
 * @author Sam Donnelly
 */
public class TestPPodModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(INewPPodVersionInfo.class).to(StubNewVersionInfo.class);
	}
}
