package org.metricssampler.service;

import org.metricssampler.config.Configuration;
import org.metricssampler.resources.SharedResource;
import org.metricssampler.sampler.Sampler;

import java.util.Map;

public interface Bootstrapper extends GlobalObjectFactory {
	Configuration getConfiguration();
	Iterable<Sampler> getSamplers();
	int getControlPort();
	String getControlHost();
	void shutdown();
	Map<String, SharedResource> getSharedResources();
}
