package org.jmxsampler.config.loader.xbeans;

import org.jmxsampler.config.OutputConfig;

/**
 * Base class for output XBeans.
 */
public abstract class OutputXBean extends NamedXBean {
	public abstract OutputConfig toConfig();
}
