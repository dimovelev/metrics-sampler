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

	/**
	 * @return the unique (among all samplers) name of the sampler.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the sampling interval in seconds
	 */
	public int getInterval() {
		return interval;
	}

	/**
	 * @return {@code true} if the sampler should be excluded when sampling
	 */
	public boolean isDisabled() {
		return disabled;
	}
}
