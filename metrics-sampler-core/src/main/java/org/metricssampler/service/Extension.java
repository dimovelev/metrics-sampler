package org.metricssampler.service;

import org.metricssampler.config.loader.XBeanPostProcessor;

import java.util.Collection;
import java.util.Collections;

/**
 * SPI entry point for extensions
 */
public interface Extension {
	/**
	 * @return the name of the extension
	 */
	String getName();
	
	/**
	 * Called by the application bootstrapper after loading the extension.
	 */
	void initialize();
	
	/**
	 * @return list of java beans that can be used with XStream to deserialize the configuration
	 */
	Collection<Class<?>> getXBeans();

	default Collection<XBeanPostProcessor> getXBeanPostProcessors() {
		return Collections.emptyList();
	}

	/**
	 * @return a factory for components introduced by this extension.
	 */
	LocalObjectFactory getObjectFactory();
}
