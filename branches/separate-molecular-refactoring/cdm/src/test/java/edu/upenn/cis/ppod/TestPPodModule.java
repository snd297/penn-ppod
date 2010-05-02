package edu.upenn.cis.ppod;

import com.google.inject.AbstractModule;

import edu.upenn.cis.ppod.util.TestPPodUtilModule;

/**
 * @author Sam Donnelly
 */
public class TestPPodModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new TestPPodUtilModule());
	}
}
