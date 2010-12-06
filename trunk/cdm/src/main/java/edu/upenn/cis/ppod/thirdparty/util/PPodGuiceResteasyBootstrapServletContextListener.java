package edu.upenn.cis.ppod.thirdparty.util;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.shiro.web.servlet.IniShiroFilter;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

import edu.upenn.cis.ppod.PPodModule;
import edu.upenn.cis.ppod.persistence.PersistenceModule;
import edu.upenn.cis.ppod.services.hibernate.PPodServicesHibernateModule;

/**
 * Based on
 * {@link org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener}
 * .
 */
public class PPodGuiceResteasyBootstrapServletContextListener extends
		ResteasyBootstrap {

	@Override
	public void contextDestroyed(final ServletContextEvent event) {}

	@Override
	public void contextInitialized(final ServletContextEvent event) {
		super.contextInitialized(event);
		final ServletContext context = event.getServletContext();
		final Registry registry = (Registry) context
				.getAttribute(Registry.class.getName());
		final ResteasyProviderFactory providerFactory = (ResteasyProviderFactory) context
				.getAttribute(ResteasyProviderFactory.class.getName());
		final PPodModuleProcessor processor = new PPodModuleProcessor(
				registry,
				providerFactory);

		final Injector injector =
				Guice.createInjector(
						new PPodModule(),
						new PersistenceModule(),
						new PPodServicesHibernateModule(),
						new ServletModule() {

							@Override
							protected void configureServlets() {
								bind(IniShiroFilter.class)
										.in(Singleton.class);
								bind(HttpServletDispatcher.class)
										.in(Singleton.class);
								filter("/*")
										.through(IniShiroFilter.class);
								filter("/*")
										.through(
												HibernateSessionRequestFilter.class);
								serve("/*")
										.with(HttpServletDispatcher.class);
							}
						}
						);
		processor.processInjector(injector);
	}
}
