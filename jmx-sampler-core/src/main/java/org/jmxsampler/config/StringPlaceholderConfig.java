package org.jmxsampler.config;

public class StringPlaceholderConfig extends PlaceholderConfig {
	private final String value;

	public StringPlaceholderConfig(final String key, final String value) {
		super(key);
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}
}
