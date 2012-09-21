package org.metricssampler.config;

/**
 * Base class for placeholders.
 */
public abstract class Placeholder {
	private final String key;

	public Placeholder(final String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
	
	public abstract Object getValue();
}
