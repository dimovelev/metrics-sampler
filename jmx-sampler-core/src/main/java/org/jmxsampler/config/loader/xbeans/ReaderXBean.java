package org.jmxsampler.config.loader.xbeans;

import org.jmxsampler.config.ConfigurationException;
import org.jmxsampler.config.ReaderConfig;

public abstract class ReaderXBean extends TemplatableXBean {
	public ReaderConfig toConfig() {
		if (isAbstract()) {
			throw new ConfigurationException("Tried to use abstract bean \"" + getName() + "\"");
		}
		validate();
		return createConfig();
	}
	
	protected abstract ReaderConfig createConfig();
}
