package org.metricssampler.config;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

import java.util.Collections;
import java.util.Map;

/**
 * Base class for input configurations.
 */
public abstract class InputConfig extends NamedConfig {
	private final Map<String, Object> variables;
	
	public InputConfig(final String name, final Map<String, Object> variables) {
		super(name);
		checkArgumentNotNull(variables, "variables");
		this.variables = Collections.unmodifiableMap(variables);
	}

	/**
	 * @return an unmodifiable map of variables by their name
	 */
	public Map<String, Object> getVariables() {
		return variables;
	}
}
