package org.metricssampler.config;
import static org.metricssampler.util.Preconditions.checkArgument;
import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

import java.util.Collections;
import java.util.Map;

/**
 * Base class for sampler configurations.
 */
public abstract class SamplerConfig extends NamedConfig {
	private final int interval;
	private final boolean disabled;
	private final Map<String, Object> globalVariables;
	
	public SamplerConfig(final String name, final int interval, final boolean disabled, final Map<String, Object> globalVariables) {
		super(name);
		checkArgument(interval > 0, "interval must be greater than 0 seconds");
		checkArgumentNotNull(globalVariables, "globalVariables");
		this.interval = interval;
		this.disabled = disabled;
		this.globalVariables = Collections.unmodifiableMap(globalVariables);
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

	public Map<String, Object> getGlobalVariables() {
		return globalVariables;
	}
	
	
}
