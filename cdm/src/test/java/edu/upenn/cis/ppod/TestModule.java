package edu.upenn.cis.ppod;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;

import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.util.StubNewVersionInfo;
import edu.upenn.cis.ppod.util.StubQuery;
import edu.upenn.cis.ppod.util.StubSession;

/**
 * @author Sam Donnelly
 */
public class TestModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Session.class).to(StubSession.class);
		bind(Query.class).to(StubQuery.class);
		bind(INewPPodVersionInfo.IFactory.class).toProvider(
				FactoryProvider.newFactory(INewPPodVersionInfo.IFactory.class,
						StubNewVersionInfo.class));
	}
}
