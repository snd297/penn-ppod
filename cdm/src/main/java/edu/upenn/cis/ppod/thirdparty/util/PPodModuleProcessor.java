package edu.upenn.cis.ppod.thirdparty.util;

import java.lang.reflect.Type;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.guice.GuiceResourceFactory;
import org.jboss.resteasy.plugins.guice.ModuleProcessor;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Binding;
import com.google.inject.Injector;

/**
 * Based on {@link ModuleProcessor}.
 */
public class PPodModuleProcessor {
	private final static Logger logger = LoggerFactory
			.getLogger(ModuleProcessor.class);

	private final Registry registry;
	private final ResteasyProviderFactory providerFactory;

	public PPodModuleProcessor(
			final Registry registry,
			final ResteasyProviderFactory providerFactory) {
		this.registry = registry;
		this.providerFactory = providerFactory;
	}

	public void processInjector(final Injector injector) {
		for (final Binding<?> binding : injector.getBindings().values()) {
			final Type type = binding.getKey().getTypeLiteral().getType();
			if (type instanceof Class) {
				final Class<?> beanClass = (Class<?>) type;
				if (GetRestful.isRootResource(beanClass)) {
					final ResourceFactory resourceFactory = new GuiceResourceFactory(
							binding.getProvider(), beanClass);
					logger.info("registering factory for {}", beanClass);
					registry.addResourceFactory(resourceFactory);
				}
				if (beanClass.isAnnotationPresent(Provider.class)) {
					logger.info("registering provider instance for {}",
							beanClass);
					providerFactory.registerProviderInstance(binding
							.getProvider().get());
				}
			}
		}
	}
}
