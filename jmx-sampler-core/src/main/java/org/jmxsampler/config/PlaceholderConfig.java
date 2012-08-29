package org.jmxsampler.config;

public abstract class PlaceholderConfig {
	private final String key;

	public PlaceholderConfig(final String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
	
	public abstract Object getValue();
}
