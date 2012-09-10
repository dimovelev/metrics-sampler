package org.jmxsampler.config.loader.xbeans;

import org.jmxsampler.config.SelectorConfig;

public abstract class SimpleSelectorXBean extends SelectorXBean {
	public abstract SelectorConfig toConfig();
}
