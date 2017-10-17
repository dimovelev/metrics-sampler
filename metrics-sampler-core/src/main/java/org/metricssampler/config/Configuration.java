package org.metricssampler.config;

import java.util.Collection;
import java.util.Map;

import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableMap;
import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

/**
 * The root configuration of the application
 */
public class Configuration {
	private final Collection<InputConfig> inputs;
	private final Collection<OutputConfig> outputs;
	private final Collection<SamplerConfig> samplers;
	private final Map<String, Object> variables;
	private final Map<String, SharedResourceConfig> sharedResources;
	
	public Configuration(final Collection<InputConfig> inputs, final Collection<OutputConfig> outputs, final Collection<SamplerConfig> samplers, final Map<String, Object> variables, final Map<String, SharedResourceConfig> sharedResources) {
		checkArgumentNotNull(inputs, "inputs");
		checkArgumentNotNull(outputs, "outputs");
		checkArgumentNotNull(samplers, "samplers");
		checkArgumentNotNull(variables, "variables");
		checkArgumentNotNull(sharedResources, "sharedResources");
		this.inputs = unmodifiableCollection(inputs);
		this.outputs = unmodifiableCollection(outputs);
		this.samplers = unmodifiableCollection(samplers);
		this.variables = unmodifiableMap(variables);
		this.sharedResources = unmodifiableMap(sharedResources);
	}

	public Collection<InputConfig> getInputs() {
		return inputs;
	}

	public Collection<OutputConfig> getOutputs() {
		return outputs;
	}

	public Collection<SamplerConfig> getSamplers() {
		return samplers;
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
