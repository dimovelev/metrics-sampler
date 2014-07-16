package org.metricssampler.sampler;

import org.metricssampler.config.SamplerConfig;

import java.util.Set;

/**
 * An active component which fetches metrics from a configured input, selects and transforms them and sends them to a configured list of outputs.
 */
public interface Sampler {
	/**
	 * fetch metrics from the input, select appropriate metrics, transform them and send them to the list of outputs. 
	 */
	void sample();
	
	/**
	 * @return fetch metrics and return {@code true} if all selectors match at least one metric
	 */
	boolean check();

    Set<String> metrics();

	SamplerConfig getConfig();
	
	void reset();
}
