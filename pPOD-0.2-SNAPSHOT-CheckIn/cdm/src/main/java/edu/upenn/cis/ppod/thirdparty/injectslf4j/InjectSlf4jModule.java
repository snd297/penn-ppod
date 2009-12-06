/*
 * From http://glauche.de/2009/08/24/logging-with-slf4j-and-guice/
 */
package edu.upenn.cis.ppod.thirdparty.injectslf4j;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 * From <a
 * href="http://glauche.de/2009/08/24/logging-with-slf4j-and-guice/">http
 * ://glauche.de/2009/08/24/logging-with-slf4j-and-guice</a>
 * 
 * @author Michael Glauche
 */
public class InjectSlf4jModule extends AbstractModule {
	@Override
	protected void configure() {
		bindListener(Matchers.any(), new Slf4jTypeListener());
	}
}
