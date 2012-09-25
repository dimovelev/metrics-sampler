package org.metricssampler.config;

import java.util.Collections;
import java.util.List;

/**
 * Base class for input configurations.
 */
public abstract class InputConfig {
	private final String name;
	private final List<Variable> variables;
	
	public InputConfig(final String name, final List<Variable> variables) {
		this.name = name;
		this.variables = Collections.unmodifiableList(variables);
	}

	/**
	 * @return the unique (among all inputs) name of this input
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return an unmodifiable list of variables
	 */
	public List<Variable> getVariables() {
		return variables;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getName() + "]";
	}
}
