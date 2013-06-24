package org.metricssampler.service;

import java.util.Map;

import org.metricssampler.config.Configuration;
import org.metricssampler.resources.SharedResource;
import org.metricssampler.sampler.Sampler;

public interface Bootstrapper extends GlobalObjectFactory {
	Configuration getConfiguration();
	Iterable<Sampler> getSamplers();
	int getControlPort();
	String getControlHost();
	void shutdown();
	Map<String, SharedResource> getSharedResources();
}
