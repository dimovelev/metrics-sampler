package org.metricssampler.config;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

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
		this.inputs = inputs;
		this.outputs = outputs;
		this.samplers = samplers;
		this.variables = Collections.unmodifiableMap(variables);
		this.sharedResources = Collections.unmodifiableMap(sharedResources);
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
