package org.metricssampler.sampler;

/**
 * An active component which fetches metrics from a configured input, selects and transforms them and sends them to a configured list of outputs.
 */
public interface Sampler {
	void sample();
	
	/**
	 * @return fetch metrics and return {@code true} if all selectors match at least one metric
	 */
	boolean check();
	
	/**
	 * @return the sampling rate in seconds
	 */
	int getRate();
}
