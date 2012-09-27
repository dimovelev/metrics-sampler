package org.metricssampler.config;

/**
 * Base class for variables.
 */
public abstract class Variable extends NamedConfig {
	public Variable(final String name) {
		super(name);
	}
	
	public abstract Object getValue();
}
