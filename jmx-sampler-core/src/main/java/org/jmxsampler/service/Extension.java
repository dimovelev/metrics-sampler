package org.jmxsampler.service;

import java.util.Collection;

/**
 * SPI entry point for extensions
 */
public interface Extension {
	String getName();
	Collection<Class<?>> getXBeans();
	LocalObjectFactory getObjectFactory();
}
