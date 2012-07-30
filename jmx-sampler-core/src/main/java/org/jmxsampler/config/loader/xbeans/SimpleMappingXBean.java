package org.jmxsampler.config.loader.xbeans;

import org.jmxsampler.config.MappingConfig;

public abstract class SimpleMappingXBean extends MappingXBean {
	public abstract MappingConfig toConfig();
}
