package org.metricssampler.config;

public class StringPlaceholder extends Placeholder {
	private final String value;

	public StringPlaceholder(final String key, final String value) {
		super(key);
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}
}
