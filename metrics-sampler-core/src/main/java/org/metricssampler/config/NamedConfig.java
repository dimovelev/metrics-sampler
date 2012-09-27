package org.metricssampler.config;

import static org.metricssampler.util.Preconditions.checkArgumentNotNullNorEmpty;

/**
 * Base class for configuration options that have a mandatory name.
 */
public abstract class NamedConfig {
	private final String name;

	public NamedConfig(final String name) {
		checkArgumentNotNullNorEmpty(name, "name");
		this.name = name;
	}

	/**
	 * @return the unique (among all configuration options of the same type) name of the configuration type
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getName() + "]";
	}

}
