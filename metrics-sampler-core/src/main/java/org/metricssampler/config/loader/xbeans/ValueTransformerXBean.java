package org.metricssampler.config.loader.xbeans;

import org.metricssampler.config.ValueTransformerConfig;

public abstract class ValueTransformerXBean extends XBean {
	protected abstract void validate();
	public abstract ValueTransformerConfig toConfig();
}
