package org.jmxsampler.config.loader.xbeans;

import org.jmxsampler.config.ReaderConfig;

public abstract class ReaderXBean extends NamedXBean {
	public abstract ReaderConfig toConfig();
}
