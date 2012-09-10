package org.metricssampler.extensions.base.sampler;

import java.util.Collections;
import java.util.List;

import org.metricssampler.config.InputConfig;
import org.metricssampler.config.OutputConfig;
import org.metricssampler.config.Placeholder;
import org.metricssampler.config.SamplerConfig;
import org.metricssampler.config.SelectorConfig;

public class DefaultSamplerConfig extends SamplerConfig {
	private final InputConfig input;
	private final List<OutputConfig> outputs;
	private final List<SelectorConfig> selectors;
	private final List<Placeholder> placeholders;
	
	public DefaultSamplerConfig(final int interval, final boolean disabled, final InputConfig input, final List<OutputConfig> outputs, final List<SelectorConfig> selectors, final List<Placeholder> placeholders) {
		super(interval, disabled);
		this.input = input;
		this.outputs = outputs;
		this.selectors = selectors;
		this.placeholders = placeholders;
	}

	public InputConfig getReader() {
		return input;
	}

	public List<OutputConfig> getOutputs() {
		return Collections.unmodifiableList(outputs);
	}

	public List<SelectorConfig> getSelectors() {
		return Collections.unmodifiableList(selectors);
	}

	public List<Placeholder> getPlaceholders() {
		return placeholders;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + input + "->" + outputs + "]";
	}
	
	
}
