package org.metricssampler.service;

import java.util.Collection;

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
	
	/**
	 * @return a factory for components introduced by this extension.
	 */
	LocalObjectFactory getObjectFactory();
}
