package org.metricssampler.resources;

import java.util.Map;

import org.metricssampler.sampler.Sampler;

public interface SamplerThreadPool extends SharedResource  {
	/**
	 * Schedule the sampler in the thread pool
	 * @param sampler
	 * @return the actual runnable task that can be used to control the sampler
	 */
	SamplerTask schedule(Sampler sampler);
	
	String getName();

	Map<String, Object> getStats();
}