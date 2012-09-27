package org.metricssampler.config;

import static org.metricssampler.util.Preconditions.checkArgument;
import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

import java.util.Collection;
import java.util.Collections;

/**
 * The root configuration of the application
 */
public class Configuration {
	private final int poolSize;
	private final Collection<InputConfig> inputs;
	private final Collection<OutputConfig> outputs;
	private final Collection<SamplerConfig> samplers;
	private final Collection<Variable> variables;
	
	public Configuration(final int poolSize, final Collection<InputConfig> inputs, final Collection<OutputConfig> outputs, final Collection<SamplerConfig> samplers, final Collection<Variable> variables) {
		checkArgument(poolSize > 0, "pool-size must be greater than 0");
		checkArgumentNotNull(inputs, "inputs");
		checkArgumentNotNull(outputs, "outputs");
		checkArgumentNotNull(samplers, "samplers");
		checkArgumentNotNull(variables, "variables");
		this.poolSize = poolSize;
		this.inputs = inputs;
		this.outputs = outputs;
		this.samplers = samplers;
		this.variables = variables;
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

	public Collection<Variable> getVariables() {
		return Collections.unmodifiableCollection(variables);
	}
}
