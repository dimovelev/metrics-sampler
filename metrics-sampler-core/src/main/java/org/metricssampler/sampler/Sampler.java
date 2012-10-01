package org.metricssampler.sampler;

import org.metricssampler.config.SamplerConfig;

/**
 * An active component which fetches metrics from a configured input, selects and transforms them and sends them to a configured list of outputs.
 */
public interface Sampler {
	String getName();
	
	void sample();
	
	/**
	 * @return fetch metrics and return {@code true} if all selectors match at least one metric
	 */
	boolean check();
	
	SamplerConfig getConfig();
}
