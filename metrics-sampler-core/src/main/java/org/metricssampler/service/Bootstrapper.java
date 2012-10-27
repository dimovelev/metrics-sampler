package org.metricssampler.service;

import org.metricssampler.config.Configuration;
import org.metricssampler.sampler.Sampler;

public interface Bootstrapper extends GlobalObjectFactory {
	Configuration getConfiguration();
	Iterable<Sampler> getSamplers();
	int getControlPort();
	String getControlHost();
	void shutdown();
}
