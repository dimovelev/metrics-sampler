package org.metricssampler.config.loader.xbeans;

import org.metricssampler.config.OutputConfig;

/**
 * Base class for output XBeans.
 */
public abstract class OutputXBean extends NamedXBean {
	public abstract OutputConfig toConfig();
}
