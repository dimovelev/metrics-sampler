package org.metricssampler.resources;

import java.util.Map;

/**
 * A resource that can be used by multiple components.
 */
public interface SharedResource {
	void shutdown();
	void startup();
	Map<String, Object> getStats();
}
