package org.jmxsampler.config.loader.xbeans;

import org.jmxsampler.config.ConfigurationException;
import org.jmxsampler.config.InputConfig;

/**
 * Base class for input XBeans.
 */
public abstract class InputXBean extends TemplatableXBean {
	public InputConfig toConfig() {
		if (isAbstract()) {
			throw new ConfigurationException("Tried to use abstract bean \"" + getName() + "\"");
		}
		validate();
		return createConfig();
	}
	
	protected abstract InputConfig createConfig();
}
