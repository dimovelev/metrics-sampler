package org.metricssampler.config;
import static org.metricssampler.util.Preconditions.checkArgument;

/**
 * Base class for sampler configurations.
 */
public abstract class SamplerConfig extends NamedConfig {
	private final int interval;
	private final boolean disabled;

	public SamplerConfig(final String name, final int interval, final boolean disabled) {
		super(name);
		checkArgument(interval > 0, "interval must be greater than 0 seconds");
		this.interval = interval;
		this.disabled = disabled;
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
