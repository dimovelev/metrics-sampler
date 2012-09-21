package org.metricssampler.config;

/**
 * Base class for sampler configurations.
 */
public abstract class SamplerConfig {
	private final String name;
	private final int interval;
	private final boolean disabled;

	public SamplerConfig(final String name, final int interval, final boolean disabled) {
		this.name = name;
		this.interval = interval;
		this.disabled = disabled;
	}

	public String getName() {
		return name;
	}

	public int getInterval() {
		return interval;
	}

	public boolean isDisabled() {
		return disabled;
	}
}
