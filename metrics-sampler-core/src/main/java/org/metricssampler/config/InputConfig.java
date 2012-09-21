package org.metricssampler.config;

/**
 * Base class for input configurations.
 */
public abstract class InputConfig {
	private final String name;

	public InputConfig(final String name) {
		this.name = name;
	}

	/**
	 * @return the unique (among all inputs) name of this input
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getName() + "]";
	}
}
