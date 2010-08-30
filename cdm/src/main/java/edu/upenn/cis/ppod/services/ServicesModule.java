package edu.upenn.cis.ppod.services;

import org.apache.shiro.web.servlet.IniShiroFilter;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

import edu.upenn.cis.ppod.thirdparty.util.HibernateSessionPerRequestFilter;

public class ServicesModule extends ServletModule {
	@Override
	protected void configureServlets() {
		bind(IniShiroFilter.class).in(Singleton.class);
		bind(HttpServletDispatcher.class).in(Singleton.class);

		filter("/*").through(IniShiroFilter.class);
		filter("/*").through(
				HibernateSessionPerRequestFilter.class);
		serve("/*")
				.with(HttpServletDispatcher.class);
	}
}
