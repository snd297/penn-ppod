package edu.upenn.cis.ppod.thirdparty.util;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.shiro.web.servlet.IniShiroFilter;
import org.hibernate.SessionFactory;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.persistence.PPodPersistenceModule;
import edu.upenn.cis.ppod.services.PPodExceptionMapper;
import edu.upenn.cis.ppod.services.PPodServicesHibernateModule;

/**
 * Based on
 * {@link org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener}
 * .
 */
public class PPodGuiceResteasyBootstrap
		extends ResteasyBootstrap {

	private static Logger logger =
			LoggerFactory
					.getLogger(PPodGuiceResteasyBootstrap.class);

	@CheckForNull
	private SessionFactory sessionFactory;

	private final Injector injector;

	public PPodGuiceResteasyBootstrap() {
		this(
				Guice.createInjector(
						new PPodPersistenceModule(),
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
								serve("/*")
										.with(HttpServletDispatcher.class);
							}
						}
						));
	}

	public PPodGuiceResteasyBootstrap(final Injector injector) {
		this.injector = injector;
	}

	@Override
	public void contextDestroyed(final ServletContextEvent event) {
		logger.debug("shutting down hibernate...");
		if (sessionFactory != null) {
			sessionFactory.close();
		}
		logger.debug("done");
	}

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

		processor.processInjector(injector);
		sessionFactory = injector.getInstance(SessionFactory.class);
		providerFactory.registerProviderInstance(new PPodExceptionMapper());
	}
}
