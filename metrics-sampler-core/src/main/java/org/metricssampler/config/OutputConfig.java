package org.metricssampler.config;

/**
 * Base class for output configurations.
 */
public abstract class OutputConfig {
	private final String name;

	public OutputConfig(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getName() + "]";
	}
}
