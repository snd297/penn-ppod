package edu.upenn.cis.ppod;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.inject.AbstractModule;

import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.util.IService;
import edu.upenn.cis.ppod.util.JettyWebServer;
import edu.upenn.cis.ppod.util.StubNewVersionInfo;
import edu.upenn.cis.ppod.util.StubQuery;
import edu.upenn.cis.ppod.util.StubSession;
import edu.upenn.cis.ppod.util.WebserverService;

/**
 * @author Sam Donnelly
 */
public class TestModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Session.class).to(StubSession.class);
		bind(Query.class).to(StubQuery.class);
		bind(INewPPodVersionInfo.class).to(StubNewVersionInfo.class);
		bind(IService.class).annotatedWith(WebserverService.class).to(
				JettyWebServer.class);
	}
}
