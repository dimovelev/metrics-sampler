package org.metricssampler.sampler;

public interface Sampler {
	void sample();
	boolean check();
}
