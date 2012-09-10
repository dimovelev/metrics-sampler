package org.metricssampler.config.loader.xbeans;

import org.metricssampler.config.SelectorConfig;

public abstract class SimpleSelectorXBean extends SelectorXBean {
	public abstract SelectorConfig toConfig();
}
