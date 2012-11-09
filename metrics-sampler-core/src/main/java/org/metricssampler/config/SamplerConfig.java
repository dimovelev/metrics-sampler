package org.metricssampler.config;
import static java.util.Collections.unmodifiableMap;
import static org.metricssampler.util.Preconditions.checkArgument;
import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

import java.util.Map;

/**
 * Base class for sampler configurations.
 */
public abstract class SamplerConfig extends NamedConfig {
	private final String pool;
	private final int interval;
	private final boolean ignored;
	private final boolean disabled;
	private final Map<String, Object> globalVariables;
	
	public SamplerConfig(final String name, final String pool, final int interval, final boolean ignored, final boolean disabled, final Map<String, Object> globalVariables) {
		super(name);
		checkArgumentNotNull(pool, "pool");
		checkArgument(interval > 0, "interval must be greater than 0 seconds");
		checkArgumentNotNull(globalVariables, "globalVariables");
		this.pool = pool;
		this.interval = interval;
		this.ignored = ignored;
		this.disabled = disabled;
		this.globalVariables = unmodifiableMap(globalVariables);
	}

	/**
	 * @return the sampling interval in seconds
	 */
	public int getInterval() {
		return interval;
	}

	/**
	 * @return {@code true} if the sampler will be excluded when sampling. Such samplers will not be scheduled at all.
	 */
	public boolean isIgnored() {
		return ignored;
	}

	public Map<String, Object> getGlobalVariables() {
		return globalVariables;
	}
	
	/**
	 * @return {@code true} if the sampler is temporarily disabled and will not sample but will still be scheduled.
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * @return the name of the pool where the sampler will be scheduled
	 */
	public String getPool() {
		return pool;
	}
}
