package org.metricssampler.extensions.base.sampler;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.metricssampler.config.InputConfig;
import org.metricssampler.config.OutputConfig;
import org.metricssampler.config.SamplerConfig;
import org.metricssampler.config.SelectorConfig;

public class DefaultSamplerConfig extends SamplerConfig {
	private final InputConfig input;
	private final List<OutputConfig> outputs;
	private final List<SelectorConfig> selectors;
	private final Map<String, Object> variables;
	private final Map<String, Object> globalVariables;
	private final boolean quiet;

	public DefaultSamplerConfig(final String name, final int interval, final boolean disabled, final InputConfig input,
			final List<OutputConfig> outputs, final List<SelectorConfig> selectors, final Map<String, Object> variables,
			final Map<String, Object> globalVariables, final boolean quiet) {
		super(name, interval, disabled, globalVariables);
		checkArgumentNotNull(input, "input");
		checkArgumentNotNull(outputs, "outputs");
		checkArgumentNotNull(selectors, "selectors");
		checkArgumentNotNull(variables, "variables");
		checkArgumentNotNull(globalVariables, "globalVariables");
		this.input = input;
		this.outputs = outputs;
		this.selectors = selectors;
		this.variables = Collections.unmodifiableMap(variables);
		this.globalVariables = Collections.unmodifiableMap(globalVariables);
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

	public Map<String, Object> getVariables() {
		return variables;
	}

	@Override
	public Map<String, Object> getGlobalVariables() {
		return globalVariables;
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
