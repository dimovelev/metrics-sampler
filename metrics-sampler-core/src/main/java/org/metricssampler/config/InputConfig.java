package org.metricssampler.config;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

import java.util.Collections;
import java.util.List;

/**
 * Base class for input configurations.
 */
public abstract class InputConfig extends NamedConfig {
	private final List<Variable> variables;
	
	public InputConfig(final String name, final List<Variable> variables) {
		super(name);
		checkArgumentNotNull(variables, "variables");
		this.variables = Collections.unmodifiableList(variables);
	}

	/**
	 * @return an unmodifiable list of variables
	 */
	public List<Variable> getVariables() {
		return variables;
	}
}
