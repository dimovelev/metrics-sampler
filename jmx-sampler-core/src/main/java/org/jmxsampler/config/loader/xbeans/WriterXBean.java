package org.jmxsampler.config.loader.xbeans;

import org.jmxsampler.config.WriterConfig;

public abstract class WriterXBean extends NamedXBean {
	public abstract WriterConfig toConfig();
}
