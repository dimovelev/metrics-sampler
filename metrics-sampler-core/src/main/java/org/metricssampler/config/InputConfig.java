package org.metricssampler.config;

import java.util.Map;

import static java.util.Collections.unmodifiableMap;
import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

/**
 * Base class for input configurations.
 */
public abstract class InputConfig extends NamedConfig {
	private final Map<String, Object> variables;

	public InputConfig(final String name, final Map<String, Object> variables) {
		super(name);
		checkArgumentNotNull(variables, "variables");
		this.variables = unmodifiableMap(variables);
	}

	/**
	 * @return an unmodifiable map of variables by their name. This map only contains the variables defined in the configuration file
	 *         and nothing else.
	 */
	public Map<String, Object> getVariables() {
		return variables;
	}
}
