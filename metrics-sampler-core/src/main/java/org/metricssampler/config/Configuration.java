package org.metricssampler.config;

import java.util.Collection;
import java.util.Collections;

public class Configuration {
	private final int poolSize;
	private final Collection<InputConfig> inputs;
	private final Collection<OutputConfig> outputs;
	private final Collection<SamplerConfig> samplers;
	private final Collection<Placeholder> placeholders;
	
	public Configuration(final int poolSize, final Collection<InputConfig> inputs, final Collection<OutputConfig> outputs, final Collection<SamplerConfig> samplers, final Collection<Placeholder> placeholders) {
		this.poolSize = poolSize;
		this.inputs = inputs;
		this.outputs = outputs;
		this.samplers = samplers;
		this.placeholders = placeholders;
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

	public Collection<Placeholder> getPlaceholders() {
		return Collections.unmodifiableCollection(placeholders);
	}
}
