package org.metricssampler.service;

import org.metricssampler.resources.SharedResource;

/**
 * A factory with access to all extensions
 */
public interface GlobalObjectFactory extends ObjectFactory {
	SharedResource getSharedResource(String name);
}
