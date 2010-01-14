/*
 * http://www.jboss.org/file-access/default/members/resteasy/freezone/docs/1.2
 */
package edu.upenn.cis.ppod.thirdparty.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.xml.bind.Marshaller;

import org.jboss.resteasy.annotations.Decorator;

/**
 * From <a href="http://www.jboss.org/file-access/default/members/resteasy/freezone/docs/1.2.GA/userguide/html_single/index.html"
 * >http://www.jboss.org/file-access/default/members/resteasy/freezone/docs/1.2.
 * GA/userguide/html_single/index.html</a>
 */
@Target( { ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER,
		ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Decorator(processor = PrettyProcessor.class, target = Marshaller.class)
public @interface Pretty {
}
