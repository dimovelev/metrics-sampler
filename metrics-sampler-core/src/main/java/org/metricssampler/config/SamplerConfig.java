package org.metricssampler.config;

/**
 * Base class for sampler configurations.
 */
public abstract class SamplerConfig {
	private final int interval;
	private final boolean disabled;

	public SamplerConfig(final int interval, final boolean disabled) {
		this.interval = interval;
		this.disabled = disabled;
	}

	public int getInterval() {
		return interval;
	}

	public boolean isDisabled() {
		return disabled;
	}
}
