/*
 * From http://glauche.de/2009/08/24/logging-with-slf4j-and-guice/
 */
package edu.upenn.cis.ppod.thirdparty.injectslf4j;

import java.lang.reflect.Field;

import org.slf4j.Logger;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * From <a
 * href="http://glauche.de/2009/08/24/logging-with-slf4j-and-guice/">http
 * ://glauche.de/2009/08/24/logging-with-slf4j-and-guice</a>
 * 
 * @author Michael Glauche
 */
public class Slf4jTypeListener implements TypeListener {

	public <I> void hear(TypeLiteral<I> aTypeLiteral,
			TypeEncounter<I> aTypeEncounter) {

		for (Field field : aTypeLiteral.getRawType().getDeclaredFields()) {
			if (field.getType() == Logger.class
					&& field.isAnnotationPresent(InjectLogger.class)) {
				aTypeEncounter.register(new Slf4jMembersInjector<I>(field));
			}
		}
	}
}