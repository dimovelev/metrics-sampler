package org.metricssampler.config;

import static org.metricssampler.util.Preconditions.checkArgument;
import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * The root configuration of the application
 */
public class Configuration {
	private final int poolSize;
	private final Collection<InputConfig> inputs;
	private final Collection<OutputConfig> outputs;
	private final Collection<SamplerConfig> samplers;
	private final Map<String, Object> variables;
	private final Map<String, SharedResourceConfig> sharedResources;
	
	public Configuration(final int poolSize, final Collection<InputConfig> inputs, final Collection<OutputConfig> outputs, final Collection<SamplerConfig> samplers, final Map<String, Object> variables, final Map<String, SharedResourceConfig> sharedResources) {
		checkArgument(poolSize > 0, "pool-size must be greater than 0");
		checkArgumentNotNull(inputs, "inputs");
		checkArgumentNotNull(outputs, "outputs");
		checkArgumentNotNull(samplers, "samplers");
		checkArgumentNotNull(variables, "variables");
		checkArgumentNotNull(sharedResources, "sharedResources");
		this.poolSize = poolSize;
		this.inputs = inputs;
		this.outputs = outputs;
		this.samplers = samplers;
		this.variables = Collections.unmodifiableMap(variables);
		this.sharedResources = Collections.unmodifiableMap(sharedResources);
	}

	/**
	 * @return the size of samplers' thread-pool
	 */
	public int getPoolSize() {
		return poolSize;
	}

	public Collection<InputConfig> getInputs() {
		return Collections.unmodifiableCollection(inputs);
	}

	public Collection<OutputConfig> getOutputs() {
		return Collections.unmodifiableCollection(outputs);
	}

	public Collection<SamplerConfig> getSamplers() {
		return Collections.unmodifiableCollection(samplers);
	}

	public Map<String, SharedResourceConfig> getSharedResources() {
		return sharedResources;
	}

	/**
	 * @return an unmodifiable map of the global variables
	 */
	public Map<String, Object> getVariables() {
		return variables;
	}
}
