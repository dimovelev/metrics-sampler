package org.metricssampler.config;

import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static org.metricssampler.util.Preconditions.checkArgument;
import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

/**
 * Base class for sampler configurations.
 */
public abstract class SamplerConfig extends NamedConfig {
	private final String pool;
	private final int interval;
	private final boolean ignored;
	private final boolean disabled;
	private final Map<String, Object> globalVariables;
	private final List<ValueTransformerConfig> valueTransformers;
	
	public SamplerConfig(final String name, final String pool, final int interval, final boolean ignored, final boolean disabled, final Map<String, Object> globalVariables, final List<ValueTransformerConfig> valueTransformers) {
		super(name);
		checkArgumentNotNull(pool, "pool");
		checkArgument(interval > 0, "interval must be greater than 0 seconds");
		checkArgumentNotNull(globalVariables, "globalVariables");
		checkArgumentNotNull(valueTransformers, "valueTransformers");
		this.pool = pool;
		this.interval = interval;
		this.ignored = ignored;
		this.disabled = disabled;
		this.globalVariables = unmodifiableMap(globalVariables);
		this.valueTransformers = unmodifiableList(valueTransformers);
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
	
	public List<ValueTransformerConfig> getValueTransformers() {
		return valueTransformers;
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
