package org.metricssampler.service;

import org.metricssampler.util.StringUtils;

/**
 * Base class for extensions. This is a combination of the extension and the local object factory so that an extension is only required to extend one single class.
 */
public abstract class AbstractExtension extends AbstractLocalObjectFactory implements Extension {
	protected static final String DEFAULT_EXTENSION_SUFFIX = "Extension";

	@Override
	public LocalObjectFactory getObjectFactory() {
		return this;
	}

	@Override
	public void initialize() {
		// do not do anything by default
	}

	@Override
	public String getName() {
		final String className = getClass().getSimpleName();
		final String name = className.endsWith(DEFAULT_EXTENSION_SUFFIX) ? className.substring(0, className.length() - DEFAULT_EXTENSION_SUFFIX.length()) : className; 
		return StringUtils.camelCaseToSplit(name, "-");
	}
}
