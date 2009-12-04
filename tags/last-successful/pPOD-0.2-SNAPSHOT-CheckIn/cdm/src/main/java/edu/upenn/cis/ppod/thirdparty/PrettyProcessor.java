/*
 * From http://www.jboss.org/file-access/default/members/resteasy/freezone/docs/1.2.GA/userguide/html_single/index.html
 */
package edu.upenn.cis.ppod.thirdparty;

import java.lang.annotation.Annotation;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.jboss.resteasy.annotations.DecorateTypes;
import org.jboss.resteasy.spi.interception.DecoratorProcessor;

/**
 * From
 * {@link "http://www.jboss.org/file-access/default/members/resteasy/freezone/docs/1.2.GA/userguide/html_single/index.html"}
 * .
 * 
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@DecorateTypes( { "text/*+xml", "application/*+xml" })
public final class PrettyProcessor implements
		DecoratorProcessor<Marshaller, Pretty> {
	public Marshaller decorate(final Marshaller target,
			final Pretty annotation, final Class type,
			final Annotation[] annotations, final MediaType mediaType) {
		try {
			target.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		} catch (final PropertyException e) {
			throw new IllegalStateException(e);
		}
		return target;
	}
}