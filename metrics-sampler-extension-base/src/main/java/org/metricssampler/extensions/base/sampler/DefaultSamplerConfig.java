package org.metricssampler.extensions.base.sampler;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

import java.util.Collections;
import java.util.List;

import org.metricssampler.config.InputConfig;
import org.metricssampler.config.OutputConfig;
import org.metricssampler.config.SamplerConfig;
import org.metricssampler.config.SelectorConfig;
import org.metricssampler.config.Variable;

public class DefaultSamplerConfig extends SamplerConfig {
	private final InputConfig input;
	private final List<OutputConfig> outputs;
	private final List<SelectorConfig> selectors;
	private final List<Variable> variables;
	private final boolean quiet;

	public DefaultSamplerConfig(final String name, final int interval, final boolean disabled, final InputConfig input,
			final List<OutputConfig> outputs, final List<SelectorConfig> selectors, final List<Variable> variables,
			final boolean quiet) {
		super(name, interval, disabled);
		checkArgumentNotNull(input, "input");
		checkArgumentNotNull(outputs, "outputs");
		checkArgumentNotNull(selectors, "selectors");
		checkArgumentNotNull(variables, "variables");
		this.input = input;
		this.outputs = outputs;
		this.selectors = selectors;
		this.variables = variables;
		this.quiet = quiet;
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

	public List<Variable> getVariables() {
		return Collections.unmodifiableList(variables);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + input + "->" + outputs + "]";
	}

	/**
	 * @return {@code true} if the sampler should not be very chatty about connectivity problems. This is useful for cases when we want to
	 *         monitor services that are not available all the time. Without setting this to {@code true} the sampler would constantly log
	 *         stack traces because it fails to open the reader.
	 */
	public boolean isQuiet() {
		return quiet;
	}

}
