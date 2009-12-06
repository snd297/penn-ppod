/*
 * From http://glauche.de/2009/08/24/logging-with-slf4j-and-guice/
 */
package edu.upenn.cis.ppod.thirdparty.injectslf4j;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * From <a
 * href="http://glauche.de/2009/08/24/logging-with-slf4j-and-guice/">http
 * ://glauche.de/2009/08/24/logging-with-slf4j-and-guice</a>
 * 
 * @author Michael Glauche
 */
@Target( { FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectLogger {

}
