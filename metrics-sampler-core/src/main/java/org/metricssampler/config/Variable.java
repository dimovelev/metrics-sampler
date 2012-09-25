package org.metricssampler.config;

/**
 * Base class for variables.
 */
public abstract class Variable {
	private final String name;

	public Variable(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public abstract Object getValue();
}
