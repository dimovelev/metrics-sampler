package org.metricssampler.extensions.base;

import org.metricssampler.config.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

public class DefaultSamplerConfig extends SamplerConfig {
	private final InputConfig input;
	private final List<OutputConfig> outputs;
	private final List<SelectorConfig> selectors;
	private final Map<String, Object> variables;
	private final Map<String, Object> globalVariables;
	private final boolean quiet;
    private final int initialResetTimeout;
    private final int regularResetTimeout;

	public DefaultSamplerConfig(final String name, final String pool, final int interval, final boolean ignored, final boolean disabled, final InputConfig input,
			final List<OutputConfig> outputs, final List<SelectorConfig> selectors, final Map<String, Object> variables,
			final Map<String, Object> globalVariables, final List<ValueTransformerConfig> valueTransformers, final boolean quiet, final int initialResetTimeout, final int regularResetTimeout) {
		super(name, pool, interval, ignored, disabled, globalVariables, valueTransformers);
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
        this.initialResetTimeout = initialResetTimeout;
        this.regularResetTimeout = regularResetTimeout;
	}

	public InputConfig getInput() {
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

    /**
     * @return the timeout in seconds that the sampler will wait after successfully connecting the input to reset so that the matched JMX beans are reloaded. This will be
     * repeated until the number of the matched beans remains unchanged for two consecutive reconnects.
     */
    public int getInitialResetTimeout() {
        return initialResetTimeout;
    }

    /**
     * @return the timeout in seconds that the sampler will wait after successfully connecting the input to reset so that the matched JMX beans are reloaded. This will be
     * done on a regular basis.
     */
    public int getRegularResetTimeout() {
        return regularResetTimeout;
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
